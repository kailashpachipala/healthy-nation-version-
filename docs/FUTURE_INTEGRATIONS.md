# Future Production Integrations - Healthy Nation

Healthy Nation is designed with decoupled service interfaces to ensure that migrating from prototype to production requires zero modifications to UI composables.

## 1. Production Payment Gateways
- **Interface**: `PaymentProvider` (`com.example.services.payment.PaymentProvider`)
- **Planned Adapters**:
  - `RazorpayPaymentProvider` (using Razorpay Android Standard Checkout SDK)
  - `StripePaymentProvider` (using `com.stripe:stripe-android`)
- **Required Env Variables**:
  - `PAYMENT_GATEWAY_KEY_ID`
  - `PAYMENT_WEBHOOK_SECRET`

## 2. Real Bluetooth Medical Peripherals (BLE)
- **Interface**: `DeviceProvider` (`com.example.services.device.DeviceProvider`)
- **Planned Adapters**:
  - `AndroidBluetoothLeProvider` utilizing `BluetoothLeScanner` and standard Health Device Profile (HDP) GATT services (Heart Rate Service UUID `0x180D`, Blood Pressure UUID `0x1810`, Glucose UUID `0x1808`).
- **Required Permissions**:
  - `android.permission.BLUETOOTH_SCAN`
  - `android.permission.BLUETOOTH_CONNECT`

## 3. WebRTC Video Teleconsultations
- **Interface**: Direct WebRTC or Twilio / Agora SDK wrapper for live peer-to-peer encrypted doctor-patient video consultations.
- **Required Permissions**:
  - `android.permission.CAMERA`
  - `android.permission.RECORD_AUDIO`

## 4. Google Maps & Geolocation
- **Integration Point**: In `OrdersScreen` delivery tracking and `EmergencyScreen` nearby ER hospital finder.
- **Planned Adapter**: Google Maps Compose SDK (`com.google.maps.android:maps-compose`).

## 5. Firebase Cloud Messaging (FCM)
- **Integration Point**: In `com.example.ui.screens.notifications.NotificationsScreen` and background Push Notification Service.
