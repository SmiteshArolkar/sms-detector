package com.smitesh.plugins.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsRetrieverReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsRetrieverReceiver";
    private SmsDetectorPlugin plugin;

    // OTP patterns - adjust these patterns based on your specific requirements
    private static final Pattern[] OTP_PATTERNS = new Pattern[] {
        // Pattern for 4-6 digit OTP
        Pattern.compile("([0-9]{4,6})"),
        
        // Common phrases before OTP
        Pattern.compile("code[^0-9]*([0-9]{4,6})", Pattern.CASE_INSENSITIVE),
        Pattern.compile("otp[^0-9]*([0-9]{4,6})", Pattern.CASE_INSENSITIVE),
        Pattern.compile("verification[^0-9]*([0-9]{4,6})", Pattern.CASE_INSENSITIVE),
        Pattern.compile("password[^0-9]*([0-9]{4,6})", Pattern.CASE_INSENSITIVE),
        Pattern.compile("one time password[^0-9]*([0-9]{4,6})", Pattern.CASE_INSENSITIVE)
    };

    public void setPlugin(SmsDetectorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                
                if (status != null) {
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            // Get SMS message contents
                            String message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE);
                            
                            // Get SMS sender address (optional, only in newer GMS versions)
                            String senderAddress = extras.getString(SmsRetriever.EXTRA_SMS_ORIGINATING_ADDRESS);
                            
                            Log.d(TAG, "SMS retrieved from: " + 
                                (senderAddress != null ? senderAddress : "unknown sender"));
                            
                            if (message != null) {
                                String otp = extractOtp(message);
                                if (otp != null && !otp.isEmpty()) {
                                    Log.d(TAG, "OTP detected (SMS Retriever): " + otp);
                                    if (plugin != null) {
                                        plugin.onOtpReceived(otp);
                                    }
                                }
                            }
                            break;

                        case CommonStatusCodes.TIMEOUT:
                            // Waiting for SMS timed out (5 minutes)
                            Log.d(TAG, "SMS retriever timeout occurred");
                            if (plugin != null) {
                                plugin.onSmsTimeout();
                            }
                            break;
                            
                        default:
                            Log.d(TAG, "SMS retrieval failed with status: " + status.getStatusCode());
                            break;
                    }
                }
            }
        }
    }

    private String extractOtp(String message) {
        if (message == null || message.isEmpty()) {
            return null;
        }

        // Try each pattern until we find a match
        for (Pattern pattern : OTP_PATTERNS) {
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        
        return null;
    }
}