# SMS Detector Capacitor Plugin

A Capacitor plugin for automatically detecting SMS OTP codes.

## Platform Support

### Android
Full support for automatic SMS OTP detection using SMS broadcast receivers.

### iOS
Limited support using iOS SMS AutoFill feature:
- Works only with iOS 12 and above
- Requires user interaction (tapping on the input field)
- The OTP must be in the format specified by Apple (carrier message format)
- Detection is not fully automatic as on Android

## Installation

```bash
npm install sms-detector
npx cap sync
```

## Usage

```typescript
import { SmsDetector } from 'sms-detector';

// Start listening for OTPs
async function startDetection() {
  const permResult = await SmsDetector.hasPermission();
  if (!permResult.granted) {
    await SmsDetector.requestPermission();
  }
  
  await SmsDetector.startListening();
  
  // Listen for OTPs
  SmsDetector.addListener('otpReceived', (data) => {
    console.log('Received OTP:', data.code);
    // Handle the OTP (autofill form, etc.)
  });
  
  // iOS-specific event
  SmsDetector.addListener('iosAutoFillReady', (data) => {
    console.log(data.message);
    // Inform the user to tap on the input field if needed
  });
}

// Stop detection
function stopDetection() {
  SmsDetector.stopListening();
  SmsDetector.removeAllListeners();
}
```

## iOS Specific Instructions

For iOS, make sure your input field for OTP is focused/tapped by the user. The iOS SMS AutoFill feature requires user interaction and will show suggested OTPs in the QuickType bar above the keyboard.

## Android Permissions

This plugin automatically handles the necessary SMS permissions on Android.
