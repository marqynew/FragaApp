# Fraga - Social Fitness App

Oleh : 
- Ammar Qurthuby - 2208107010031
- M taris Rizki  - 2208107010047

## Features

### Authentication
- User registration and login
- Profile management
- Secure authentication using Firebase

### Social Features
- Follow/Unfollow users
- View followers and following lists
- Friend requests system
- User search functionality
- Profile viewing

### Profile Management
- Profile photo upload
- Bio and personal information
- Activity statistics
- Social statistics (followers/following)

### UI Components
- Material Design implementation
- Dark mode support
- Responsive layouts
- Modern UI elements

## Technical Stack

### Frontend
- Android SDK
- Material Design Components
- Glide (Image loading)
- ViewBinding

### Backend
Link FireBase : [https://console.firebase.google.com/project/fraga-4e157](https://console.firebase.google.com/project/fraga-4e157) 

- Firebase Authentication
- Firebase Firestore
- Firebase Storage
- Firebase Cloud Messaging (Planned)

The Firebase configuration file [google-services.json](google-services.json) located in the app/.

The [database.rules.json](database.rules.json) file as the security rules for Firebase Realtime Database.

### Architecture
- MVVM (Model-View-ViewModel) Architecture
- Repository Pattern
- LiveData
- ViewModel
- Coroutines

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/fraga/
│   │   │   ├── activities/
│   │   │   ├── adapters/
│   │   │   ├── fragments/
│   │   │   ├── models/
│   │   │   ├── repositories/
│   │   │   ├── utils/
│   │   │   └── viewmodels/
│   │   └── res/
│   │       ├── layout/
│   │       ├── values/
│   │       └── drawable/
└── build.gradle
```


## Dependencies

```gradle
dependencies {
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    implementation 'com.google.firebase:firebase-storage-ktx'

    // AndroidX
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    // Glide
    implementation 'com.github.bumptech.glide:glide:4.16.0'

    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3'
}
```

## Planned Features

- [ ] Activity tracking with Google Maps integration
- [ ] Social interactions (likes, comments)
- [ ] Push notifications
- [ ] Group activities
- [ ] Leaderboards
- [ ] Chat functionality
- [ ] Activity sharing

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

- Material Design Components
- Firebase
- Glide
- AndroidX Libraries

## Contact

Project Link: [https://github.com/marqynew/FragaApp.git](https://github.com/marqynew/FragaApp.git) 
