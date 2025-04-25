import type { PluginListenerHandle } from "@capacitor/core";

export interface SmsDetectorPlugin {
  /**
   * Echo a value
   */
  echo(options: { value: string }): Promise<{ value: string }>;
  
  /**
   * Start listening for SMS messages to detect OTPs
   */
  startListening(): Promise<{ success: boolean }>;
  
  /**
   * Stop listening for SMS messages
   */
  stopListening(): Promise<{ success: boolean }>;
  
  /**
   * Check if the app has SMS permissions
   */
  hasPermission(): Promise<{ granted: boolean }>;
  
  /**
   * Request SMS permissions
   */
  requestPermission(): Promise<{ granted: boolean }>;
  
  /**
   * Add listener for OTP detection
   */
  addListener(eventName: string, listenerFunc: (...args: any[]) => any): Promise<PluginListenerHandle>;
  
  /**
   * Remove listeners for OTP detection
   */
  removeAllListeners(): Promise<void>;
}
