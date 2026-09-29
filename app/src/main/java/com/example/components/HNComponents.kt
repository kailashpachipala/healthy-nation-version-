package com.example.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HNButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isSecondary: Boolean = false,
    isOutlined: Boolean = false,
    isDanger: Boolean = false,
    enabled: Boolean = true,
    testTag: String = "hn_button"
) {
    val buttonColors = when {
        isDanger -> ButtonDefaults.buttonColors(
            containerColor = RoseEmergency,
            contentColor = Color.White
        )
        isSecondary -> ButtonDefaults.buttonColors(
            containerColor = TealHealth,
            contentColor = Color.White
        )
        isOutlined -> ButtonDefaults.outlinedButtonColors(
            contentColor = OrangePrimary
        )
        else -> ButtonDefaults.buttonColors(
            containerColor = OrangePrimary,
            contentColor = Color.White
        )
    }

    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .defaultMinSize(minHeight = 48.dp)
                .testTag(testTag),
            enabled = enabled,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isDanger) RoseEmergency else OrangePrimary),
            colors = buttonColors
        ) {
            ButtonContent(text = text, icon = icon)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier
                .defaultMinSize(minHeight = 48.dp)
                .testTag(testTag),
            enabled = enabled,
            shape = RoundedCornerShape(14.dp),
            colors = buttonColors,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 4.dp)
        ) {
            ButtonContent(text = text, icon = icon)
        }
    }
}

@Composable
private fun ButtonContent(text: String, icon: ImageVector?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}

@Composable
fun HNCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    contentPadding: Dp = 16.dp,
    elevation: Dp = 1.dp,
    testTag: String = "hn_card",
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun HNStatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    isCritical: Boolean = false,
    isWarning: Boolean = false,
    isSuccess: Boolean = false
) {
    val (bg, textColor) = when {
        isCritical || status.equals("CRITICAL", true) || status.equals("EMERGENCY", true) ->
            Pair(RoseLight, RoseEmergencyDark)
        isWarning || status.equals("ATTENTION", true) || status.equals("URGENT", true) ->
            Pair(AmberLight, Color(0xFFB45309))
        isSuccess || status.equals("NORMAL", true) || status.equals("COMPLETED", true) || status.equals("DELIVERED", true) ->
            Pair(EmeraldLight, Color(0xFF047857))
        status.equals("UPCOMING", true) || status.equals("PLACED", true) ->
            Pair(TealLight, TealDark)
        else -> Pair(SurfaceSubtle, TextSecondary)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.uppercase(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun HNSectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = actionText,
                    color = OrangePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun HNMetricCard(
    title: String,
    value: String,
    unit: String,
    statusText: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    HNCard(
        modifier = modifier,
        onClick = onClick,
        contentPadding = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            HNStatusBadge(status = statusText)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = unit,
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        Text(
            text = title,
            fontSize = 12.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PrototypeDisclaimerBanner(
    title: String = "Interactive Healthcare Prototype",
    message: String = "Simulated clinical flows and telemetry for demonstration. Not for medical emergencies."
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AmberLight.copy(alpha = 0.6f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFFB45309),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF78350F)
                )
                Text(
                    text = message,
                    fontSize = 10.sp,
                    color = Color(0xFF92400E),
                    lineHeight = 13.sp
                )
            }
        }
    }
}
