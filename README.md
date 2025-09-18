# HelloGerman - German Learning App

A comprehensive Android application designed to help users learn German through interactive lessons covering reading, listening, writing, and speaking skills.

## 🚀 Features

### 📚 Multi-Skill Learning
- **Lesen (Reading)**: Comprehension exercises with authentic German texts
- **Hören (Listening)**: Audio-based lessons with Text-to-Speech support
- **Schreiben (Writing)**: Writing prompts and feedback system
- **Sprechen (Speaking)**: Real-time speech recognition and pronunciation analysis

### 🎯 Progressive Learning System
- **6 CEFR Levels**: A1, A2, B1, B2, C1, C2
- **Structured Lessons**: Organized by skill and difficulty level
- **Progress Tracking**: Monitor your learning journey with detailed analytics
- **Scoring System**: Get feedback on your performance

### 🎨 Modern UI/UX
- **Material Design 3**: Beautiful, modern interface
- **Dark/Light Theme**: Adaptive theming support
- **Responsive Design**: Optimized for various screen sizes
- **Intuitive Navigation**: Easy-to-use interface

### 🔧 Technical Features
- **Speech Recognition**: Real-time German speech analysis
- **Text-to-Speech**: High-quality German pronunciation
- **Offline Support**: Core functionality works without internet
- **Data Persistence**: Local database for progress tracking
- **AdMob Integration**: Monetization support

## 📱 Screenshots

*[Screenshots will be added here]*

## 🛠️ Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Database**: Room Database
- **Dependency Injection**: Manual DI
- **Build System**: Gradle with Kotlin DSL
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36 (Android 14)

## 📋 Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API 24+
- Kotlin 1.8+
- JDK 11+

## 🔧 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/HelloGerman.git
   cd HelloGerman
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the HelloGerman folder and select it

3. **Sync Gradle**
   - Wait for the initial Gradle sync to complete
   - If prompted, update Gradle wrapper

4. **Configure SDK**
   - Ensure you have Android SDK API 24+ installed
   - Set up Android SDK path in local.properties if needed

5. **Build and Run**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio
   - Select your target device and install the app

## 🎯 Usage

### Getting Started
1. Launch the app
2. Complete the onboarding process
3. Select your current German level (A1-C2)
4. Choose a skill to practice (Reading, Listening, Writing, Speaking)

### Learning Flow
1. **Select a Lesson**: Browse lessons by skill and level
2. **Complete Exercises**: Follow the interactive instructions
3. **Get Feedback**: Review your performance and scores
4. **Track Progress**: Monitor your improvement over time

### Speech Recognition (Speaking Lessons)
- Ensure microphone permission is granted
- Speak clearly in German
- Wait for real-time transcription
- Review feedback on pronunciation and vocabulary

### AdMob Test Ads (Development)
- In DEBUG builds, the app automatically uses Google test ad unit IDs.
- The emulator is configured as a test device via `RequestConfiguration`.
- No changes are required to switch; build a debug variant to see test ads.
- In release builds, production ad unit IDs are used automatically.

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/hellogerman/app/
│   │   ├── data/
│   │   │   ├── entities/          # Database entities
│   │   │   ├── repository/        # Data repositories
│   │   │   └── LessonContentGenerator.kt
│   │   ├── ui/
│   │   │   ├── screens/           # Compose screens
│   │   │   ├── components/        # Reusable UI components
│   │   │   ├── theme/             # App theming
│   │   │   └── viewmodel/         # ViewModels
│   │   ├── ads/                   # AdMob integration
│   │   └── MainActivity.kt
│   └── res/                       # Resources
└── build.gradle.kts
```

## 🔑 Key Components

### Data Layer
- **Room Database**: Local data persistence
- **Entities**: Lesson, UserProgress, and content models
- **Repository Pattern**: Data access abstraction

### UI Layer
- **Jetpack Compose**: Modern declarative UI
- **MVVM Architecture**: Separation of concerns
- **Material Design 3**: Consistent design system

### Features
- **Speech Recognition**: Android SpeechRecognizer API
- **Text-to-Speech**: Android TTS with German language support
- **Progress Tracking**: Comprehensive learning analytics
- **Ad Integration**: Google AdMob for monetization

## 🎨 Customization

### Adding New Lessons
1. Modify `LessonContentGenerator.kt`
2. Add new lesson content following the existing structure
3. Update the database with new lessons

### Theming
- Customize colors in `ui/theme/Color.kt`
- Modify typography in `ui/theme/Type.kt`
- Update theme in `ui/theme/Theme.kt`

### Localization
- Add new language support in `res/values-*/`
- Update string resources for different locales

## 🐛 Troubleshooting

### Common Issues

**Speech Recognition Not Working**
- Check microphone permissions
- Ensure internet connection is available
- Verify device supports speech recognition

**Build Errors**
- Clean and rebuild project
- Update Gradle dependencies
- Check Android SDK installation

**Performance Issues**
- Close other apps to free memory
- Restart the app if it becomes unresponsive
- Check device storage space

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- German language content and exercises
- Material Design 3 guidelines
- Android development community
- Jetpack Compose team

## 📞 Support

If you encounter any issues or have questions:
- Create an issue on GitHub
- Check the troubleshooting section
- Review the documentation

## 🔄 Version History

- **v1.0.0**: Initial release with core features
- Speech recognition and analysis
- Multi-skill learning system
- Progress tracking
- Modern Material Design 3 UI

---

**Happy Learning! 🇩🇪📚**
