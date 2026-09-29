package com.example.ui.screens.triage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.components.*
import com.example.services.triage.TriageLevel
import com.example.services.triage.TriageResult
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriageScreen(
    viewModel: HealthyNationViewModel,
    onBack: (() -> Unit)? = null,
    onBookDoctor: (specialty: String) -> Unit,
    onEmergencySos: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Triage Assessment, 1: AI Chatbot

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Health Assistant",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Rootly Pill Segmented Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        Surface(
                            onClick = { selectedTab = 0 },
                            shape = RoundedCornerShape(50),
                            color = if (selectedTab == 0) RootlyObsidian else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Healing,
                                    contentDescription = null,
                                    tint = if (selectedTab == 0) OrangePrimary else Color(0xFF64748B),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Symptom Triage",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (selectedTab == 0) Color.White else Color(0xFF64748B)
                                )
                            }
                        }

                        Surface(
                            onClick = { selectedTab = 1 },
                            shape = RoundedCornerShape(50),
                            color = if (selectedTab == 1) RootlyObsidian else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = if (selectedTab == 1) RootlyNeonGreen else Color(0xFF64748B),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Clinical AI Chat",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (selectedTab == 1) Color.White else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                TriageAssessmentTab(
                    viewModel = viewModel,
                    onBookDoctor = onBookDoctor,
                    onEmergencySos = onEmergencySos
                )
            } else {
                ChatbotTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun TriageAssessmentTab(
    viewModel: HealthyNationViewModel,
    onBookDoctor: (String) -> Unit,
    onEmergencySos: () -> Unit
) {
    val selectedSymptoms by viewModel.selectedSymptoms.collectAsState()
    val triageResult by viewModel.triageResult.collectAsState()
    val isEvaluating by viewModel.isTriageEvaluating.collectAsState()

    var freeText by remember { mutableStateOf("") }
    var useDeepThinking by remember { mutableStateOf(false) }
    var useSearchGrounding by remember { mutableStateOf(false) }

    val commonSymptoms = listOf(
        "Chest Pain", "Shortness of Breath", "High Fever", "Severe Headache",
        "Persistent Cough", "Fatigue", "Dizziness", "Stomach Ache",
        "Skin Rash", "Sore Throat", "Joint Pain", "Nausea"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Banner Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.triage_banner_1790687150427),
                    contentDescription = "AI Triage Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        item {
            PrototypeDisclaimerBanner(
                title = "Educational Triage Tool (Prototype)",
                message = "Powered by clinical decision heuristics and Gemini AI. Not for medical emergencies."
            )
        }

        item {
            Text(
                text = "1. Select Experienced Symptoms",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Symptom Chips
            OptFlowRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                commonSymptoms.forEach { symptom ->
                    val isSelected = selectedSymptoms.contains(symptom)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.toggleSymptom(symptom) },
                        label = { Text(symptom, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangeContainer,
                            selectedLabelColor = OnOrangeContainer
                        ),
                        modifier = Modifier.padding(end = 6.dp, bottom = 6.dp)
                    )
                }
            }
        }

        item {
            Text(
                text = "2. Describe Duration & Characteristics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = freeText,
                onValueChange = { freeText = it },
                placeholder = { Text("e.g. Started 2 days ago, worse after meals, accompanied by mild chills...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Gemini AI Tuning Options
        item {
            HNCard(backgroundColor = SurfaceSubtle) {
                Text(
                    text = "AI Intelligence Modes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "High Thinking Mode",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Deep diagnostic reasoning via gemini-3.1-pro-preview",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = useDeepThinking,
                        onCheckedChange = { useDeepThinking = it }
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Google Search Grounding",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Ground guidance in latest medical research databases",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = useSearchGrounding,
                        onCheckedChange = { useSearchGrounding = it }
                    )
                }
            }
        }

        item {
            HNButton(
                text = if (isEvaluating) "Evaluating Clinical Urgency..." else "Run Triage Assessment",
                icon = Icons.Default.Healing,
                onClick = {
                    viewModel.evaluateTriage(
                        freeText = freeText,
                        enableThinking = useDeepThinking,
                        enableSearch = useSearchGrounding
                    )
                },
                enabled = !isEvaluating,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Triage Result Section
        if (triageResult != null) {
            item {
                TriageResultCard(
                    result = triageResult!!,
                    onBookDoctor = { onBookDoctor(triageResult!!.suggestedSpecialty) },
                    onEmergency = onEmergencySos
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TriageResultCard(
    result: TriageResult,
    onBookDoctor: () -> Unit,
    onEmergency: () -> Unit
) {
    val isEmergency = result.level == TriageLevel.EMERGENCY_RED
    val isUrgent = result.level == TriageLevel.URGENT_ORANGE

    HNCard(
        backgroundColor = if (isEmergency) RoseLight.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface,
        borderColor = if (isEmergency) RoseEmergency else MaterialTheme.colorScheme.outline,
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HNStatusBadge(
                status = result.level.label,
                isCritical = isEmergency,
                isWarning = isUrgent
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Urgency:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${result.urgencyScore}/10",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isEmergency) RoseEmergencyDark else OrangePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = result.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = result.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Recommended Next Steps:",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        result.recommendedActions.forEach { action ->
            Row(
                modifier = Modifier.padding(vertical = 3.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "• ", fontWeight = FontWeight.Bold, color = OrangePrimary)
                Text(text = action, fontSize = 13.sp, color = TextSecondary)
            }
        }

        if (result.redFlagWarnings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Clinical Red Flags to Watch:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = RoseEmergencyDark
            )
            result.redFlagWarnings.forEach { warning ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = RoseEmergencyDark,
                        modifier = Modifier
                            .size(14.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = warning, fontSize = 12.sp, color = RoseEmergencyDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isEmergency) {
            HNButton(
                text = "Call Emergency SOS (911)",
                icon = Icons.Default.Emergency,
                isDanger = true,
                onClick = onEmergency,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            HNButton(
                text = "Book Consult with ${result.suggestedSpecialty}",
                icon = Icons.Default.CalendarToday,
                isSecondary = true,
                onClick = onBookDoctor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ChatbotTab(viewModel: HealthyNationViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val isResponding by viewModel.isChatbotResponding.collectAsState()
    var inputMessage by remember { mutableStateOf("") }
    var useDeepThinkingChat by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Thinking Mode Chip Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clinical Thinking Mode", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Switch(
                checked = useDeepThinkingChat,
                onCheckedChange = { useDeepThinkingChat = it },
                modifier = Modifier.height(24.dp)
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        // Chat messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }
            if (isResponding) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            color = OrangePrimary,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (useDeepThinkingChat) "Analyzing with clinical thinking..." else "Generating response...",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Input bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("Ask a question about health or medicine...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val text = inputMessage.trim()
                        if (text.isNotEmpty()) {
                            viewModel.sendChatMessage(text, useThinking = useDeepThinkingChat)
                            inputMessage = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: com.example.ui.viewmodel.ChatMessage) {
    val isUser = message.sender == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(TealDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(if (isUser) OrangePrimary else SurfaceSubtle)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = if (isUser) Color.White else TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun OptFlowRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Clean wrapping row representation
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start
    ) {
        Column {
            content()
        }
    }
}
