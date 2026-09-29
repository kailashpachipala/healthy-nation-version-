package com.example.ui.screens.orders

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.data.local.PharmacyOrderEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: HealthyNationViewModel,
    onBack: (() -> Unit)? = null,
    onShopPharmacy: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    var selectedOrderForTracking by remember { mutableStateOf<PharmacyOrderEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Medicine Orders & Deliveries",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                PrototypeDisclaimerBanner(
                    title = "Simulated Logistics Pipeline",
                    message = "Orders integrate with real-time GPS courier dispatcher mock service."
                )
            }

            if (orders.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Orders Placed Yet",
                        message = "Order medicines or chronic care refills from the Healthy Nation pharmacy.",
                        actionButtonText = "Browse Pharmacy",
                        onActionClick = onShopPharmacy
                    )
                }
            } else {
                items(orders) { order ->
                    OrderItemCard(
                        order = order,
                        onTrackDelivery = { selectedOrderForTracking = order }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (selectedOrderForTracking != null) {
        DeliveryTrackingModal(
            order = selectedOrderForTracking!!,
            onDismiss = { selectedOrderForTracking = null }
        )
    }
}

@Composable
private fun OrderItemCard(
    order: PharmacyOrderEntity,
    onTrackDelivery: () -> Unit
) {
    HNCard(onClick = onTrackDelivery) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = order.orderNumber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Estimated: ${order.estimatedDelivery}",
                    fontSize = 12.sp,
                    color = TealDark,
                    fontWeight = FontWeight.Medium
                )
            }
            HNStatusBadge(status = order.orderStatus)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = order.itemsSummary,
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total Paid",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(
                    text = "$${"%.2f".format(order.totalAmount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = OrangePrimary
                )
            }

            HNButton(
                text = "Live Tracking",
                icon = Icons.Default.LocalShipping,
                isSecondary = true,
                onClick = onTrackDelivery
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeliveryTrackingModal(
    order: PharmacyOrderEntity,
    onDismiss: () -> Unit
) {
    val steps = listOf(
        "Order Placed" to "Prescription verified by pharmacist",
        "Confirmed" to "Inventory reserved at central pharmacy",
        "Packed" to "Tamper-evident cold-chain packaging sealed",
        "Out for Delivery" to "Courier en route to address",
        "Delivered" to "Delivered to recipient"
    )

    val currentStepIndex = when (order.orderStatus.uppercase()) {
        "PLACED" -> 0
        "CONFIRMED" -> 1
        "PACKED" -> 2
        "OUT_FOR_DELIVERY" -> 3
        "DELIVERED" -> 4
        else -> 2
    }

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
                Column {
                    Text(
                        text = "Delivery Status",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.orderNumber,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                HNStatusBadge(status = order.orderStatus)
            }

            // Simulated Map Route Graphic
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw simulated road lines
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(0f, size.height * 0.5f),
                        end = Offset(size.width, size.height * 0.5f),
                        strokeWidth = 14f
                    )
                    drawLine(
                        color = OrangePrimary,
                        start = Offset(size.width * 0.1f, size.height * 0.5f),
                        end = Offset(size.width * 0.7f, size.height * 0.5f),
                        strokeWidth = 6f
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = TealLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Healthy Nation Pharmacy", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text("Central Depot", fontSize = 10.sp, color = DarkTextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delivery Address", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text(order.deliveryAddress.take(24), fontSize = 10.sp, color = DarkTextSecondary)
                    }
                }
            }

            // Timeline Steps
            Column(modifier = Modifier.fillMaxWidth()) {
                steps.forEachIndexed { index, (title, description) ->
                    val isDone = index <= currentStepIndex
                    val isCurrent = index == currentStepIndex

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> OrangePrimary
                                            isDone -> EmeraldSuccess
                                            else -> SurfaceBorder
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            if (index < steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(28.dp)
                                        .background(if (isDone && index < currentStepIndex) EmeraldSuccess else SurfaceBorder)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.padding(bottom = if (index < steps.size - 1) 12.dp else 0.dp)) {
                            Text(
                                text = title,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = if (isDone) MaterialTheme.colorScheme.onSurface else TextMuted
                            )
                            Text(
                                text = description,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            HNButton(
                text = "Close Tracking",
                isOutlined = true,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
