package com.hellogerman.app.data.dictionary

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.RandomAccessFile
import java.util.zip.GZIPInputStream

/**
 * Lightweight FreeDict reader for dictd dictionaries (.dict.dz + .index)
 * - Decompresses .dict.dz on first use to internal storage for fast random access
 * - Parses .index and provides exact lookup and basic prefix suggestions
 * - Extracts basic translations and grammar hints (article/gender) heuristically
 */
class FreedictReader(
    private val context: Context,
    private val assetDir: String, // e.g. "freedict-deu-eng-1.9-fd1.dictd/deu-eng"
    private val id: String // e.g. "deu-eng" or "eng-deu"
) {

    private val cacheDir: File = File(context.filesDir, "freedict")
    private val dictFile: File = File(cacheDir, "$id.dict")
    // Keep memory footprint low; build suggestions index lazily on demand
    private val index: MutableMap<String, IndexEntry> = HashMap()
    @Volatile private var sortedKeys: List<String>? = null
    @Volatile private var initialized: Boolean = false

    data class IndexEntry(val offset: Long, val length: Int)

    data class Entry(
        val headword: String,
        val raw: String,
        val translations: List<String>,
        val gender: String? = null
    )

    @Synchronized
    fun initializeIfNeeded() {
        if (initialized) return
        cacheDir.mkdirs()
        ensureDecompressedDict()
        loadIndex()
        initialized = true
    }

    fun clearCache() {
        if (dictFile.exists()) dictFile.delete()
        index.clear()
        sortedKeys = null
        initialized = false
    }

    fun size(): Int {
        if (!initialized) initializeIfNeeded()
        return index.size
    }

    fun suggest(prefix: String, limit: Int = 20): List<String> {
        if (!initialized) initializeIfNeeded()
        if (prefix.isBlank()) return emptyList()
        val p = prefix.lowercase()
        // Build sorted keys lazily only when suggestions are requested
        var keys = sortedKeys
        if (keys == null) {
            keys = index.keys.map { it }.sorted()
            sortedKeys = keys
        }
        // Binary search on sorted key snapshot
        var lo = 0
        var hi = keys.size
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            val cmp = keys[mid].compareTo(p)
            if (cmp < 0) lo = mid + 1 else hi = mid
        }
        val suggestions = ArrayList<String>(limit)
        var i = lo
        while (i < keys.size && suggestions.size < limit) {
            val key = keys[i]
            if (!key.startsWith(p)) break
            suggestions.add(key)
            i++
        }
        return suggestions
    }

    fun lookupExact(word: String): Entry? {
        if (!initialized) initializeIfNeeded()
        val key = word.trim().lowercase()
        val entry = index[key] ?: return null
        val raw = readBlock(entry.offset, entry.length)
        val translations = parseTranslations(raw, word)
        val gender = extractGenderFromRaw(raw)
        return Entry(headword = word, raw = raw, translations = translations, gender = gender)
    }

    private fun ensureDecompressedDict() {
        if (dictFile.exists() && dictFile.length() > 0) return
        val dzName = if (id.startsWith("deu")) "deu-eng.dict.dz" else "eng-deu.dict.dz"
        context.assets.open("$assetDir/$dzName").use { input ->
            // dictzip is gzip-compatible; decompress fully to a plain .dict file
            GZIPInputStream(input).use { gz ->
                dictFile.outputStream().use { out ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    while (true) {
                        val read = gz.read(buffer)
                        if (read <= 0) break
                        out.write(buffer, 0, read)
                    }
                }
            }
        }
    }

    private fun loadIndex() {
        index.clear()
        sortedKeys = null
        val idxName = if (id.startsWith("deu")) "deu-eng.index" else "eng-deu.index"
        context.assets.open("$assetDir/$idxName").use { isr ->
            BufferedReader(InputStreamReader(isr, Charsets.UTF_8)).useLines { lines ->
                lines.forEach { line ->
                    // Expected format: headword \t offset64 \t length64
                    // Skip metadata lines starting with "00database" and empty headwords
                    if (line.isEmpty()) return@forEach
                    val parts = line.split('\t')
                    if (parts.size < 3) return@forEach
                    val head = parts[0]
                    if (head.isEmpty() || head.startsWith("00database")) return@forEach
                    val off64 = parts[1]
                    val len64 = parts[2]
                    val offset = decodeBase64Number(off64)
                    val length = decodeBase64Number(len64).toInt()
                    val key = head.lowercase()
                    if (!index.containsKey(key)) {
                        index[key] = IndexEntry(offset, length)
                    }
                }
                // suggestions index (sortedKeys) will be built lazily on first suggest()
            }
        }
    }

    private fun readBlock(offset: Long, length: Int): String {
        RandomAccessFile(dictFile, "r").use { raf ->
            raf.seek(offset)
            val buf = ByteArray(length)
            var total = 0
            while (total < length) {
                val read = raf.read(buf, total, length - total)
                if (read <= 0) break
                total += read
            }
            return String(buf, 0, total, Charsets.UTF_8)
        }
    }

    private fun parseTranslations(raw: String, headword: String): List<String> {
        // FreeDict entries are usually one or multiple lines; apply robust cleanup
        val candidates = mutableListOf<String>()

        raw.split('\n').forEach { line ->
            var t = line.trim()
            if (t.isEmpty()) return@forEach

            // Remove dict-specific markup and labels
            t = t
                .replace('\t', ' ')
                .replace(Regex("<[^>]+>"), "") // remove <masc>, <fem>, domain tags
                .replace(Regex("\\[[^\\]]+\\]"), "") // remove [bot.], [cook.], etc.
                .replace(Regex("\\([^)]*\\)"), "") // remove parenthetical notes
                .replace(Regex("(?i)^see:.*$"), "") // drop see: cross-refs
                .replace(Regex("(?i)^synonym:.*$"), "") // drop synonym lines
                .replace(Regex("(?i)^antonym:.*$"), "") // drop antonym lines
                .replace(Regex("\\{[^}]+\\}"), "") // remove {forms}
                .replace(Regex("\\s+"), " ")
                .trim()

            // Split by common separators to enumerate translations
            t.split(';', '|', '/').map { it.trim() }.forEach { part ->
                var p = part
                if (p.isEmpty()) return@forEach

                // Drop leading German article if present
                p = p.replace(Regex("^(?i)(der|die|das)\\s+"), "").trim()

                // Remove quotes and trailing punctuation
                p = p.trim('"', '\'', ',', ';', '.', '–', '—', '•').trim()

                // Skip the headword itself if it leaked into payload (common in ENG→DE)
                if (p.equals(headword, ignoreCase = true)) return@forEach

                // Skip IPA-like fragments (characters in IPA Unicode blocks)
                val looksLikeIpa = Regex("[\\u0250-\\u02AF\\u02B0-\\u02FF]").containsMatchIn(p)
                if (looksLikeIpa) return@forEach

                // Very short tokens (1–2 chars) are usually labels; skip them
                if (p.length <= 2) return@forEach

                candidates.add(p)
            }
        }

        // Deduplicate while preserving order
        val seen = HashSet<String>(candidates.size)
        val out = ArrayList<String>(candidates.size)
        for (c in candidates) {
            val k = c.lowercase()
            if (seen.add(k)) out.add(c)
        }
        return out.take(16)
    }

    private fun extractGenderFromRaw(raw: String): String? {
        val l = raw.lowercase()
        // Only trust explicit tag markers to avoid false matches with abbreviations like "n." (noun)
        return when {
            Regex("<(masc|m)>").containsMatchIn(l) -> "der"
            Regex("<(fem|f)>").containsMatchIn(l) -> "die"
            Regex("<(neut|n)>").containsMatchIn(l) -> "das"
            else -> null
        }
    }

    private fun decodeBase64Number(s: String): Long {
        var result = 0L
        for (ch in s) {
            val v = when (ch) {
                in 'A'..'Z' -> ch.code - 'A'.code
                in 'a'..'z' -> 26 + (ch.code - 'a'.code)
                in '0'..'9' -> 52 + (ch.code - '0'.code)
                '+' -> 62
                '/' -> 63
                else -> 0
            }
            result = (result shl 6) or v.toLong()
        }
        return result
    }

    companion object {
        fun buildGermanToEnglish(context: Context): FreedictReader {
            return FreedictReader(
                context = context,
                assetDir = "freedict-deu-eng-1.9-fd1.dictd/deu-eng",
                id = "deu-eng"
            )
        }

        fun buildEnglishToGerman(context: Context): FreedictReader {
            return FreedictReader(
                context = context,
                assetDir = "freedict-eng-deu-1.9-fd1.dictd/eng-deu",
                id = "eng-deu"
            )
        }
    }
}


