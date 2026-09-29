# API Contracts & Integration Points - Healthy Nation

Healthy Nation isolates backend and external services through clean repository and service layers.

## REST Endpoints Structure

| Domain | Route | Methods | Responsibility |
|---|---|---|---|
| **Auth** | `/api/auth/session` | `GET`, `POST` | User authentication, token verification |
| **Vitals** | `/api/vitals` | `GET`, `POST` | Stream biometrics, log manual readings |
| **Triage** | `/api/triage/assess` | `POST` | Clinical triage evaluation (Gemini AI + heuristic fallback) |
| **Doctors** | `/api/doctors` | `GET` | Physician listings, specialties, and schedules |
| **Appointments** | `/api/appointments` | `GET`, `POST`, `DELETE` | Booking, rescheduling, and cancellation |
| **Medical History** | `/api/medical-history` | `GET`, `POST` | Conditions, allergies, medications dossier |
| **Lab Reports** | `/api/lab-reports` | `GET` | Pathology reports, test parameters, PDF blobs |
| **Pharmacy** | `/api/pharmacy/medicines` | `GET` | Medicine inventory catalog & pricing |
| **Orders** | `/api/orders` | `GET`, `POST` | Order placement, active delivery tracking |
| **Payments** | `/api/payments/process` | `POST` | Gateway authorization (UPI, Card, Wallet, Cashless) |
| **Emergency** | `/api/emergency/nearby` | `GET` | Nearest trauma centers and ambulance dispatch |
| **Insurance** | `/api/insurance/claims` | `GET`, `POST` | Policy details, TPA pre-authorization |
| **Devices** | `/api/devices/telemetry` | `POST` | Wearable BLE sync ingestion |

## Unified Response Schema

```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": 1790687135000
}
```

Error format:
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "APPOINTMENT_SLOT_UNAVAILABLE",
    "message": "Selected consultation slot is no longer available"
  }
}
```
