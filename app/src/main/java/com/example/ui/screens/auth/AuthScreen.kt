package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.HNButton
import com.example.components.HNCard
import com.example.components.PrototypeDisclaimerBanner
import com.example.ui.theme.*

enum class AuthMode {
    SIGN_IN,
    SIGN_UP,
    FORGOT_PASSWORD
}

@Composable
fun AuthScreen(
    onAuthSuccess: (String, String) -> Unit,
    onBackToOnboarding: () -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var email by remember { mutableStateOf("alex.morgan@healthynation.org") }
    var password by remember { mutableStateOf("••••••••") }
    var fullName by remember { mutableStateOf("Alex Morgan") }
    var phone by remember { mutableStateOf("+1 (555) 349-2810") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (mode) {
                    AuthMode.SIGN_IN -> "Welcome Back"
                    AuthMode.SIGN_UP -> "Create Account"
                    AuthMode.FORGOT_PASSWORD -> "Reset Password"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = when (mode) {
                    AuthMode.SIGN_IN -> "Enter your Healthy Nation credentials to access your health portal"
                    AuthMode.SIGN_UP -> "Join Healthy Nation for verified doctors and smart health tracking"
                    AuthMode.FORGOT_PASSWORD -> "Enter your email to receive recovery instructions"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            PrototypeDisclaimerBanner(
                title = "Prototype Auth Flow",
                message = "Pre-configured for test user Alex Morgan. Tap Continue to access the live dashboard."
            )

            Spacer(modifier = Modifier.height(16.dp))

            HNCard(elevation = 2.dp) {
                if (mode == AuthMode.SIGN_UP) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                if (mode != AuthMode.FORGOT_PASSWORD) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }

                if (mode == AuthMode.SIGN_IN) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { mode = AuthMode.FORGOT_PASSWORD }) {
                            Text(
                                text = "Forgot Password?",
                                fontSize = 13.sp,
                                color = TealHealth,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                HNButton(
                    text = when (mode) {
                        AuthMode.SIGN_IN -> "Sign In to Health Portal"
                        AuthMode.SIGN_UP -> "Complete Registration"
                        AuthMode.FORGOT_PASSWORD -> "Send Reset Link"
                    },
                    onClick = {
                        if (mode == AuthMode.FORGOT_PASSWORD) {
                            statusMessage = "A simulated password reset link has been dispatched to $email."
                        } else {
                            onAuthSuccess(email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (statusMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = statusMessage!!,
                        color = EmeraldSuccess,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Switch Auth Mode
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (mode) {
                        AuthMode.SIGN_IN -> "Don't have an account? "
                        AuthMode.SIGN_UP -> "Already registered? "
                        AuthMode.FORGOT_PASSWORD -> "Remember your password? "
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = {
                        statusMessage = null
                        mode = when (mode) {
                            AuthMode.SIGN_IN -> AuthMode.SIGN_UP
                            AuthMode.SIGN_UP -> AuthMode.SIGN_IN
                            AuthMode.FORGOT_PASSWORD -> AuthMode.SIGN_IN
                        }
                    }
                ) {
                    Text(
                        text = when (mode) {
                            AuthMode.SIGN_IN -> "Sign Up"
                            AuthMode.SIGN_UP -> "Sign In"
                            AuthMode.FORGOT_PASSWORD -> "Back to Sign In"
                        },
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
