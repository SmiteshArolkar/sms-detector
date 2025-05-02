package com.smitesh.plugins.app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class AppSignatureHelper extends ContextWrapper {
    private static final String TAG = "AppSignatureHelper";
    private static final String HASH_TYPE = "SHA-256";
    public static final int NUM_HASHED_BYTES = 9;
    public static final int NUM_BASE64_CHAR = 11;

    public AppSignatureHelper(Context context) {
        super(context);
    }

    /**
     * Get the app signature for SMS Retriever API
     */
    public String getAppSignature() {
        ArrayList<String> appSignatures = new ArrayList<>();

        try {
            // Get all package signatures for the current package
            String packageName = getPackageName();
            PackageManager packageManager = getPackageManager();
            Signature[] signatures;

            try {
                // For Android API level 28 and above
                signatures = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                        .signingInfo.getApkContentsSigners();
                Log.d(TAG, "Using API 28+ method to get signatures");
            } catch (Exception e) {
                // For Android API level below 28
                signatures = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
                Log.d(TAG, "Using API <28 method to get signatures");
            }

            // For each signature create a compatible hash
            for (Signature signature : signatures) {
                String hash = hash(packageName, signature.toCharsString());
                if (hash != null) {
                    Log.d(TAG, "Generated hash: " + hash + " for signature: " + signature.hashCode());
                    appSignatures.add(String.format("%s", hash));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to get app signatures", e);
            e.printStackTrace();
        }

        if (appSignatures.isEmpty()) {
            Log.w(TAG, "No valid app signatures found");
            return "";
        }
        
        return appSignatures.get(0);
    }

    private static String hash(String packageName, String signature) {
        String appInfo = packageName + " " + signature;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_TYPE);
            messageDigest.update(appInfo.getBytes(StandardCharsets.UTF_8));
            byte[] hashSignature = messageDigest.digest();

            // Truncated the hash
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES);
            // Base64 encoding
            return Base64.encodeToString(hashSignature, Base64.NO_PADDING | Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Unable to get app hash", e);
            return null;
        }
    }
}