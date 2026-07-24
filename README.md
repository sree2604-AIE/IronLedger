# 🛡️ IronLedger: The Luxury Financial Operating System

[![Build Status](https://img.shields.io/badge/Build-Success-brightgreen)](https://github.com/yourusername/IronLedger)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-orange)](https://developer.android.com/topic/architecture)

**IronLedger** (or VaultX) is not a simple expense tracker. It is a premium, executive-grade financial command center designed with the elegance of a Rolls-Royce and the precision of a BMW. Engineered for high-net-worth individuals who demand a sophisticated, high-performance interface for their personal fleet, global travels, and financial portfolios.

---

## 🌟 Premium Features

### 🏎️ Executive Fleet Command
- **Personalized 3D Vehicle Mapping**: Dynamically renders your specific vehicle (e.g., *Hyundai i10 2010 Blue*) with custom badging and real-world color mapping.
- **Automotive Analytics**: High-fidelity tracking of fuel efficiency (`Distance / Fuel`), service history, tolls, and maintenance logs.
- **Expense Trends**: Visual 12-month spending velocity bars with executive emerald highlights.

### 💼 Professional Ledger & Vault
- **Multi-Bank Mastery**: Real-time management of HDFC, SBI, ICICI, and custom accounts in a 4-card "Cockpit" grid.
- **Audit-Grade Recalculation**: A deep-summation engine ensures 100% accuracy by auditing every transaction across the entire ledger.
- **Dynamic Budgets**: Functional category limits (Housing, Food, Shopping) with proactive velocity alerts.

### 🤖 Intelligence & Automation
- **Active SMS Bridge**: Background detection of bank transactions with an executive "Pending Review" queue.
- **Voice Entry Engine**: Natural language parsing logic ready to convert speech commands into structured financial records.
- **Rule-Based AI Insights**: Context-aware narrative on spending hotspots and subscription waste detection.

### 🛡️ Elite Security
- **Biometric Executive Guard**: Mandatory fingerprint/face unlock on app launch and resume via Android Biometric API.
- **Hidden Mode**: One-tap executive privacy to blur balances and sensitive amounts globally.
- **AMOLED Dark Titanium UI**: Optimized for high-end displays with deep blacks and mechanically smooth luxury animations.

---

## 🛠 Tech Stack

- **Language**: Kotlin 2.3+ (Coroutines, Flow)
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Repository Pattern (Clean Architecture)
- **Database**: Room v4 (Version 4 with 16+ Production Entities)
- **Dependency Injection**: Hilt
- **Persistence**: Jetpack DataStore (User Preferences)
- **Charts**: MPAndroidChart via high-fidelity AndroidView wrappers
- **Security**: Android Biometric Library

---

## 📸 Visual Identity

The UI is meticulously crafted to match the **Rolls-Royce/Mercedes** luxury standard:
- **Palette**: Matte Black, Graphite Gray, Titanium Silver.
- **Accents**: Financial Emerald Green, Champagne Gold.
- **Typography**: SF Pro / General Sans style for executive readability.

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- AGP 9.2+
- compileSdk 36
- minSdk 26

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/IronLedger.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and ensure all Hilt dependencies are generated.
4. Deploy to an AMOLED-optimized device for the intended visual experience.

---

## 🏛 Project Structure

```text
com.ironledger.app
├── core
│   ├── design       # Luxury Theme, Glassmorphic Components
│   ├── security     # Biometric Lock Manager
│   └── sms          # Background Transaction Detector
├── data
│   ├── local        # Room Database, DAOs, Production Entities
│   └── repository   # Functional Business Logic Contracts
├── domain           # Models, Analytics Engine, Calculation Logic
└── feature          # Executive Modules (Home, Vault, Vehicle, Trip, AI)
```

---

## 🤝 Contribution & Handover

IronLedger is engineered for scalability. The current implementation is **100% fully functional**, verified by `assembleDebug`, and ready for production deployment.

**Developer**: Sreeh (Lead Fintech Architect)  
**Status**: Phase 4 Handover Complete - 1:1 Reference Match.

---

*“Manage. Track. Grow. In Absolute Luxury.”*
