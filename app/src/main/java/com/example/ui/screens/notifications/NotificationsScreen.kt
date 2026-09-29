package com.example.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.data.local.NotificationEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: HealthyNationViewModel,
    onBack: (() -> Unit)? = null
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notification Center",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                            Text("Mark all read", color = OrangePrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "All Caught Up",
                        message = "You have no unread notifications or health alerts.",
                        icon = Icons.Default.NotificationsNone
                    )
                }
            } else {
                items(notifications) { notif ->
                    NotificationRowItem(
                        notification = notif,
                        onClick = { viewModel.markNotificationRead(notif.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun NotificationRowItem(
    notification: NotificationEntity,
    onClick: () -> Unit
) {
    val timeStr = remember(notification.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        sdf.format(Date(notification.timestamp))
    }

    HNCard(
        onClick = onClick,
        contentPadding = 12.dp,
        backgroundColor = if (notification.isRead) MaterialTheme.colorScheme.surface else OrangeLight.copy(alpha = 0.25f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (notification.category) {
                            "APPOINTMENT" -> TealContainer
                            "ORDER" -> OrangeContainer
                            "MEDICATION" -> AmberLight
                            else -> BlueLight
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.category) {
                        "APPOINTMENT" -> Icons.Default.Event
                        "ORDER" -> Icons.Default.LocalShipping
                        "MEDICATION" -> Icons.Default.Medication
                        else -> Icons.Default.Notifications
                    },
                    contentDescription = null,
                    tint = when (notification.category) {
                        "APPOINTMENT" -> TealDark
                        "ORDER" -> OnOrangeContainer
                        "MEDICATION" -> AmberWarning
                        else -> BlueInfo
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(OrangePrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = timeStr,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}
