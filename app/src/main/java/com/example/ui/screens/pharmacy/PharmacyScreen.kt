package com.example.ui.screens.pharmacy

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
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
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
import com.example.data.local.MedicineEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyScreen(
    viewModel: HealthyNationViewModel,
    onBack: (() -> Unit)? = null,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    val medicines by viewModel.medicines.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val cartTotalCount = remember(cart) { cart.sumOf { it.quantity } }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedMedicineDetail by remember { mutableStateOf<MedicineEntity?>(null) }

    val categories = listOf("All", "Chronic Care", "Pain Relief", "Vitamins & Supplements", "Antibiotics")

    val filteredMedicines = remember(medicines, searchQuery, selectedCategory) {
        medicines.filter { med ->
            (selectedCategory == "All" || med.category.equals(selectedCategory, ignoreCase = true)) &&
            (searchQuery.isBlank() || med.name.contains(searchQuery, ignoreCase = true) || med.brand.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Doorstep Pharmacy",
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
                    IconButton(onClick = onNavigateToOrders) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Order History")
                    }
                    IconButton(onClick = onNavigateToCart) {
                        BadgedBox(
                            badge = {
                                if (cartTotalCount > 0) {
                                    Badge(containerColor = OrangePrimary) {
                                        Text("$cartTotalCount")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = OrangePrimary)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search medicines, brands, or strengths...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            // Categories
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangeContainer,
                                selectedLabelColor = OnOrangeContainer
                            )
                        )
                    }
                }
            }

            // Express Delivery Banner
            item {
                HNCard(backgroundColor = DarkSurface) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(TealDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Express Pharmacy Delivery",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "100% Genuine Certified Medicines • Delivered under 4 hours",
                                fontSize = 11.sp,
                                color = DarkTextSecondary
                            )
                        }
                    }
                }
            }

            item {
                HNSectionHeader(
                    title = "Available Catalog",
                    subtitle = "${filteredMedicines.size} medications found"
                )
            }

            if (filteredMedicines.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Medicines Found",
                        message = "No medications match your query. Contact our pharmacist or search another term."
                    )
                }
            } else {
                items(filteredMedicines) { med ->
                    MedicineCardItem(
                        medicine = med,
                        onClick = { selectedMedicineDetail = med },
                        onAddToCart = { viewModel.addToCart(med) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (selectedMedicineDetail != null) {
        MedicineDetailSheet(
            medicine = selectedMedicineDetail!!,
            onDismiss = { selectedMedicineDetail = null },
            onAddToCart = {
                viewModel.addToCart(selectedMedicineDetail!!)
                selectedMedicineDetail = null
            }
        )
    }
}

@Composable
private fun MedicineCardItem(
    medicine: MedicineEntity,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    HNCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrangeContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = null,
                    tint = OnOrangeContainer,
                    modifier = Modifier.size(26.dp)
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
                        text = medicine.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HNStatusBadge(status = if (medicine.requiresPrescription) "Rx Required" else "OTC")
                }

                Text(
                    text = "${medicine.brand} • ${medicine.dosageForm} • ${medicine.strength}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = medicine.description,
                    fontSize = 12.sp,
                    color = TextMuted,
                    maxLines = 2,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${"%.2f".format(medicine.price)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = OrangePrimary
                    )

                    HNButton(
                        text = "Add to Cart",
                        icon = Icons.Default.AddShoppingCart,
                        onClick = onAddToCart
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicineDetailSheet(
    medicine: MedicineEntity,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HNStatusBadge(status = if (medicine.requiresPrescription) "Prescription Required" else "Over-The-Counter")
                Text(
                    text = "$${"%.2f".format(medicine.price)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = OrangePrimary
                )
            }

            Text(
                text = medicine.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Manufacturer / Brand: ${medicine.brand} • Form: ${medicine.dosageForm} • Strength: ${medicine.strength}",
                fontSize = 13.sp,
                color = TextSecondary
            )

            HNCard(backgroundColor = SurfaceSubtle, contentPadding = 12.dp) {
                Text(text = "Therapeutic Category", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = medicine.category, fontSize = 13.sp, color = TealDark, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Clinical Information", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = medicine.description, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
            }

            PrototypeDisclaimerBanner(
                title = "Safe Medication Notice",
                message = "Dosages are standardized information. Always follow instructions from your licensed physician."
            )

            HNButton(
                text = "Add to Cart ($${"%.2f".format(medicine.price)})",
                icon = Icons.Default.AddShoppingCart,
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
