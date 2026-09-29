package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: HealthyNationViewModel,
    onNavigateToMedicalId: () -> Unit,
    onNavigateToInsurance: () -> Unit,
    onNavigateToDevices: () -> Unit,
    onNavigateToMedicalHistory: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToEmergency: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile & Health Hub",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Profile Banner
            item {
                HNCard(backgroundColor = DarkSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(TealDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user?.name?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("") ?: "AM",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user?.name ?: "Alex Morgan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = user?.email ?: "alex.morgan@healthynation.org",
                                fontSize = 12.sp,
                                color = DarkTextSecondary
                            )
                            Text(
                                text = user?.phone ?: "+1 (555) 349-2810",
                                fontSize = 12.sp,
                                color = DarkTextSecondary
                            )
                        }

                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = OrangePrimary)
                        }
                    }
                }
            }

            // High Priority Medical ID Highlight Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToMedicalId),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = OrangeContainer.copy(alpha = 0.45f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangePrimary)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(OrangePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Emergency Medical ID Card",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = OnOrangeContainer
                                )
                                Text(
                                    text = "Blood Group ${user?.bloodGroup} • ${user?.allergies}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = OrangePrimary
                        )
                    }
                }
            }

            // Health Records & Care Section
            item {
                Text(
                    text = "Health Management",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                HNCard(contentPadding = 6.dp) {
                    ProfileMenuItem(title = "Medical History & Conditions", icon = Icons.Default.History, onClick = onNavigateToMedicalHistory)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ProfileMenuItem(title = "Health Insurance & Policy", icon = Icons.Default.Security, onClick = onNavigateToInsurance)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ProfileMenuItem(title = "Connected Health Devices", icon = Icons.Default.Watch, onClick = onNavigateToDevices)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ProfileMenuItem(title = "Medicine Order History", icon = Icons.Default.ShoppingBag, onClick = onNavigateToOrders)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ProfileMenuItem(title = "Emergency Contacts & Facilities", icon = Icons.Default.Emergency, onClick = onNavigateToEmergency)
                }
            }

            // Security, Privacy & Settings
            item {
                Text(
                    text = "Security & Prototype Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                HNCard(contentPadding = 6.dp) {
                    ProfileMenuItem(title = "HIPAA & Health Privacy Controls", icon = Icons.Default.Lock, onClick = { /* simulated */ })
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ProfileMenuItem(title = "Help & Clinical Support Center", icon = Icons.AutoMirrored.Filled.HelpOutline, onClick = { /* simulated */ })
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ProfileMenuItem(
                        title = "Sign Out",
                        icon = Icons.AutoMirrored.Filled.Logout,
                        isDestructive = true,
                        onClick = { viewModel.signOut() }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showEditProfileDialog) {
        EditProfileModal(
            currentName = user?.name ?: "",
            currentPhone = user?.phone ?: "",
            currentBlood = user?.bloodGroup ?: "O+",
            currentAllergies = user?.allergies ?: "",
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, phone, blood, allergies ->
                viewModel.updateProfile(name, phone, blood, allergies)
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    title: String,
    icon: ImageVector,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) RoseEmergency else OrangePrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDestructive) RoseEmergency else MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun EditProfileModal(
    currentName: String,
    currentPhone: String,
    currentBlood: String,
    currentAllergies: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var phone by remember { mutableStateOf(currentPhone) }
    var blood by remember { mutableStateOf(currentBlood) }
    var allergies by remember { mutableStateOf(currentAllergies) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Patient Information", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = blood,
                    onValueChange = { blood = it },
                    label = { Text("Blood Group (e.g. O+, A-, B+)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Known Allergies") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            HNButton(text = "Save Changes", onClick = { onSave(name, phone, blood, allergies) })
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
