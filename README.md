# Fraga - Social Fitness App


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
- Firebase Authentication
- Firebase Firestore
- Firebase Storage
- Firebase Cloud Messaging (Planned)

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

## Setup

1. Clone the repository
```bash
git clone https://github.com/yourusername/fraga.git
```

2. Open the project in Android Studio

3. Add your Firebase configuration
   - Create a new Firebase project
   - Add your `google-services.json` to the app directory
   - Enable Authentication and Firestore in Firebase Console

4. Build and run the project

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
- [ ] Privacy settings
- [ ] Activity sharing

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

- Material Design Components
- Firebase
- Glide
- AndroidX Libraries

## Contact

Project Link: [https://github.com/yourusername/fraga](https://github.com/yourusername/fraga) 