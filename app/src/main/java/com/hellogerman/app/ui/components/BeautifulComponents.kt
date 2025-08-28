package com.hellogerman.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellogerman.app.data.entities.*
import com.hellogerman.app.ui.animations.enhancedPressAnimation
import com.hellogerman.app.ui.animations.entranceAnimation
import com.hellogerman.app.ui.animations.floatingAnimation
import com.hellogerman.app.ui.animations.pulseAnimation
import com.hellogerman.app.ui.animations.successCelebration
import com.hellogerman.app.ui.animations.shimmerEffect
import com.hellogerman.app.ui.animations.animatedGradient
import kotlin.math.*

// Beautiful Achievement Card with animations
@Composable
fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val rarityColor = when (achievement.rarity) {
        AchievementRarity.COMMON -> Color(0xFF8D6E63)    // Bronze
        AchievementRarity.RARE -> Color(0xFF90A4AE)      // Silver  
        AchievementRarity.EPIC -> Color(0xFFFFB74D)      // Gold
        AchievementRarity.LEGENDARY -> Color(0xFFE1BEE7) // Diamond
    }
    
    val shimmerAnimation = rememberInfiniteTransition()
    val shimmerOffset by shimmerAnimation.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .enhancedPressAnimation()
            .entranceAnimation(delay = 100)
            .then(if (achievement.isUnlocked) Modifier.floatingAnimation() else Modifier),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) rarityColor.copy(alpha = 0.1f) 
                           else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievement Icon with glow effect
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        brush = if (achievement.isUnlocked) {
                            Brush.radialGradient(
                                colors = listOf(rarityColor.copy(alpha = 0.3f), Color.Transparent),
                                radius = 80f
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(Color.Gray.copy(alpha = 0.1f), Color.Transparent),
                                radius = 80f
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.icon,
                    fontSize = 32.sp,
                    modifier = Modifier.scale(if (achievement.isUnlocked) 1f else 0.6f)
                )
                
                // Shimmer effect for unlocked achievements
                if (achievement.isUnlocked && achievement.rarity == AchievementRarity.LEGENDARY) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val shimmerBrush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                rarityColor.copy(alpha = 0.5f),
                                Color.Transparent
                            ),
                            center = center
                        )
                        drawCircle(
                            brush = shimmerBrush,
                            radius = size.minDimension / 2,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Achievement Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) MaterialTheme.colorScheme.onSurface
                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Rarity badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(rarityColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = achievement.rarity.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = rarityColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "${achievement.points} XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Progress bar for unlocked achievements
                if (!achievement.isUnlocked && achievement.maxProgress > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ProgressBar(
                        progress = achievement.progress.toFloat() / achievement.maxProgress,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "${achievement.progress} / ${achievement.maxProgress}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Unlocked indicator
            if (achievement.isUnlocked) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Unlocked",
                    tint = rarityColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Beautiful animated progress bar
@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeWidth: Float = 8f
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(strokeWidth.dp)
            .clip(RoundedCornerShape(strokeWidth.dp / 2))
    ) {
        // Background
        drawRoundRect(
            color = backgroundColor,
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(strokeWidth / 2)
        )
        
        // Progress
        if (animatedProgress > 0f) {
            drawRoundRect(
                color = color,
                size = size.copy(width = size.width * animatedProgress),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(strokeWidth / 2)
            )
        }
    }
}

// Beautiful XP gain notification
@Composable
fun XPGainNotification(
    xpAmount: Long,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(500)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨",
                            fontSize = 24.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "XP Gained!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "+$xpAmount XP",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
    
    // Auto dismiss after 3 seconds
    LaunchedEffect(isVisible) {
        if (isVisible) {
            kotlinx.coroutines.delay(3000)
            onDismiss()
        }
    }
}

// Level up celebration animation
@Composable
fun LevelUpCelebration(
    newLevel: Int,
    newTitle: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            // Confetti effect
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawConfetti(this)
            }
            
            Card(
                modifier = Modifier
                    .scale(scale)
                    .padding(32.dp)
                    .successCelebration()
                    .animatedGradient(
                        colors = listOf(
                            Color(0xFFFFD700),
                            Color(0xFFFFA500),
                            Color(0xFFFF6B6B)
                        )
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Rotating celebration icon
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .rotate(rotation)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎉",
                            fontSize = 60.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "LEVEL UP!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Level $newLevel",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = newTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Continue Learning!")
                    }
                }
            }
        }
    }
}

// Beautiful daily challenge card
@Composable
fun DailyChallengeCard(
    challenge: DailyChallenge,
    modifier: Modifier = Modifier
) {
    val progressPercentage = if (challenge.targetValue > 0) {
        (challenge.currentProgress.toFloat() / challenge.targetValue).coerceAtMost(1f)
    } else 0f
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .enhancedPressAnimation()
            .entranceAnimation(delay = 200)
            .then(if (challenge.isCompleted) Modifier.successCelebration() else Modifier.pulseAnimation()),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (challenge.isCompleted) 
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getChallengeTitle(challenge.challengeType),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (challenge.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = getChallengeDescription(challenge.challengeType, challenge.targetValue),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress
            ProgressBar(
                progress = progressPercentage,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${challenge.currentProgress} / ${challenge.targetValue}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "🏆 ${challenge.rewardXP} XP + ${challenge.rewardCoins} coins",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Helper function to draw confetti
private fun drawConfetti(drawScope: DrawScope) {
    val colors = listOf(
        Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFF45B7D1),
        Color(0xFFFFA07A), Color(0xFF98FB98), Color(0xFFDDA0DD)
    )
    
    repeat(50) {
        val x = (0..drawScope.size.width.toInt()).random().toFloat()
        val y = (0..drawScope.size.height.toInt()).random().toFloat()
        val color = colors.random()
        val size = (5..15).random().toFloat()
        
        drawScope.drawCircle(
            color = color,
            radius = size,
            center = Offset(x, y)
        )
    }
}

// Helper functions
private fun getChallengeTitle(type: ChallengeType): String {
    return when (type) {
        ChallengeType.COMPLETE_LESSONS -> "Lesson Marathon"
        ChallengeType.SCORE_POINTS -> "Point Hunter"
        ChallengeType.PERFECT_STREAK -> "Perfectionist"
        ChallengeType.GRAMMAR_MASTER -> "Grammar Master"
        ChallengeType.SPEED_DEMON -> "Speed Demon"
        ChallengeType.VOCABULARY_COLLECTOR -> "Word Collector"
    }
}

private fun getChallengeDescription(type: ChallengeType, target: Int): String {
    return when (type) {
        ChallengeType.COMPLETE_LESSONS -> "Complete $target lessons today"
        ChallengeType.SCORE_POINTS -> "Score $target points today"
        ChallengeType.PERFECT_STREAK -> "Get $target perfect scores"
        ChallengeType.GRAMMAR_MASTER -> "Master $target grammar topics"
        ChallengeType.SPEED_DEMON -> "Complete $target lessons quickly"
        ChallengeType.VOCABULARY_COLLECTOR -> "Learn $target new words"
    }
}
