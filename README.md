
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




