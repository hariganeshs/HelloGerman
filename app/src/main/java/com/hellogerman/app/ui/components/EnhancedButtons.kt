package com.hellogerman.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellogerman.app.ui.animations.enhancedPressAnimation
import com.hellogerman.app.ui.animations.entranceAnimation
import com.hellogerman.app.ui.animations.floatingAnimation
import com.hellogerman.app.ui.animations.pulseAnimation
import com.hellogerman.app.ui.animations.animatedGradient

/**
 * Enhanced button components with premium animations and visual effects
 */

@Composable
fun EnhancedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    style: EnhancedButtonStyle = EnhancedButtonStyle.Primary
) {
    val colors = when (style) {
        EnhancedButtonStyle.Primary -> listOf(
            Color(0xFF6366F1),
            Color(0xFF8B5CF6),
            Color(0xFFA855F7)
        )
        EnhancedButtonStyle.Success -> listOf(
            Color(0xFF10B981),
            Color(0xFF059669),
            Color(0xFF047857)
        )
        EnhancedButtonStyle.Warning -> listOf(
            Color(0xFFF59E0B),
            Color(0xFFD97706),
            Color(0xFFB45309)
        )
        EnhancedButtonStyle.Danger -> listOf(
            Color(0xFFEF4444),
            Color(0xFFDC2626),
            Color(0xFFB91C1C)
        )
    }
    
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .enhancedPressAnimation()
            .entranceAnimation()
            .animatedGradient(colors),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp)
                )
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun EnhancedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.PlayArrow,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .enhancedPressAnimation()
            .floatingAnimation()
            .pulseAnimation(),
        shape = RoundedCornerShape(16.dp),
        containerColor = backgroundColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 12.dp,
            pressedElevation = 6.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun EnhancedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    style: EnhancedCardStyle = EnhancedCardStyle.Default,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = when (style) {
        EnhancedCardStyle.Default -> listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surface
        )
        EnhancedCardStyle.Gradient -> listOf(
            Color(0xFF667EEA),
            Color(0xFF764BA2)
        )
        EnhancedCardStyle.Success -> listOf(
            Color(0xFF11998E),
            Color(0xFF38EF7D)
        )
        EnhancedCardStyle.Warning -> listOf(
            Color(0xFFFFB75E),
            Color(0xFFED8F03)
        )
    }
    
    Card(
        modifier = modifier
            .enhancedPressAnimation()
            .entranceAnimation(delay = 150)
            .then(
                if (style != EnhancedCardStyle.Default) 
                    Modifier.animatedGradient(colors) 
                else Modifier
            ),
        onClick = onClick ?: {},
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (style == EnhancedCardStyle.Default) 
                MaterialTheme.colorScheme.surface 
            else Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            content = content
        )
    }
}

@Composable
fun EnhancedProgressCard(
    title: String,
    subtitle: String,
    progress: Float,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    progressColor: Color = MaterialTheme.colorScheme.primary
) {
    EnhancedCard(
        modifier = modifier,
        onClick = onClick,
        style = EnhancedCardStyle.Default
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            progressColor.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .floatingAnimation(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = progressColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                EnhancedProgressBar(
                    progress = progress,
                    color = progressColor
                )
            }
        }
    }
}

@Composable
fun EnhancedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            color,
                            color.copy(alpha = 0.8f)
                        )
                    )
                )
                .entranceAnimation(delay = 300)
        )
    }
}

enum class EnhancedButtonStyle {
    Primary, Success, Warning, Danger
}

enum class EnhancedCardStyle {
    Default, Gradient, Success, Warning
}
