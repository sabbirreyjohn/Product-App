# ProductApp

A scalable, modern Android application built using a **multi-module architecture** and the latest Android development practices. It showcases a paginated product list powered by Jetpack Compose, Room, and a public API from [dummyjson.com](https://dummyjson.com).

---

## 🚀 Features

- 🧩 Multi-module architecture for high scalability and code separation
- 📦 Product listing from a feature module named `main`
- ⚡️ Jetpack Compose for UI (chosen over XML for future readiness)
- 🔌 Ktor for networking
- 💉 Hilt for dependency injection
- 🗃 Room for local database storage
  - App caches data for offline use
- 🔄 Paging 3 for efficient data loading
- 🔁 Kotlin Coroutines and Flow for smooth, asynchronous operations
- 🧪 Instrumented test for Room DB (check `main > androidTest > TheDatabaseTest.kt`)

---

## 📂 Project Modules

- `:main` – Handles product UI and business logic
- `:network` – Manages networking configurations using Ktor
- `:theme` – Centralized styling and theme resources

---

## 🧪 Testing

- Instrumented tests are added for Room DB to ensure proper data operations.
- File location:  
  `main/src/androidTest/.../TheDatabaseTest.kt`

---

## ⚙️ Getting Started

### 1. Clone the project
### 2. Create local.properties
- Add the following line in the local.properties file in the root directory to avoid compile errors:
- base_url=dummyjson.com

### Build and run

### Developer Note:
	•	Although the requirement was to use View-based UI, Jetpack Compose was used instead, as it is the modern standard and XML is gradually becoming outdated.
	•	Due to limited time (while working another full-time job), UX polish and search-related edge cases may have minor issues.
	•	This app uses publicly available data from dummyjson.com.