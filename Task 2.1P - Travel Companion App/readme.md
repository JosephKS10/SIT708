# 🌍 Travel Companion App

An essential utility application built natively for Android, designed to help international travelers quickly and accurately convert critical values on the go. The app supports dynamic conversions across three main categories: Currency, Fuel Efficiency & Distance, and Temperature.

## 🚀 Key Features

* **Dynamic Category Selection:** UI automatically updates the available source and destination units based on the selected category (Currency, Fuel & Distance, or Temperature).
* **Hub-and-Spoke Currency Logic:** Utilizes a centralized base-unit conversion logic (USD) to efficiently calculate exchange rates across multiple global currencies without redundant code.
* **Robust Input Validation:** "Crash-proof" error handling that actively prevents app closures by intercepting:
  * Empty inputs
  * Non-numeric characters
  * Negative values (where physically impossible, like fuel efficiency)
  * Identity conversions (e.g., converting USD to USD)
* **Real-time UI Feedback:** Utilizes intuitive Android Toast messages and integrated `EditText` error icons to guide the user toward correct inputs.

## 🧮 Supported Conversions
*(Note: Currency rates are fixed to 2026 project specifications)*

**Currency:**
* USD (Base), AUD, EUR, JPY, GBP

**Fuel Efficiency & Distance:**
* Miles per Gallon (mpg) ↔ Kilometers per Liter (km/L)
* Gallons (US) ↔ Liters
* Nautical Miles ↔ Kilometers

**Temperature:**
* Celsius ↔ Fahrenheit ↔ Kelvin

## 🛠️ Tech Stack
* **Platform:** Android
* **Language:** Java
* **UI Toolkit:** XML (Views)
* **IDE:** Android Studio (Minimum SDK: API 24 / Nougat)

## 💻 Installation & Setup
To run this project locally on your machine:

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/JosephKS10/Travel-Companion-App.git](https://github.com/JosephKS10/Travel-Companion-App.git)