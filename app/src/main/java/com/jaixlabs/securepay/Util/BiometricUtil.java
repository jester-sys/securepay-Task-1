package com.jaixlabs.securepay.Util;

import android.content.Context;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import java.util.concurrent.Executor;

public class BiometricUtil {
    public static void showBiometricPrompt(Context ctx, BiometricPrompt.AuthenticationCallback callback) {
        Executor executor = ContextCompat.getMainExecutor(ctx);
        BiometricPrompt prompt = new BiometricPrompt((FragmentActivity) ctx, executor, callback);

        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setDescription("Use fingerprint to authenticate")
                .setNegativeButtonText("Cancel")
                .build();

        prompt.authenticate(info);
    }
}
