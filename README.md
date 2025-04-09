
# 💳 Android App – Secure Transactions with Biometric Auth & API Integration

This Android application demonstrates **secure user login**, **transaction listing**, and **biometric authentication** for subsequent access. The app is built using **Java**, integrates with REST APIs, and uses **modern Android security standards** like `EncryptedSharedPreferences` and **BiometricPrompt API**.

> ✅ **Note**: App name and icon are AI-generated placeholders for demo purposes.

---

##  Features Implemented

✅ **Login API Integration**  
✅ **Transaction API Integration**  
✅ **Biometric Authentication** (Fingerprint)  
✅ **EncryptedSharedPreferences** for Secure Token Storage  
✅ **Logout with Confirmation Dialog**  
✅ **Dark Mode + Light Mode Support**  
✅ **Category Filtering via Spinner**  
✅ **Offline Mode** with Room Database (Bonus)  

---

##  API Endpoints Used

1. **Login**  
   `POST https://api.prepstripe.com/login`  
   Payload: `{ "username": "user", "password": "pass" }`

2. **Transactions**  
   `GET https://api.prepstripe.com/transactions`  
   Requires `Authorization: Bearer <token>` header

---

##  Biometric Authentication Code

```java
BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
    .setTitle("Biometric Login")
    .setSubtitle("Log in using your fingerprint")
    .setNegativeButtonText("Use Account Password")
    .build();

BiometricPrompt biometricPrompt = new BiometricPrompt(
    this, ContextCompat.getMainExecutor(this),
    new BiometricPrompt.AuthenticationCallback() {
        @Override
        public void onAuthenticationSucceeded(AuthenticationResult result) {
            // Proceed to Home
        }
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            // Handle error
        }
    }
);

biometricPrompt.authenticate(promptInfo);
```

EncryptedSharedPreferences Code
```
SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
    "secure_prefs",
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    context,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
);

// Store token
sharedPreferences.edit().putString("auth_token", token).apply();

// Retrieve token
String token = sharedPreferences.getString("auth_token", null);

SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
    "secure_prefs",
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    context,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
);

// Store token
sharedPreferences.edit().putString("auth_token", token).apply();

// Retrieve token
String token = sharedPreferences.getString("auth_token", null);
```
Logout Functionality
```
new MaterialAlertDialogBuilder(context)
    .setTitle("Logout")
    .setMessage("Are you sure you want to logout?")
    .setIcon(R.drawable.ic_logout)
    .setPositiveButton("Yes", (dialog, which) -> {
        sharedPreferences.edit().clear().apply();
        // Navigate to Login
    })
    .setNegativeButton("No", null)
    .show();
```

UI Previews

 Dark Theme
<img src="https://github.com/user-attachments/assets/47f11981-a720-4ec4-9776-add711e6cded" width="100"/> <img src="https://github.com/user-attachments/assets/c628dc53-f72c-4c53-9e8c-f59c24e4ec6f" width="100"/> <img src="https://github.com/user-attachments/assets/1a4646f7-8b75-4602-b68e-3813aba5c5c5" width="100"/> <img src="https://github.com/user-attachments/assets/ab0b64c9-b015-498f-9e13-c397e1b53e7d" width="100"/> <img src="https://github.com/user-attachments/assets/9b4088bc-0c62-4f9c-94b9-61c6196aabaa" width="100"/> <img src="https://github.com/user-attachments/assets/a3e08e9f-8699-4530-9a86-9b2bc8cff51c" width="100"/> <img src="https://github.com/user-attachments/assets/09c6812b-e697-4fc6-86c4-4edbe834c8dc" width="100"/>

 Light Theme
<img src="https://github.com/user-attachments/assets/08fc17d7-03ce-4241-8eef-d672ba2bb0f2" width="100"/> <img src="https://github.com/user-attachments/assets/8ce05ed2-6c15-40af-be7c-1c915a24e340" width="100"/> <img src="https://github.com/user-attachments/assets/3caaa725-5223-4776-aacd-5cd3f712d07a" width="100"/> <img src="https://github.com/user-attachments/assets/6f217a21-090c-4816-b4fd-2be54e1d4a23" width="100"/> <img src="https://github.com/user-attachments/assets/2c01ac91-1689-42fc-962c-52cd44af795f" width="100"/> <img src="https://github.com/user-attachments/assets/a633cfc6-3ad0-48a6-9599-ac33d5d4ec28" width="100"/> <img src="https://github.com/user-attachments/assets/52c63100-a085-47c4-b917-3db7e3f2ee64" width="100"/>


Filter/Category Search via Spinner

Implemented category-based transaction filtering using a Spinner dropdown menu to allow users to narrow down results.
🛠 Tech Stack
```
    Java

    MVVM Architecture

    Retrofit (API Calls)

    EncryptedSharedPreferences

    BiometricPrompt API

    Room DB (for Offline Mode)

    Material Design Components ```
```
Folder Structure

├── ui/
│   ├── LoginFragment.java
│   ├── HomeFragment.java
│   ├── BiometricActivity.java
├── viewmodel/
│   └── TransactionViewModel.java
├── repository/
│   └── TransactionRepository.java
├── model/
│   └── Transaction.java
├── network/
│   └── ApiService.java
└── utils/
    └── AuthManager.java

🧑‍💻 Developer Notes

    The app is built to demonstrate security-first Android development.

    Code is modular and scalable.

    Designed with simplicity and clarity for new developers and reviewers.

📦 APK & Run Instructions

    Clone the repo

    Open in Android Studio

    Build APK or Run on Emulator

    Test using dummy credentials provided by the backend team


