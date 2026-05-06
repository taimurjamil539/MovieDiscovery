# 🎬 MovieDiscover - Android App

A professional movie discovery application built with Kotlin,
MVVM architecture, Jetpack Navigation Component, Room Database,
Retrofit, and AdMob integration.

## 🛠 Tech Stack

| Technology         | Purpose                          |
|--------------------|----------------------------------|
| Kotlin             | Primary language                 |
| MVVM + LiveData    | Architecture pattern             |
| Navigation Component | Single-activity navigation    |
| Room Database      | Local persistence                |
| Retrofit + OkHttp  | Network API calls                |
| Glide              | Image loading                    |
| AdMob              | Native + Interstitial Ads        |
| Shimmer            | Loading skeleton effect          |
| Coroutines         | Async operations                 |

## ⚙️ Setup Instructions

### 1. Clone the repository
git clone https://github.com/taimurjamil539/MovieDiscovery.git
cd MovieDiscoveryApp

### 2. Open in Android Studio
Open Android Studio → File → Open → Select project folder

### 3. Configure AdMob (Production)
Replace test Ad Unit IDs in AdManager.kt:
- INTERSTITIAL_AD_ID = "your-real-interstitial-id"
- NATIVE_AD_ID = "your-real-native-ad-id"

Replace App ID in AndroidManifest.xml:
- com.google.android.gms.ads.APPLICATION_ID = "your-app-id"


### 4. Build & Run
./gradlew assembleDebug

## 📱 Features

✅ Splash screen with 5-second countdown
✅ Native ads on all screens
✅ API fetch → Room DB persistence
✅ Local image storage (Internal Storage)
✅ First launch vs subsequent launch logic
✅ Delete movie (DB + file)
✅ Share movie poster via Intent
✅ Interstitial ads before details navigation
✅ Exit confirmation BottomSheet
✅ Shimmer loading skeleton
✅ Collapsing toolbar in details
✅ MVVM clean architecture 

## 🏗 Architecture

SplashFragment ──► MoviesFragment ──► MovieDetailsFragment
                        │
                   SharedViewModel
                        │
                  MovieRepository
                  ┌─────┴──────┐
              ApiService    MovieDao
              (Retrofit)    (Room DB)
