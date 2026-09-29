package com.example.ui.screens.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.*
import com.example.services.payment.PaymentMethodType
import com.example.services.payment.PaymentTransaction
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthyNationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: HealthyNationViewModel,
    onBack: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    val cart by viewModel.cart.collectAsState()
    val totalAmount by viewModel.cartTotal.collectAsState()

    var selectedMethod by remember { mutableStateOf(PaymentMethodType.UPI) }
    var deliveryAddress by remember { mutableStateOf("742 Evergreen Terrace, Springfield, OR 97477") }
    var isProcessing by remember { mutableStateOf(false) }
    var completedReceipt by remember { mutableStateOf<PaymentTransaction?>(null) }

    val transactions by viewModel.transactions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cart & Payment",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (completedReceipt != null) {
            ReceiptView(
                transaction = completedReceipt!!,
                onDone = {
                    val orderId = completedReceipt!!.orderId
                    completedReceipt = null
                    onOrderPlaced(orderId)
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    PrototypeDisclaimerBanner(
                        title = "Payment Provider Architecture (Prototype)",
                        message = "Processed through MockPaymentProvider interface. Ready for Razorpay / Stripe gateway adapter."
                    )
                }

                // Cart Items Summary
                item {
                    HNSectionHeader(
                        title = "Order Items (${cart.sumOf { it.quantity }})",
                        actionText = if (cart.isNotEmpty()) "Clear" else null,
                        onActionClick = { viewModel.clearCart() }
                    )
                }

                if (cart.isEmpty()) {
                    item {
                        EmptyStateView(
                            title = "Your Cart is Empty",
                            message = "Explore medications and wellness supplements in the pharmacy.",
                            actionButtonText = "Browse Pharmacy",
                            onActionClick = onBack
                        )
                    }
                } else {
                    items(cart) { item ->
                        HNCard(contentPadding = 12.dp) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.medicine.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "$${"%.2f".format(item.medicine.price)} each • ${item.medicine.strength}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.removeFromCart(item.medicine.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease", tint = OrangePrimary)
                                    }
                                    Text(
                                        text = "${item.quantity}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )
                                    IconButton(
                                        onClick = { viewModel.addToCart(item.medicine) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase", tint = OrangePrimary)
                                    }
                                }
                            }
                        }
                    }

                    // Delivery Address
                    item {
                        HNSectionHeader(title = "Delivery Destination")
                        OutlinedTextField(
                            value = deliveryAddress,
                            onValueChange = { deliveryAddress = it },
                            label = { Text("Delivery Address") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = OrangePrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Payment Methods Selection
                    item {
                        HNSectionHeader(title = "Select Payment Method")
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PaymentMethodOption(
                                method = PaymentMethodType.UPI,
                                isSelected = selectedMethod == PaymentMethodType.UPI,
                                onClick = { selectedMethod = PaymentMethodType.UPI }
                            )
                            PaymentMethodOption(
                                method = PaymentMethodType.CREDIT_DEBIT_CARD,
                                isSelected = selectedMethod == PaymentMethodType.CREDIT_DEBIT_CARD,
                                onClick = { selectedMethod = PaymentMethodType.CREDIT_DEBIT_CARD }
                            )
                            PaymentMethodOption(
                                method = PaymentMethodType.HEALTH_WALLET,
                                isSelected = selectedMethod == PaymentMethodType.HEALTH_WALLET,
                                onClick = { selectedMethod = PaymentMethodType.HEALTH_WALLET }
                            )
                            PaymentMethodOption(
                                method = PaymentMethodType.INSURANCE_CASHLESS,
                                isSelected = selectedMethod == PaymentMethodType.INSURANCE_CASHLESS,
                                onClick = { selectedMethod = PaymentMethodType.INSURANCE_CASHLESS }
                            )
                            PaymentMethodOption(
                                method = PaymentMethodType.CASH_ON_DELIVERY,
                                isSelected = selectedMethod == PaymentMethodType.CASH_ON_DELIVERY,
                                onClick = { selectedMethod = PaymentMethodType.CASH_ON_DELIVERY }
                            )
                        }
                    }

                    // Total Calculation & Pay Button
                    item {
                        HNCard(backgroundColor = SurfaceSubtle) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal", color = TextSecondary)
                                Text("$${"%.2f".format(totalAmount)}", fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Express Delivery", color = TextSecondary)
                                Text("FREE", color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Payable", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    text = "$${"%.2f".format(totalAmount)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = OrangePrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        HNButton(
                            text = if (isProcessing) "Authorizing Payment..." else "Authorize & Place Order ($${"%.2f".format(totalAmount)})",
                            icon = Icons.Default.Lock,
                            enabled = !isProcessing,
                            onClick = {
                                isProcessing = true
                                viewModel.processCheckout(selectedMethod, deliveryAddress) { orderId ->
                                    isProcessing = false
                                    completedReceipt = transactions.firstOrNull() ?: PaymentTransaction(
                                        transactionId = "TXN-${System.currentTimeMillis().toString().takeLast(6)}",
                                        orderId = orderId,
                                        amount = totalAmount,
                                        currency = "USD",
                                        method = selectedMethod,
                                        status = com.example.services.payment.PaymentStatus.SUCCESS,
                                        timestamp = System.currentTimeMillis(),
                                        receiptNumber = "RCP-${System.currentTimeMillis().toString().takeLast(6)}"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodOption(
    method: PaymentMethodType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) OrangeContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) OrangePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = method.displayName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = when (method) {
                        PaymentMethodType.UPI -> "Google Pay, PhonePe, UPI ID"
                        PaymentMethodType.CREDIT_DEBIT_CARD -> "Visa, Mastercard, Amex"
                        PaymentMethodType.HEALTH_WALLET -> "Healthy Nation Credits ($150 balance)"
                        PaymentMethodType.INSURANCE_CASHLESS -> "CareShield Policy #HN-882914"
                        PaymentMethodType.CASH_ON_DELIVERY -> "Pay via cash or QR on arrival"
                    },
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun ReceiptView(
    transaction: PaymentTransaction,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(EmeraldLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Payment Authorized!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Receipt #${transaction.receiptNumber}",
            fontSize = 13.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(20.dp))

        HNCard(contentPadding = 18.dp) {
            ReceiptRow(label = "Amount Paid", value = "$${"%.2f".format(transaction.amount)} USD")
            ReceiptRow(label = "Payment Gateway", value = "Healthy Nation Mock Gateway")
            ReceiptRow(label = "Method", value = transaction.method.displayName)
            ReceiptRow(label = "Transaction ID", value = transaction.transactionId)
            ReceiptRow(label = "Order Reference", value = transaction.orderId)
            ReceiptRow(label = "Status", value = "COMPLETED")
        }

        Spacer(modifier = Modifier.height(24.dp))

        HNButton(
            text = "Track Delivery Progress",
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
