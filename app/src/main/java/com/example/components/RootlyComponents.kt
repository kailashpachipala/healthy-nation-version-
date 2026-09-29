package com.example.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

/**
 * Rootly-inspired Hero Banner with Ethereal Watercolor Mountain Backdrop,
 * Floating Alert Pill, Bold SaaS Headline, and Pill Action Buttons.
 */
@Composable
fun RootlyHeroHeader(
    userName: String,
    onBookDoctor: () -> Unit,
    onStartTriage: () -> Unit,
    onEmergencySos: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
    ) {
        // Mountain Backdrop Image
        Image(
            painter = painterResource(id = R.drawable.img_rootly_mountain_bg_1790688608551),
            contentDescription = "Ambient watercolor sunset mountain background",
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp),
            contentScale = ContentScale.Crop
        )

        // Gradient Overlay to ensure readable contrast & seamless blend into content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x33FFF5EE),
                            Color(0xBBFAF8F5),
                            Color(0xFFFAF9F6)
                        ),
                        startY = 0f,
                        endY = 900f
                    )
                )
        )

        // Hero Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, start = 18.dp, end = 18.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Floating Incident Pill (like Rootly top alert)
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.92f),
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(RootlyNeonGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vitals Nominal • 0 Unescalated Incidents",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            // Headline
            Text(
                text = "Modern on-call and\nhealth incident response",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Center,
                lineHeight = 31.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Continuous telemetry, Gemini 3.1 clinical triage, and instant physician paging under one roof.",
                fontSize = 13.sp,
                color = Color(0xFF475569),
                textAlign = TextAlign.Center,
                lineHeight = 17.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CTA Pill Buttons (Watch Video / Book a Demo style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary Pill: Watch Video / Book Doctor
                Surface(
                    onClick = onBookDoctor,
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.testTag("rootly_cta_book_doctor")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Book Doctor",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Primary Pill: Book a demo / Start AI Triage
                Surface(
                    onClick = onStartTriage,
                    shape = RoundedCornerShape(50),
                    color = RootlyObsidian,
                    shadowElevation = 4.dp,
                    modifier = Modifier.testTag("rootly_cta_triage")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Start AI Triage",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Partner Network Trust Strip
            RootlyTrustStrip()
        }
    }
}

/**
 * Trust strip with leading clinical network partner badges (like Cisco, Nvidia, Lattice in the image).
 */
@Composable
fun RootlyTrustStrip(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TRUSTED BY LEADING CLINICAL SYSTEMS",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrustBadge(name = "Mayo Clinic", icon = Icons.Default.LocalHospital)
            TrustBadge(name = "Johns Hopkins", icon = Icons.Default.Shield)
            TrustBadge(name = "Stanford Health", icon = Icons.Default.Verified)
            TrustBadge(name = "Cleveland Clinic", icon = Icons.Default.Favorite)
        }
    }
}

@Composable
private fun TrustBadge(name: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = name,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF475569)
        )
    }
}

/**
 * High-Fidelity Signature Phone Mockup Card from the Rootly design.
 * Features:
 * - Obsidian Dark phone container (#11141C) with subtle border (#2A3142)
 * - Pulsing emerald status badge: "Hi Alex, You're on-call"
 * - "Always on-call" inner card
 * - Shift expiration & primary physician card
 * - Next shift & telemetry card
 * - Bottom floating white capsule button
 */
