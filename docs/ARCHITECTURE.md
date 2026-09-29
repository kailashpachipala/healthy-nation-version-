# System Architecture - Healthy Nation

Healthy Nation is engineered following clean architecture and MVVM (Model-View-ViewModel) principles for Android.

```
UI Layer (Jetpack Compose Screens & Reusable Components)
       ↓ (StateFlow & Intent actions)
ViewModel Layer (HealthyNationViewModel)
       ↓
Domain Services & Gateways
  ├── TriageService (Gemini AI + Rule-Based Fallback)
  ├── PaymentService (PaymentProvider Interface → MockPaymentProvider)
  ├── DeviceService (DeviceProvider Interface → MockBLEPeripheralProvider)
  └── EmergencyService (System Android Intents + Emergency Dispatch Heuristics)
       ↓
Data Layer (HealthyNationRepository)
       ↓
Local Persistence (HealthyNationDatabase Room ORM + SQLite)
```

## Directory Structure

```
app/src/main/java/com/example/
├── MainActivity.kt                      # Application entry point with enableEdgeToEdge()
├── components/                          # Reusable Design System components
│   ├── HNComponents.kt                  # Buttons, Cards, Status Pills, Section Headers
│   └── HNStates.kt                      # Loading, Empty, Error states
├── data/
│   ├── local/
│   │   ├── Entities.kt                  # 12 Room database entities
│   │   ├── Daos.kt                      # Room reactive DAOs with Flow<T>
│   │   └── HealthyNationDatabase.kt     # RoomDatabase & initial clinical seed
│   └── repository/
│       └── HealthyNationRepository.kt   # Clean repository boundary
├── services/
│   ├── gemini/
│   │   └── GeminiClient.kt              # REST client with Thinking Mode & Search Grounding
│   ├── triage/
│   │   ├── TriageModels.kt              # TriageLevel, TriageResult, TriageProvider
│   │   └── TriageService.kt             # Gemini AI provider with rule-based fallback
│   ├── payment/
│   │   └── PaymentService.kt            # PaymentProvider interface & mock processor
│   ├── device/
│   │   └── DeviceService.kt             # DeviceProvider interface & BLE scanner mock
│   └── emergency/
│       └── EmergencyService.kt          # Ambulance dialer & trauma hospital directory
├── ui/
│   ├── navigation/
│   │   └── AppNavigation.kt             # 5 M3 tabs + secondary modal backstack
│   ├── theme/
│   │   ├── Color.kt                     # Orange, Teal, Cream, Dark Charcoal tokens
│   │   ├── Theme.kt                     # Light & Dark MaterialTheme ColorSchemes
│   │   └── Type.kt                      # Accessible typography scale
│   ├── viewmodel/
│   │   └── HealthyNationViewModel.kt    # Unified reactive state holder
│   └── screens/
│       ├── onboarding/                  # Welcome & value proposition
│       ├── auth/                        # Sign In, Sign Up, Password Reset
│       ├── dashboard/                   # Central health metrics & quick actions
│       ├── vitals/                      # Interactive biometrics & trend charts
│       ├── triage/                      # AI Health Assistant & Chatbot
│       ├── doctors/                     # Specialist directory & profiles
│       ├── appointments/                # Video/in-person booking & history
│       ├── history/                     # Patient dossier & chronic conditions
│       ├── labreports/                  # Pathology reports & PDF viewer sheet
│       ├── pharmacy/                    # Medicine catalog & shopping cart
│       ├── orders/                      # Orders list & delivery timeline
│       ├── payments/                    # Checkout & receipt generator
│       ├── emergency/                   # Emergency SOS & 911 dispatch
│       ├── insurance/                   # Policy card, claim filing & TPA
│       ├── devices/                     # Smartwatch, BP & Glucometer BLE pairing
│       ├── notifications/               # Filtered real-time notification alerts
│       └── profile/                     # Medical ID card & profile settings
```
