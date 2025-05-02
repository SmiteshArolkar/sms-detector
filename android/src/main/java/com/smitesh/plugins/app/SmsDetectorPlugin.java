package com.smitesh.plugins.app;

import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.content.IntentFilter;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

@CapacitorPlugin(
    name = "SmsDetector"
)
public class SmsDetectorPlugin extends Plugin {
    private static final String TAG = "SmsDetectorPlugin";
    private SmsRetrieverReceiver smsRetrieverReceiver;
    private boolean isSmsRetrieverActive = false;
    private AppSignatureHelper appSignatureHelper;

    @Override
    public void load() {
        super.load();
        
        // Initialize SMS Retriever receiver
        smsRetrieverReceiver = new SmsRetrieverReceiver();
        smsRetrieverReceiver.setPlugin(this);
        
        // Initialize AppSignatureHelper
        appSignatureHelper = new AppSignatureHelper(getContext());
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");
        JSObject ret = new JSObject();
        ret.put("value", value);
        call.resolve(ret);
    }

    @PluginMethod
    public void startListening(PluginCall call) {
        // Start SMS Retriever (doesn't require permissions)
        startSmsRetriever();
        
        JSObject ret = new JSObject();
        ret.put("success", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void stopListening(PluginCall call) {
        unregisterReceiver();
        
        JSObject ret = new JSObject();
        ret.put("success", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void hasPermission(PluginCall call) {
        // With SMS Retriever API, we don't need permissions
        JSObject ret = new JSObject();
        ret.put("granted", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void requestPermission(PluginCall call) {
        // With SMS Retriever API, we don't need permissions
        JSObject ret = new JSObject();
        ret.put("granted", true);
        call.resolve(ret);
    }
    
    @PluginMethod
    public void getAppSignature(PluginCall call) {
        String appSignature = appSignatureHelper.getAppSignature();
        JSObject ret = new JSObject();
        ret.put("signature", appSignature);
        call.resolve(ret);
    }

    /**
     * Start the SMS Retriever API
     */
    private void startSmsRetriever() {
        if (getActivity() == null) {
            Log.e(TAG, "Cannot start SMS Retriever: Activity is null");
            return;
        }
        
        // Get an instance of SmsRetrieverClient
        SmsRetrieverClient client = SmsRetriever.getClient(getActivity());
        
        // Start SMS Retriever
        Task<Void> task = client.startSmsRetriever();
        
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever
                Log.d(TAG, "SMS Retriever started successfully");
                isSmsRetrieverActive = true;
                
                // Generate and log app signature
                String appSignature = appSignatureHelper.getAppSignature();
                Log.d(TAG, "App hash for SMS Retriever: " + appSignature);
                
                // Register SMS Retriever receiver dynamically
                IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
                
                try {
                    ContextCompat.registerReceiver(
                        getActivity(), 
                        smsRetrieverReceiver, 
                        intentFilter,
                        "com.google.android.gms.auth.api.phone.permission.SEND",
                        null, 
                        ContextCompat.RECEIVER_NOT_EXPORTED
                    );
                } catch (Exception e) {
                    Log.e(TAG, "Failed to register receiver: " + e.getMessage());
                    // Fallback for older Android versions
                    ContextCompat.registerReceiver(getActivity(), smsRetrieverReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED);
                }
                
                JSObject statusObj = new JSObject();
                statusObj.put("method", "smsRetriever");
                statusObj.put("status", "started");
                statusObj.put("appHash", appSignature);
                notifyListeners("smsDetectorStatus", statusObj);
            }
        });
        
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever
                Log.e(TAG, "Failed to start SMS Retriever: " + e.getMessage());
                isSmsRetrieverActive = false;
                
                JSObject statusObj = new JSObject();
                statusObj.put("method", "smsRetriever");
                statusObj.put("status", "failed");
                statusObj.put("error", e.getMessage());
                notifyListeners("smsDetectorStatus", statusObj);
            }
        });
    }
    
    /**
     * Handle OTP received
     */
    public void onOtpReceived(String otp) {
        Log.d(TAG, "OTP detected: " + otp);
        JSObject otpObj = new JSObject();
        otpObj.put("code", otp);
        notifyListeners("otpReceived", otpObj);
        
        // Restart SMS Retriever after receiving an OTP
        startSmsRetriever();
    }
    
    /**
     * Handle SMS timeout
     */
    public void onSmsTimeout() {
        Log.d(TAG, "SMS timeout occurred");
        JSObject timeoutObj = new JSObject();
        timeoutObj.put("message", "SMS detection timed out after 5 minutes");
        notifyListeners("smsTimeout", timeoutObj);
        
        // Restart retriever
        startSmsRetriever();
    }

    /**
     * Unregister receiver
     */
    private void unregisterReceiver() {
        if (isSmsRetrieverActive && getActivity() != null) {
            try {
                getActivity().unregisterReceiver(smsRetrieverReceiver);
                isSmsRetrieverActive = false;
                Log.d(TAG, "SMS Retriever receiver unregistered");
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering SMS Retriever receiver", e);
            }
        }
    }

    @Override
    protected void handleOnDestroy() {
        unregisterReceiver();
        super.handleOnDestroy();
    }
}
