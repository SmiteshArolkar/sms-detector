package com.smitesh.plugins.app;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(
    name = "SmsDetector",
    permissions = {
        @Permission(
            alias = "sms",
            strings = {
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
            }
        )
    }
)
public class SmsDetectorPlugin extends Plugin {
    private static final String TAG = "SmsDetectorPlugin";
    private SmsDetector implementation = new SmsDetector();
    private SmsReceiver smsReceiver;
    private boolean isReceiverRegistered = false;

    @Override
    public void load() {
        super.load();
        smsReceiver = new SmsReceiver();
        smsReceiver.setPlugin(this);
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void startListening(PluginCall call) {
        if (hasRequiredPermissions()) {
            registerReceiver();
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } else {
            requestPermissionForAlias("sms", call, "smsPermsCallback");
        }
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
        JSObject ret = new JSObject();
        ret.put("granted", hasRequiredPermissions());
        call.resolve(ret);
    }

    @PluginMethod
    public void requestPermission(PluginCall call) {
        if (hasRequiredPermissions()) {
            JSObject ret = new JSObject();
            ret.put("granted", true);
            call.resolve(ret);
        } else {
            requestPermissionForAlias("sms", call, "smsPermsCallback");
        }
    }

    @PermissionCallback
    private void smsPermsCallback(PluginCall call) {
        if (hasRequiredPermissions()) {
            if (call.getMethodName().equals("startListening")) {
                registerReceiver();
                JSObject ret = new JSObject();
                ret.put("success", true);
                call.resolve(ret);
            } else {
                JSObject ret = new JSObject();
                ret.put("granted", true);
                call.resolve(ret);
            }
        } else {
            JSObject ret = new JSObject();
            if (call.getMethodName().equals("startListening")) {
                ret.put("success", false);
            } else {
                ret.put("granted", false);
            }
            call.resolve(ret);
        }
    }

    public void onOtpReceived(String otp) {
        Log.d(TAG, "OTP detected: " + otp);
        JSObject otpObj = new JSObject();
        otpObj.put("code", otp);
        notifyListeners("otpReceived", otpObj);
    }

    public boolean hasRequiredPermissions() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            getActivity().registerReceiver(smsReceiver, intentFilter);
            isReceiverRegistered = true;
            Log.d(TAG, "SMS receiver registered");
        }
    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            try {
                getActivity().unregisterReceiver(smsReceiver);
                isReceiverRegistered = false;
                Log.d(TAG, "SMS receiver unregistered");
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering SMS receiver", e);
            }
        }
    }

    @Override
    protected void handleOnDestroy() {
        unregisterReceiver();
        super.handleOnDestroy();
    }
}
