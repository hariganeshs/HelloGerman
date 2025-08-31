package com.hellogerman.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellogerman.app.data.entities.AnimationType
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Displays an illustration for a lesson
 */
@Composable
fun LessonIllustration(
    illustrationResId: String?,
    modifier: Modifier = Modifier,
    contentDescription: String = "Lesson illustration"
) {
    if (illustrationResId.isNullOrEmpty()) return

    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(illustrationResId, "drawable", context.packageName)

    if (resourceId != 0) {
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = contentDescription,
            modifier = modifier
                .size(64.dp)
                .semantics { this.contentDescription = contentDescription }
        )
    }
}

/**
 * Displays a character for feedback or interaction
 */
@Composable
fun CharacterDisplay(
    characterResId: String?,
    modifier: Modifier = Modifier,
    animationType: AnimationType = AnimationType.NONE,
    contentDescription: String = "Character"
) {
    if (characterResId.isNullOrEmpty()) return

    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(characterResId, "drawable", context.packageName)

    if (resourceId != 0) {
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = contentDescription,
            modifier = modifier
                .size(48.dp)
                .semantics { this.contentDescription = contentDescription }
        )
    }
}

/**
 * Animated progress bar with icons
 */
@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    showStar: Boolean = true
) {
    Box(modifier = modifier.height(24.dp)) {
        // Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = Color.LightGray,
                size = size
            )
        }

        // Progress fill animation
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
            label = "progress"
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = Color(0xFF4ECDC4), // Teal color
                size = size.copy(width = size.width * animatedProgress)
            )
        }

        // Star at the end (animated when progress completes)
        if (showStar && progress >= 0.95f) {
            val starScale by animateFloatAsState(
                targetValue = if (progress >= 1.0f) 1f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "star"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                if (starScale > 0) {
                    translate(left = size.width - 24f, top = 0f) {
                        // Simple star shape
                        rotate(degrees = 45f) {
                            drawRect(
                                color = Color(0xFFFFD93D),
                                size = androidx.compose.ui.geometry.Size(16f * starScale, 16f * starScale),
                                topLeft = androidx.compose.ui.geometry.Offset(-8f * starScale, -8f * starScale)
                            )
                        }
                        rotate(degrees = -45f) {
                            drawRect(
                                color = Color(0xFFFFD93D),
                                size = androidx.compose.ui.geometry.Size(16f * starScale, 16f * starScale),
                                topLeft = androidx.compose.ui.geometry.Offset(-8f * starScale, -8f * starScale)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Confetti animation for celebrations
 */
@Composable
fun ConfettiAnimation(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val particles = remember { List(20) { ConfettiParticle() } }

    Box(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            ConfettiParticleItem(particle)
        }
    }
}

private data class ConfettiParticle(
    val color: Color = listOf(
        Color(0xFFFFD93D), Color(0xFF4ECDC4), Color(0xFFFF6B35),
        Color(0xFF45B7D1), Color(0xFFF7931E), Color(0xFF6B35FF)
    ).random(),
    val startX: Float = Random.nextFloat() * 1000f,
    val startY: Float = -50f,
    val endX: Float = Random.nextFloat() * 1000f,
    val endY: Float = 1000f,
    val rotation: Float = Random.nextFloat() * 360f
)

@Composable
private fun ConfettiParticleItem(particle: ConfettiParticle) {
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")

    val positionY by infiniteTransition.animateFloat(
        initialValue = particle.startY,
        targetValue = particle.endY,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000 + Random.nextInt(1000), easing = EaseIn),
            repeatMode = RepeatMode.Restart
        ),
        label = "fall"
    )

    val positionX by infiniteTransition.animateFloat(
        initialValue = particle.startX,
        targetValue = particle.endX,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500 + Random.nextInt(500), easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "drift"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = particle.rotation,
        targetValue = particle.rotation + 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        translate(left = positionX, top = positionY) {
            rotate(degrees = rotation) {
                drawRect(
                    color = particle.color,
                    size = androidx.compose.ui.geometry.Size(8f, 8f),
                    topLeft = androidx.compose.ui.geometry.Offset(-4f, -4f)
                )
            }
        }
    }
}

/**
 * Procedural graphics generator for custom drawings
 */
@Composable
fun ProceduralDrawing(
    type: DrawingType,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(Color(0xFF4ECDC4), Color(0xFFFF6B35), Color(0xFFFFD93D))
) {
    Canvas(modifier = modifier) {
        when (type) {
            DrawingType.GRAMMAR_BALLS -> drawGrammarBalls(colors)
            DrawingType.CONNECTING_DOTS -> drawConnectingDots(colors)
            DrawingType.WAVE_PATTERN -> drawWavePattern(colors)
        }
    }
}

enum class DrawingType {
    GRAMMAR_BALLS,    // For noun gender (der/die/das balls)
    CONNECTING_DOTS,  // For matching exercises
    WAVE_PATTERN      // For sound patterns
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGrammarBalls(colors: List<Color>) {
    val ballCount = 5
    val ballRadius = 15f

    for (i in 0 until ballCount) {
        val x = (size.width / ballCount) * (i + 0.5f)
        val y = size.height / 2
        val color = colors[i % colors.size]

        drawCircle(
            color = color,
            radius = ballRadius,
            center = androidx.compose.ui.geometry.Offset(x, y)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawConnectingDots(colors: List<Color>) {
    val dotCount = 6
    val dotRadius = 8f

    for (i in 0 until dotCount) {
        val x = (size.width / (dotCount + 1)) * (i + 1)
        val y = if (i % 2 == 0) size.height * 0.3f else size.height * 0.7f
        val color = colors[i % colors.size]

        drawCircle(
            color = color,
            radius = dotRadius,
            center = androidx.compose.ui.geometry.Offset(x, y)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWavePattern(colors: List<Color>) {
    val waveCount = 3
    val amplitude = size.height * 0.2f

    for (wave in 0 until waveCount) {
        val color = colors[wave % colors.size]
        val offset = (size.height / waveCount) * wave + size.height * 0.2f

        // Draw a simple sine wave
        val points = mutableListOf<androidx.compose.ui.geometry.Offset>()
        for (x in 0..size.width.toInt() step 10) {
            val y = offset + kotlin.math.sin(x * 0.02f) * amplitude
            points.add(androidx.compose.ui.geometry.Offset(x.toFloat(), y))
        }

        // Draw lines connecting the points
        for (i in 0 until points.size - 1) {
            drawLine(
                color = color,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 3f
            )
        }
    }
}

// Simple modifier extensions for basic animations
