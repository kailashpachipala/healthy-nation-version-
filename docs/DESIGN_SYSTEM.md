# Design System & UI Tokens - Healthy Nation

Healthy Nation features an original, polished healthcare visual identity combining warm organic tones with energetic accents and clinical trust.

## Color Palette Tokens

| Semantic Token | Hex Value | Role & Usage |
|---|---|---|
| `OrangePrimary` | `#FFFF6B00` | Energetic hero brand accent, primary CTA buttons, active tab indicators |
| `OrangeContainer` | `#FFFFE7D1` | Light peach container for active filter chips and badges |
| `TealHealth` | `#FF0D9488` | Trust, health, and medical authority accent |
| `TealDark` | `#FF0F766E` | Contrast elements, hero dark card backgrounds |
| `CreamBackground` | `#FFFBFBF8` | Warm organic paper/cream screen background (Light mode) |
| `DarkSurface` | `#FF131B2E` | Deep charcoal/navy surface for hero summary cards |
| `DarkBackground` | `#FF0B0F19` | AMOLED/Deep navy background (Dark mode) |
| `EmeraldSuccess` | `#FF10B981` | Normal vitals, verified status, active insurance |
| `RoseEmergency` | `#FFEF4444` | SOS triggers, critical triage level, severe allergy warnings |
| `AmberWarning` | `#FFF59E0B` | Elevated biometrics, pending reviews, urgent same-day triage |
| `BlueInfo` | `#FF3B82F6` | SpO2 metrics, lab reports, informational pills |

## Core UI Components

- **`HNButton`**: Material 3 buttons with rounded 14.dp corners, strict minimum 48.dp interactive touch targets, and visual ripple feedback. Supports Primary, Secondary (Teal), Outlined, and Danger (Red SOS) variants.
- **`HNCard`**: Clean elevated and outlined cards with 20.dp radius, subtle borders, and generous 16.dp padding adhering to the 8.dp design grid.
- **`HNStatusBadge`**: Pill chips with semantic background/text contrast for Normal, Attention, Critical, and Prescription statuses.
- **`HNSectionHeader`**: Editorial section header pairing bold typography with optional interactive forward arrows.
- **`HNMetricCard`**: Vitals display card pairing circular icon badges, large metric typography, unit descriptors, and status pills.
- **`PrototypeDisclaimerBanner`**: Unobtrusive, transparent banner alerting users and reviewers to prototype boundaries.
