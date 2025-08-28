package com.hellogerman.app.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Enhanced animations for the Hello German app
 * Provides smooth, premium feeling animations and effects
 */
object EnhancedAnimations {
    
    // Animation Specifications
    val smoothSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val quickSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val bounceSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val slideSpec = tween<Int>(
        durationMillis = 400,
        easing = FastOutSlowInEasing
    )
    
    val fadeSpec = tween<Float>(
        durationMillis = 300,
        easing = LinearOutSlowInEasing
    )
    
    val scaleSpec = tween<Float>(
        durationMillis = 250,
        easing = FastOutSlowInEasing
    )
    
    // Advanced Transition Specs
    val cardEnterTransition = slideInVertically(
        animationSpec = tween(500, easing = FastOutSlowInEasing)
    ) { it / 2 } + fadeIn(animationSpec = tween(300))
    
    val cardExitTransition = slideOutVertically(
        animationSpec = tween(300, easing = FastOutLinearInEasing)
    ) { -it / 2 } + fadeOut(animationSpec = tween(200))
    
    val pageEnterTransition = slideInHorizontally(
        animationSpec = tween(400, easing = FastOutSlowInEasing)
    ) { it } + fadeIn(animationSpec = tween(300, delayMillis = 100))
    
    val pageExitTransition = slideOutHorizontally(
        animationSpec = tween(300, easing = FastOutLinearInEasing)
    ) { -it / 3 } + fadeOut(animationSpec = tween(200))
}

/**
 * Enhanced press animation that provides satisfying feedback
 */
fun Modifier.enhancedPressAnimation(): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = EnhancedAnimations.quickSpring,
        label = "pressScale"
    )
    
    this
        .scale(scale)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                }
            )
        }
}

/**
 * Bouncy hover animation for interactive elements
 */
fun Modifier.bouncyHover(): Modifier = composed {
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1f,
        animationSpec = EnhancedAnimations.bounceSpring,
        label = "hoverScale"
    )
    
    this.scale(scale)
}

/**
 * Shimmer loading effect
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    
    this.alpha(alpha)
}

/**
 * Floating animation for elements
 */
fun Modifier.floatingAnimation(
    amplitude: Dp = 4.dp,
    duration: Int = 2000
): Modifier = composed {
    val density = LocalDensity.current
    val amplitudePixels = with(density) { amplitude.toPx() }
    
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = amplitudePixels,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingOffset"
    )
    
    this.offset(y = with(density) { offsetY.toDp() })
}

/**
 * Pulse animation for attention-grabbing elements
 */
fun Modifier.pulseAnimation(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    this.scale(scale)
}

/**
 * Rotation animation
 */
fun Modifier.rotateAnimation(
    duration: Int = 1000,
    clockwise: Boolean = true
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (clockwise) 360f else -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    this.rotate(rotation)
}

/**
 * Entrance animation for cards and components
 */
fun Modifier.entranceAnimation(
    delay: Int = 0,
    duration: Int = 600
): Modifier = composed {
    var visible by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        ),
        label = "entranceScale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = duration,
            delayMillis = delay,
            easing = LinearOutSlowInEasing
        ),
        label = "entranceAlpha"
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    this
        .scale(scale)
        .alpha(alpha)
}

/**
 * Success celebration animation
 */
fun Modifier.successCelebration(): Modifier = composed {
    var triggerAnimation by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (triggerAnimation) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = {
            if (triggerAnimation) {
                triggerAnimation = false
            }
        },
        label = "successScale"
    )
    
    LaunchedEffect(Unit) {
        triggerAnimation = true
    }
    
    this.scale(scale)
}

/**
 * Particle effect background
 */
fun Modifier.particleBackground(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleRotation"
    )
    
    this.drawBehind {
        drawParticles(rotation)
    }
}

private fun DrawScope.drawParticles(rotation: Float) {
    val particles = 20
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = size.minDimension / 3
    
    repeat(particles) { i ->
        val angle = (360f / particles) * i + rotation
        val x = centerX + radius * cos(Math.toRadians(angle.toDouble())).toFloat()
        val y = centerY + radius * sin(Math.toRadians(angle.toDouble())).toFloat()
        
        drawCircle(
            color = Color.White.copy(alpha = 0.1f),
            radius = 3f,
            center = Offset(x, y)
        )
    }
}

/**
 * Gradient background animation
 */
fun Modifier.animatedGradient(
    colors: List<Color>,
    duration: Int = 3000
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )
    
    this.drawBehind {
        val brush = Brush.linearGradient(
            colors = colors,
            start = Offset(0f, offset * size.height),
            end = Offset(size.width, (1 - offset) * size.height),
            tileMode = TileMode.Mirror
        )
        drawRect(brush = brush)
    }
}
