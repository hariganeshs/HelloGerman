# HelloGerman Release Keystore Information

## 🔐 **Keystore Details**

### **File Location**
- **Keystore File**: `app/hellogerman-release-key.jks`
- **Keystore Type**: JKS (Java KeyStore)
- **Algorithm**: RSA 2048-bit
- **Validity**: 10,000 days (approximately 27 years)

### **Keystore Credentials**
- **Store Password**: `hellogerman123`
- **Key Alias**: `hellogerman-key`
- **Key Password**: `hellogerman123`

### **Certificate Information**
- **Common Name (CN)**: HelloGerman
- **Organizational Unit (OU)**: Development
- **Organization (O)**: HelloGerman
- **Locality (L)**: City
- **State (ST)**: State
- **Country (C)**: US

## 📋 **Build Configuration**

The keystore is configured in `app/build.gradle.kts`:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("hellogerman-release-key.jks")
        storePassword = "hellogerman123"
        keyAlias = "hellogerman-key"
        keyPassword = "hellogerman123"
    }
}
```

## ⚠️ **IMPORTANT SECURITY NOTES**

### **Keystore Backup**
- **BACKUP THIS KEYSTORE FILE**: `app/hellogerman-release-key.jks`
- **Store it securely**: Keep it in a safe location
- **Never commit to Git**: The keystore file should be in `.gitignore`

### **Password Security**
- **Change passwords**: Consider changing the default passwords
- **Secure storage**: Store passwords in a password manager
- **Team access**: Share credentials securely with team members

### **Play Store Requirements**
- **Same keystore**: Must use the same keystore for all future updates
- **Lost keystore**: If lost, you cannot update the app on Play Store
- **New app**: Would require publishing as a completely new app

## 🚀 **Building Release AAB**

### **Command**
```bash
./gradlew bundleRelease
```

### **Output**
- **File**: `app/build/outputs/bundle/release/app-release.aab`
- **Size**: ~6.6MB
- **Signing**: Release-signed (not debug)

## 📱 **Play Store Upload**

### **Ready for Upload**
- ✅ **Release-signed AAB**: `HelloGerman-v1.0.0-release-signed.aab`
- ✅ **Proper signing**: Not debug-signed
- ✅ **Play Store compliant**: Meets all requirements

### **Upload Process**
1. Go to Google Play Console
2. Upload the release-signed AAB
3. Complete store listing
4. Submit for review

## 🔄 **Future Updates**

### **Version Updates**
- **Increment versionCode**: In `build.gradle.kts`
- **Update versionName**: If needed
- **Use same keystore**: Always use this keystore for updates

### **Example Version Update**
```kotlin
defaultConfig {
    versionCode = 2  // Increment this
    versionName = "1.0.1"  // Update if needed
}
```

## 📞 **Support**

### **If Keystore is Lost**
- Contact Google Play Support
- Provide app package name and details
- May need to publish as new app

### **If Passwords are Forgotten**
- Use keytool to change passwords
- Update build.gradle.kts accordingly

---

**Created**: January 27, 2025  
**For**: HelloGerman Android App  
**Purpose**: Release signing for Google Play Store
