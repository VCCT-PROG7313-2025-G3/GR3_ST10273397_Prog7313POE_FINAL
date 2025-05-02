# ThriftSense

**ThriftSense** is a personal finance tracking and budgeting app.  
The app enables users to manage their income, expenses, and budgeting habits through a user-friendly interface and local data storage.

---

## 📱 Features

- 🔐 User registration and login
- 👤 View and edit account details
- 💰 Create expenses with amount, category, date, description, and optional receipt photo
- 🧮 Create budgets with category-specific monthly limits and goals
- 📆 Filter expenses by date ranges
- 🌓 Toggle dark mode via the settings
- 🔄 Switch user and logout functionality
- 🧭 Navigation drawer and bottom navigation bar
- 💾 Data stored locally using Room Database

---

## 🏗️ Tech Stack

- Kotlin (Android)
- Room Database
- Jetpack (Fragments, ViewModel, LiveData)
- MVVM Architecture
- Material Design Components

---

## 📂 Project Structure

| Component                       | Purpose                                             |
|---------------------------------|-----------------------------------------------------|
| `MyHomeActivity.kt`             | Main screen shell with toolbar, drawer, and nav     |
| `HomeFragment.kt`               | Dashboard summary with balances and logo            |
| `SettingsFragment.kt`           | Dark mode toggle and currency selector              |
| `AccountDetailsFragment.kt`     | Edit/view user info                                 |
| `MyLoginActivity.kt`            | Handles user login                                  |
| `MyUserRegistrationActivity.kt` | Handles new user registration                       |
| `AppDatabase.kt`                | Room database setup                                 |
| `UserDAO.kt`                    | Interface to manage user data                       |

---

## 👨‍💻 Contributors

| Name               | Student ID   | Contribution                                        |
|--------------------|--------------|-----------------------------------------------------|
| Nicholas Wolfaardt | ST10273397   | Room DB, login system, GitHub setup, test scripts   |
| Ewald Pagel        | ST10257942   | Basic UI layout, Toolbar/Menu integration, README   |
| Michael Peterson   | ST10261925   | Expenses/Budgets functionality                      |
| Lidvin Megha       | ST10049585   | Filtering logic                                     |

---

## 🎥 Video Demonstration


---

## 🚀 How to Run

1. Clone the repository
2. Open in Android Studio Arctic Fox or later
3. Build and run on Android device or emulator (API 21+)
4. Register a new user and start managing expenses

---

## 📖 References

Programming 3C Module Manual

OpenAI. 2024. Chat-GPT (Version 3.5). [Large language model]. Available at: https://chat.openai.com/ [Accessed: 2 May 2025]
