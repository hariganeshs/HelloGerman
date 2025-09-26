# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Core Build Commands

### Gradle Tasks
```bash
# Clean build artifacts
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires keystore)
./gradlew assembleRelease

# Install debug build on connected device/emulator
./gradlew installDebug

# Run all unit tests
./gradlew test

# Run instrumented tests on connected device
./gradlew connectedAndroidTest

# Generate test reports
./gradlew testDebugUnitTest

# Check for lint issues
./gradlew lintDebug

# Build and run debug variant
./gradlew build
```

### Development Workflow
```bash
# Quick development cycle
./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk

# Run specific test class
./gradlew test --tests="*LessonContentGeneratorTest"

# Run specific instrumented test
./gradlew connectedAndroidTest --tests="*MainScreensTest"

# Clean and rebuild everything
./gradlew clean build
```

### Database Management
```bash
# Reset database (requires rebuild)
./gradlew clean && ./gradlew assembleDebug
# Database will reinitialize on app launch with sample data
```

## Architecture Overview

### High-Level Structure
HelloGerman is a German learning app built with **Jetpack Compose** using **MVVM architecture** with Repository pattern. The app features a comprehensive learning system covering reading, listening, writing, and speaking skills across CEFR levels A1-C2.

### Core Components

#### 1. Data Layer (`app/src/main/java/com/hellogerman/app/data/`)
- **Room Database**: `HelloGermanDatabase` (version 15) with extensive migration support
- **Entities**: UserProgress, Lesson, UserSubmission, GrammarProgress, Achievement, DictionaryCacheEntry, UserVocabulary
- **DAOs**: Comprehensive data access objects for each entity
- **Repository**: `HelloGermanRepository` - central data orchestration with business logic
- **Content Generation**: `LessonContentGenerator` - dynamic lesson creation system

#### 2. UI Layer (`app/src/main/java/com/hellogerman/app/ui/`)
- **Jetpack Compose**: Modern declarative UI framework
- **Navigation**: Type-safe navigation with `NavGraph` and `Screen` sealed classes
- **Responsive Design**: Adaptive layouts for different screen sizes
- **Theming**: Material Design 3 with dark/light theme support and custom theme selection

#### 3. Presentation Layer (`app/src/main/java/com/hellogerman/app/ui/viewmodel/`)
- **ViewModels**: MainViewModel, LessonViewModel, DictionaryViewModel, GrammarViewModel, etc.
- **State Management**: StateFlow and Flow for reactive data handling

#### 4. Feature Modules
- **Skills Training**: Separate screens for Lesen, Hören, Schreiben, Sprechen
- **Grammar System**: Comprehensive grammar lessons with progress tracking
- **Dictionary**: Multi-source dictionary with caching (`UnifiedDictionaryRepository`)
- **Gamification**: Achievement system with XP, coins, streaks
- **Vocabulary Management**: Personal vocabulary lists with spaced repetition

#### 5. External Integrations
- **AdMob**: Monetization with test/production ad configurations
- **Speech Recognition**: Android SpeechRecognizer API for speaking lessons
- **TTS**: Text-to-Speech for German pronunciation
- **API Services**: Multiple dictionary APIs (Wiktionary, Glosbe, LibreTranslate, etc.)

### Navigation Architecture
The app uses a single-activity architecture with Jetpack Navigation Compose:
- **MainActivity**: Single entry point with edge-to-edge support
- **Screen Routes**: Type-safe navigation through sealed Screen classes
- **Deep Linking**: Support for lesson and dictionary deep links
- **Animations**: Smooth transitions between screens with custom animations

### Database Design
Complex Room database with 15 migration versions supporting:
- User progress tracking across multiple skills and levels
- Lesson content with dynamic generation
- Achievement and gamification data
- Dictionary caching for offline functionality
- Personal vocabulary management
- Grammar progress with topic-based tracking

### Content Generation System
Dynamic lesson content creation through `LessonContentGenerator`:
- CEFR-aligned content (A1-C2)
- Skill-specific lesson types (reading, listening, writing, speaking)
- Grammar lessons with explanations and exercises
- Localization support (German/English explanations)
- Visual enhancement system with illustrations and characters

## Development Guidelines

### Code Organization
- **Package by Feature**: Organized by data, ui, presentation layers
- **Compose Components**: Reusable UI components in `ui/components/`
- **Dependency Injection**: Manual DI pattern (ready for Hilt migration)
- **Repository Pattern**: Single source of truth for data access

### Key Development Patterns
- **State Hoisting**: UI state managed at appropriate levels
- **Reactive Programming**: Extensive use of Flow for data streams
- **Error Handling**: Comprehensive error handling in repositories
- **Caching Strategy**: Multi-level caching for offline functionality

### Testing Structure
- **Unit Tests**: `app/src/test/` - Repository and business logic tests
- **Instrumented Tests**: `app/src/androidTest/` - UI and integration tests
- **Test Coverage**: Focus on critical paths like content generation and progress tracking

### Build Configuration
- **Gradle KTS**: Modern Kotlin DSL configuration
- **Version Catalog**: Centralized dependency management in `gradle/libs.versions.toml`
- **Build Variants**: Debug/Release with different ad configurations
- **Signing Config**: Release signing with dedicated keystore
- **ProGuard**: Code obfuscation and optimization for release builds

### Performance Considerations
- **Database**: Optimized queries with indexed columns
- **UI**: Efficient recomposition with state management best practices
- **Memory**: Proper lifecycle management and resource cleanup
- **Storage**: Compression and caching strategies for large datasets

### Localization
- **String Resources**: German (`values-de/`) and English (`values/`)
- **Dynamic Content**: Server-side content localization capability
- **RTL Support**: Ready for future RTL language support

### External Dependencies Management
All major dependencies managed through version catalog:
- Compose BOM for UI consistency
- Room for database persistence
- Navigation Compose for navigation
- Retrofit + OkHttp for networking
- WorkManager for background tasks
- DataStore for preferences

### Common Development Tasks

#### Adding New Lessons
1. Modify `LessonContentGenerator.kt` in the data package
2. Add new lesson content following existing structure patterns
3. Consider database migration if schema changes are needed
4. Test content generation with unit tests

#### Modifying UI Screens
1. Compose screens are in `ui/screens/`
2. Follow existing responsive design patterns
3. Update navigation routes in `Screen` sealed class
4. Maintain consistent theming and animations

#### Adding New Features
1. Create feature-specific packages in appropriate layers
2. Add necessary database entities and DAOs if data persistence required
3. Create ViewModels for state management
4. Update navigation and UI components
5. Add comprehensive tests

#### Database Schema Changes
1. Increment database version in `HelloGermanDatabase`
2. Create migration object following existing patterns
3. Test migration thoroughly on different data states
4. Consider fallback to destructive migration for major changes

### AdMob Integration Notes
- Debug builds automatically use test ad unit IDs
- Release builds require proper ad unit configuration
- Emulator configured as test device automatically
- Interstitial ads preloaded for better UX

This architecture supports rapid development while maintaining code quality, testability, and scalability for a comprehensive language learning application.