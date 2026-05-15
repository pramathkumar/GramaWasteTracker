# 🚜 Grama Waste Tracker
### Smart Village Waste Management System

Grama Waste Tracker is an Android-based smart waste management application designed for villages and rural communities. The application helps residents report garbage blackspots and enables administrators to monitor waste collection vehicles in real time using GPS tracking and Firebase cloud services.

---

# 📱 Features

## 👤 Resident Features
- Resident Login & Signup
- Live Tractor Tracking on Google Maps
- Garbage Blackspot Reporting
- Image Upload with Location
- Nearby Tractor Notifications
- Waste Segregation Guide
- Real-time Map Updates

---

## 👨‍💼 Admin Features
- Secure Admin Login
- Live GPS Tractor Tracking
- Realtime Garbage Report Dashboard
- Delete Cleaned Reports
- Firebase Cloud Synchronization
- Role-Based Access Control

---

# 🗺️ System Workflow

1. Resident reports garbage with image and GPS location.
2. Image uploads to Firebase Storage.
3. Report data stores in Firebase Realtime Database.
4. Admin device updates tractor GPS location continuously.
5. Residents receive nearby tractor notifications.
6. Admin removes reports after cleanup.
7. Google Maps updates markers in real time.

---

# 🛠 Technologies Used

- Kotlin
- Android Studio
- Jetpack Compose
- Firebase Authentication
- Firebase Realtime Database
- Firebase Storage
- Google Maps SDK
- Google Play Services Location API
- Material 3
- Coil Image Library

---

# 🔥 Screenshots

## Login Screen
<img width="1080" height="2412" alt="image" src="https://github.com/user-attachments/assets/adeb4616-a34c-42a1-b0df-ff4525e5d22a" />


## Resident Dashboard
<img width="1080" height="2412" alt="image" src="https://github.com/user-attachments/assets/e0c7c88e-4469-4931-b478-eecf20230d5c" />


## Admin Dashboard
<img width="716" height="1600" alt="image" src="https://github.com/user-attachments/assets/83fc1a2a-a5d3-4f0c-a6f1-3361771ac4ca" />


## Live Map Tracking
<img width="716" height="1600" alt="image" src="https://github.com/user-attachments/assets/2d3dd0c1-cd10-4a20-aa1e-e353eea3d212" />


---

# ⚙️ Firebase Setup

## Enable:
- Firebase Authentication
- Firebase Realtime Database
- Firebase Storage

---

## Add `google-services.json`

Place inside:

```text
app/google-services.json
