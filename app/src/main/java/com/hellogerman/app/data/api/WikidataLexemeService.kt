package com.hellogerman.app.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Wikidata Lexeme API service for grammatical information
 * Documentation: https://www.wikidata.org/wiki/Wikidata:Data_access
 */
interface WikidataLexemeService {
    companion object {
        const val BASE_URL = "https://www.wikidata.org/"
    }

    /**
     * Search for lexemes by lemma
     * @param search The word to search for
     * @param language Language code (e.g., "de" for German)
     * @param type Entity type (should be "lexeme")
     * @param limit Maximum number of results
     */
    @GET("w/api.php")
    suspend fun searchLexemes(
        @Query("action") action: String = "wbsearchentities",
        @Query("format") format: String = "json",
        @Query("type") type: String = "lexeme",
        @Query("language") language: String = "de",
        @Query("search") search: String,
        @Query("limit") limit: Int = 10
    ): Response<WikidataSearchResponse>

    /**
     * Get lexeme entity data
     * @param lexemeId The lexeme ID (e.g., "L1234")
     */
    @GET("wiki/Special:EntityData/{lexemeId}.json")
    suspend fun getLexemeData(
        @Query("lexemeId") lexemeId: String
    ): Response<WikidataLexemeResponse>
}

/**
 * Wikidata search response model
 */
data class WikidataSearchResponse(
    val searchinfo: WikidataSearchInfo? = null,
    val search: List<WikidataSearchResult>? = null,
    val success: Int? = null
)

data class WikidataSearchInfo(
    val search: String
)

data class WikidataSearchResult(
    val id: String,
    val concepturi: String,
    val url: String,
    val title: String,
    val pageid: Int,
    val label: String,
    val description: String? = null,
    val match: WikidataMatch? = null
)

data class WikidataMatch(
    val type: String,
    val language: String,
    val text: String
)

/**
 * Wikidata lexeme response model
 */
data class WikidataLexemeResponse(
    val entities: Map<String, WikidataLexemeEntity>
)

data class WikidataLexemeEntity(
    val id: String,
    val type: String,
    val lexicalCategory: WikidataLexicalCategory? = null,
    val language: String? = null,
    val lemmas: Map<String, WikidataLemma>? = null,
    val forms: List<WikidataForm>? = null,
    val senses: List<WikidataSense>? = null,
    val grammaticalFeatures: List<String>? = null
)

data class WikidataLexicalCategory(
    val id: String
)

data class WikidataLemma(
    val language: String,
    val value: String
)

data class WikidataForm(
    val id: String,
    val representations: Map<String, WikidataRepresentation>? = null,
    val grammaticalFeatures: List<String>? = null
)

data class WikidataRepresentation(
    val language: String,
    val value: String
)

data class WikidataSense(
    val id: String,
    val glosses: Map<String, WikidataGloss>? = null
)

data class WikidataGloss(
    val language: String,
    val value: String
)
