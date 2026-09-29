# Healthy Nation - Mobile Digital Healthcare Platform

Healthy Nation is a production-grade Android mobile healthcare application prototype built with Kotlin, Jetpack Compose, Material Design 3, Room Database persistence, and Gemini AI clinical intelligence.

## 🚀 Key Features

1. **Patient Dashboard & Health Score**: Central wellness index, real-time vitals monitoring, next dose alerts, and upcoming appointment cards.
2. **Continuous Biometric Vitals Tracker**: Live telemetry tracking for Heart Rate, Blood Pressure (Sys/Dia), SpO2, Body Temperature, and Step activity with interactive canvas trend charts.
3. **AI Health Assistant & Symptom Triage**: Rule-based clinical triage engine backed by Gemini AI (`gemini-3.5-flash` and `gemini-3.1-pro-preview` with High Thinking mode and Google Search grounding).
4. **Specialist Doctor Consultations**: Searchable physician directory across cardiology, dermatology, pulmonology, and internal medicine with instant video/in-clinic scheduling.
5. **Medical History & Patient Dossier**: Comprehensive record of chronic conditions, active prescriptions, severe allergies, past surgical history, and family heredity.
6. **Diagnostic Lab Reports**: Pathology and radiology test results with clinical findings and reference range indicators.
7. **Doorstep Express Pharmacy**: Searchable medication catalog with OTC vs. Prescription badges, shopping cart, and order dispatch.
8. **Live Order Tracking**: Visual order-to-doorstep timeline (Placed → Confirmed → Packed → Out for Delivery → Delivered) with simulated GPS courier route map.
9. **Extensible Payment System**: Clean `PaymentService` architecture with `MockPaymentProvider` supporting UPI, Cards, Health Wallet, Direct Cashless Insurance, and Cash on Delivery.
10. **Emergency SOS & 911 Dispatch**: High-visibility emergency interface with pulsing SOS trigger, one-touch 911 ambulance dialer intent, next-of-kin alerts, and Level 1 trauma center finder.
11. **Comprehensive Health Insurance**: Digital policy card, coverage utilization bar, cashless hospital network, and claim reimbursement filing.
12. **Connected Health Devices (BLE Gateway)**: Peripheral scanning and telemetry sync with smartwatches, wireless blood pressure cuffs, and glucometers.
13. **Emergency Medical ID**: First-responder critical information card including blood group, severe allergies, chronic conditions, and organ donor status.
14. **Notification Center**: Real-time alerts for prescription timings, appointment reminders, lab results, and delivery updates.

---

## 🏗️ Architecture & Technology Stack

- **Platform**: Android (Kotlin, Jetpack Compose, Material Design 3)
- **Local Persistence**: Android Room Database with reactive Kotlin Coroutines `Flow`
- **AI Intelligence**: Direct REST API integration with Google Gemini models (`gemini-3.5-flash`, `gemini-3.1-pro-preview`) with High Thinking mode and Google Search grounding
- **Design System**: Centralized design tokens (Warm Cream `#FBFBF8`, Energetic Orange `#FF6B00`, Trust Teal `#0D9488`, Deep Charcoal `#0B0F19`)
- **State Management**: MVVM Architecture with Android `ViewModel`, `StateFlow`, and `BackHandler`

---

## 📖 Documentation

- [System Architecture](docs/ARCHITECTURE.md)
- [Developer Guide](docs/DEVELOPER_GUIDE.md)
- [Design System & UI Tokens](docs/DESIGN_SYSTEM.md)
- [API Contracts & Endpoints](docs/API.md)
- [Feature Matrix & Implementation Status](docs/FEATURE_MATRIX.md)
- [Future Production Integrations](docs/FUTURE_INTEGRATIONS.md)
