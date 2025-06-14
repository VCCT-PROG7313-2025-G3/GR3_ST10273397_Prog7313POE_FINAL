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
- 🔄 Multiple accounts in one, user and logout functionality
- 🧭 Navigation drawer and bottom navigation bar
- 💾 Data stored using Firebase

---

## 🏗️ Tech Stack

- Kotlin (Android)
- Room Database
- Jetpack (Fragments, ViewModel, LiveData)
- MVVM Architecture
- Material Design Components

---

## 📂 Project Structure

| Component               | Purpose                                             |
|-------------------------|-----------------------------------------------------|
| `MyHomeActivity.kt`     | Main screen shell with toolbar, drawer, and nav     |
| `HomeFragment.kt`       | Dashboard summary with balances and logo            |
| `SettingsFragment.kt`   | Dark mode toggle and currency selector              |
| `AccountDetailsFragment.kt` | Edit/view user info                           |
| `MyLoginActivity.kt`    | Handles user login                                  |
| `MyUserRegistrationActivity.kt` | Handles new user registration              |
| `AppDatabase.kt`        | Room database setup                                 |
| `UserDAO.kt`            | Interface to manage user data                       |

---

## 👨‍💻 Contributors

| Name              | Student ID   | Contribution                                        |
|-------------------|--------------|-----------------------------------------------------|
| Nicholas Wolfaardt| ST10273397   | Room DB, login system, GitHub setup, test scripts   |
| Ewald Pagel       | ST10257942   | Basic UI layout, Toolbar/Menu integration, README   |
| Michael Peterson  | ST10261925   | Expenses/Budgets functionality                      |
| Lidvin Megha      | ST10049585   | Filtering logic, exception handling                 |

---

## 🎥 Video Demonstration

https://youtu.be/qJldq-LnXPo?si=jeNHzLzMr2VkEjMx

https://youtube.com/shorts/qJldq-LnXPo?si=REJvdGqAqTK2CbkB
---

## 🚀 How to Run

1. Clone the repository
2. Open in Android Studio Meerkat
3. Build and run on Pixel 9 Android 16.0 Baklava (API 36)
4. Register a new user and start managing expenses

---

## 🔗 GitHub Link:

https://github.com/VCCT-PROG7313-2025-G3/GR3_ST10273397_Prog7313POE_FINAL.git

---

## 📖 References

Programming 3C Module Manual

OpenAI. 2025. Chat-GPT (Version 3.5). [Large language model]. Available at: https://chat.openai.com/ [Accessed: 02 May 2025].  
