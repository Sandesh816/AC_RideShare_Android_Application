# AC_RideShare_Android_Application🚗
Putting every seat to work during Amherst College breaks.

## ✨ What is AC RideShare?
Every holiday hundreds of Amherst College students head to Boston, NYC, or home—often in half-empty cars or individual Ubers.  
**AC RideShare** matches drivers and riders in real-time, so cars run full, costs drop, and carbon footprints shrink.

**Highlights**
* Publish or search rides in real-time (Firestore snapshots).
* Join / leave with seat-count validation.
* In-ride group chat (read receipts, lazy-load).
* Upcoming vs Past tabs auto-filter rides.
* Profile editor (name, phone OTP, email verification).
* Modern Material 3 UI (Compose, bottom bar + FAB).
* Offline cache, coroutine Flows, Hilt DI.

## 📸 Screenshots
| Home | Search | Create Ride | Current Ride |
|------|--------|-------------|--------------|
![Screenshot_1747315639](https://github.com/user-attachments/assets/6a30d37d-8f44-4cb3-867e-d7a0e4b4cd87)
![Screenshot_1747315701](https://github.com/user-attachments/assets/e1b63fab-3622-40ca-97a9-4389ec4f5186)
![Screenshot_1747315727](https://github.com/user-attachments/assets/5ad0b96d-dfc0-4e94-8372-22221d6ed96c)
![Screenshot_1747315736](https://github.com/user-attachments/assets/735d8c8b-df83-4d82-a55e-10009a43770d)
![Screenshot_1747315741](https://github.com/user-attachments/assets/d7abaf39-2480-4594-98d5-89cb06217861)

## 🏗 Architecture
Jetpack Compose ──► ViewModels (Hilt + Coroutines / Flow) ──► Repositories
Firebase Auth + Firestore (offline-enabled)
* **DI** – Hilt  
* **Async** – Kotlin Coroutines / Kotlin Flow  
* **DB** – Firebase Firestore (users, rides, messages)

## 🔧 Setup
1. **Clone**
   ```bash
   git clone [https://github.com/<your-user>/ac-rideshare.git](https://github.com/Sandesh816/AC_RideShare_Android_Application.git)
   cd ac-rideshare

2. **Firebase**
	•	Create a Firebase project.
	•	Enable Email/Password and Phone sign-in methods.
	•	Add Android app package com.example.acrideshare.
	•	Download google-services.json into the app/ folder.

3. **Run**
   ./gradlew installDebug

## 🗺 Project Map
Path  Purpose
app/src/main/java  Compose screens, ViewModels, DI modules
app/src/main/res   Material 3 theme, strings, icons
