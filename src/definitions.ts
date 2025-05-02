import type { PluginListenerHandle } from "@capacitor/core";

export interface SmsDetectorPlugin {
  /**
   * Echo a value
   */
  echo(options: { value: string }): Promise<{ value: string }>;
  
  /**
   * Start listening for SMS messages to detect OTPs
   * On Android: Uses SMS Retriever API (no permissions required)
   * On iOS: Sets up SMS AutoFill (limited functionality)
   */
  startListening(): Promise<{ success: boolean }>;
  
  /**
   * Stop listening for SMS messages
   */
  stopListening(): Promise<{ success: boolean }>;
  
  /**
   * Check if the app has SMS permissions
   * Always returns true as SMS Retriever API doesn't need permissions
   */
  hasPermission(): Promise<{ granted: boolean }>;
  
  /**
   * Request SMS permissions
   * No-op as SMS Retriever API doesn't need permissions
   */
  requestPermission(): Promise<{ granted: boolean }>;
  
  /**
   * Get the app signature hash for SMS Retriever API
   * This hash must be included in the SMS for automatic retrieval
   */
  getAppSignature(): Promise<{ signature: string }>;
  
  /**
   * Add listener for events
   * - 'otpReceived': When an OTP is detected
   * - 'smsTimeout': When SMS detection times out (SMS Retriever API)
   * - 'smsDetectorStatus': Status updates for SMS detection methods
   * - 'iosAutoFillReady': iOS-specific event when AutoFill is ready
   */
  addListener(eventName: string, listenerFunc: (...args: any[]) => any): Promise<PluginListenerHandle>;
  
  /**
   * Remove listeners for all events
   */
  removeAllListeners(): Promise<void>;
}
