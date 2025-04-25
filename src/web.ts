import { WebPlugin } from '@capacitor/core';

import type { SmsDetectorPlugin } from './definitions';

export class SmsDetectorWeb extends WebPlugin implements SmsDetectorPlugin {
  // Override addListener but use type assertion to satisfy both types
  addListener(
    eventName: string, 
    listenerFunc: (...args: any[]) => any
  ): Promise<any> {
    // Call the original implementation
    const result = super.addListener(eventName, listenerFunc);
    // Return the result but TypeScript treats it as Promise<void>
    return result as any;
  }

  removeAllListeners(): Promise<void> {
    return super.removeAllListeners();
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async startListening(): Promise<{ success: boolean }> {
    console.warn('SMS detection is not available on web');
    return { success: false };
  }

  async stopListening(): Promise<{ success: boolean }> {
    console.warn('SMS detection is not available on web');
    return { success: false };
  }

  async hasPermission(): Promise<{ granted: boolean }> {
    console.warn('SMS permissions are not available on web');
    return { granted: false };
  }

  async requestPermission(): Promise<{ granted: boolean }> {
    console.warn('SMS permissions are not available on web');
    return { granted: false };
  }
}