package com.smitesh.plugins.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
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
        if (intent.getAction() != null && intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();
                        Log.d(TAG, "SMS received from: " + sender);
                        
                        String otp = extractOtp(messageBody);
                        if (otp != null && !otp.isEmpty()) {
                            Log.d(TAG, "OTP detected: " + otp);
                            if (plugin != null) {
                                plugin.onOtpReceived(otp);
                            }
                        }
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