@Composable
fun RootlyOnCallPhoneCard(
    userName: String,
    physicianName: String = "Dr. Sarah Jenkins",
    specialty: String = "Cardiology & Telemetry",
    shiftEndsText: String = "Today, 11:00 PM EDT",
    nextShiftText: String = "Tomorrow, 9:00 AM EDT",
    onPrimaryAction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Pulse animation for live on-call status
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rootly_on_call_phone_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = RootlyObsidian),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF2A3142)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top status bar notch & indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "9:41",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                    // Mini notch pill
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF1E2330))
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = RootlyNeonGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Avatar with Pulsing Emerald Live Indicator
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsing Outer Ring
                    Box(
                        modifier = Modifier
                            .size((46 * pulseScale).dp)
                            .clip(CircleShape)
                            .background(RootlyNeonGreen.copy(alpha = 0.18f))
                    )
                    // Inner Circle Icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B382B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = RootlyNeonGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // "Hi Alex, You're on-call"
                Text(
                    text = "Hi $userName,",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "You're on-call",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nested Dark Card 1: Always on-call
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF161A24),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262E40)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(RootlyNeonGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Always on-call",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You are continuously monitored for unescalated clinical anomalies.",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Nested Dark Card 2: Current Shift ends in
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF161A24),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262E40)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Current monitoring window ends:",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = shiftEndsText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E2433))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Primary: $physicianName ($specialty)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Nested Dark Card 3: Next Shift
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF161A24),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262E40)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Next scheduled checkup starts at:",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = nextShiftText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Floating Capsule Action Button in bottom-right (matching screenshot white button)
            Surface(
                onClick = onPrimaryAction,
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "On-Call Clinical Contact",
                        tint = RootlyObsidian,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Rootly "On-call Schedules" Timeline Grid (Desktop view adaptation).
 * Shows multi-column pastel schedules across the week.
 */
@Composable
fun RootlyScheduleBoard(
    onDaySelected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rootly_schedules_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "On-call Schedules",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Clinical Monitoring & Telemetry Rotations",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = "+ New Schedule",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Days of the week row
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { index, day ->
                    val isToday = index == 1 // Tuesday
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = day,
                            fontSize = 11.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                            color = if (isToday) OrangePrimary else Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(if (isToday) 3.dp else 1.dp)
                                .background(if (isToday) OrangePrimary else Color(0xFFE2E8F0))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-Color Schedule Event Columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Mon Column
                ScheduleColumn(
                    modifier = Modifier.weight(1f),
                    blocks = listOf(
                        ScheduleBlockData("08:00", "Vitals", RootlyPeach, RootlyPeachBorder),
                        ScheduleBlockData("20:00", "Rx", RootlyLavender, RootlyLavenderBorder)
                    )
                )

                // Tue Column (Active Day)
                ScheduleColumn(
                    modifier = Modifier.weight(1f),
                    isCurrent = true,
                    blocks = listOf(
                        ScheduleBlockData("09:00", "Dr. Call", RootlyMint, RootlyMintBorder),
                        ScheduleBlockData("14:00", "ECG", RootlyPeach, RootlyPeachBorder),
                        ScheduleBlockData("21:00", "Rx", RootlyLavender, RootlyLavenderBorder)
                    )
                )

                // Wed Column
                ScheduleColumn(
                    modifier = Modifier.weight(1f),
                    blocks = listOf(
                        ScheduleBlockData("08:00", "Vitals", RootlyPeach, RootlyPeachBorder),
                        ScheduleBlockData("17:00", "Lab Sync", RootlySky, RootlySkyBorder)
                    )
                )

                // Thu Column
                ScheduleColumn(
                    modifier = Modifier.weight(1f),
                    blocks = listOf(
                        ScheduleBlockData("12:00", "Cardio", RootlyMint, RootlyMintBorder),
                        ScheduleBlockData("20:00", "Rx", RootlyLavender, RootlyLavenderBorder)
                    )
                )

                // Fri Column
                ScheduleColumn(
                    modifier = Modifier.weight(1f),
                    blocks = listOf(
                        ScheduleBlockData("08:00", "Vitals", RootlyPeach, RootlyPeachBorder),
                        ScheduleBlockData("15:00", "Consult", RootlyMint, RootlyMintBorder)
                    )
                )

                // Sat Column
                ScheduleColumn(
                    modifier = Modifier.weight(1f),
                    blocks = listOf(
                        ScheduleBlockData("10:00", "Rest", RootlySky, RootlySkyBorder)
                    )
                )

                // Sun Column
                ScheduleColumn(
                    modifier = Modifier.weight(1f),
                    blocks = listOf(
                        ScheduleBlockData("19:00", "Summary", RootlyLavender, RootlyLavenderBorder)
                    )
                )
            }
        }
    }
}

private data class ScheduleBlockData(
    val time: String,
    val title: String,
    val bgColor: Color,
    val borderColor: Color
)

@Composable
private fun ScheduleColumn(
    blocks: List<ScheduleBlockData>,
    modifier: Modifier = Modifier,
    isCurrent: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isCurrent) Color(0xFFF8FAFC) else Color(0xFFFAFAFA))
            .border(
                1.dp,
                if (isCurrent) OrangePrimary.copy(alpha = 0.5f) else Color(0xFFF1F5F9),
                RoundedCornerShape(8.dp)
            )
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        blocks.forEach { block ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(block.bgColor)
                    .border(1.dp, block.borderColor, RoundedCornerShape(6.dp))
                    .padding(horizontal = 4.dp, vertical = 5.dp)
            ) {
                Column {
                    Text(
                        text = block.time,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Text(
                        text = block.title,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Trio Feature Showcase Cards (matching the bottom section of the Rootly screenshot).
 */
@Composable
fun RootlyFeatureTrio(
    onNavigateToVitals: () -> Unit,
    onNavigateToTriage: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Finally, consolidate continuous monitoring and clinical response under one roof.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        // Card 1: Multi-sensor health telemetry
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateToVitals)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Mini device icon row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DeviceBadge(icon = Icons.Default.Watch, label = "Smartwatch", color = Color(0xFFFFE0D0))
                    Spacer(modifier = Modifier.width(8.dp))
                    DeviceBadge(icon = Icons.Default.Speed, label = "BP Cuff", color = Color(0xFFECE7FF))
                    Spacer(modifier = Modifier.width(8.dp))
                    DeviceBadge(icon = Icons.Default.Air, label = "SpO2 Sensor", color = Color(0xFFDCFCE7))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "The only multi-device clinical telemetry platform, period",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sync Apple Health, Withings, and Bluetooth monitors with automatic baseline anomaly detection.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )
            }
        }

        // Card 2: AI Triage & Clinical reasoning
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateToTriage)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Message snippet preview
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(OrangePrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Gemini 3.1 Pro Clinical Triage",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Emergency red-flags screened with High Thinking Reasoning",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Seamlessly triage symptoms with full clinical post-incident context",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Differential diagnosis suggestions, urgency classification, and physician briefing notes generated instantly.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )
            }
        }

        // Card 3: Alert Paging & Incident Response Mockup
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RootlyObsidian),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A3142)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateToEmergency)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Mini phone alert mockup inside card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E2330),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoseEmergency.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = RoseEmergency,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ALERT: Tachycardia Spike (>112 BPM)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Paging on-call clinical physician & emergency contact",
                                fontSize = 10.sp,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Automated Incident Escalation & SOS Paging",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Instantly alert emergency responders, on-call doctors, and designated family caregivers with live GPS telemetry.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun DeviceBadge(icon: ImageVector, label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF1E293B),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
    }
}
