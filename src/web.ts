import { WebPlugin } from '@capacitor/core';

import type { SmsDetectorPlugin } from './definitions';

export class SmsDetectorWeb extends WebPlugin implements SmsDetectorPlugin {
  async addListener(
    eventName: string, 
    listenerFunc: (...args: any[]) => any
  ): Promise<any> {
    const result = super.addListener(eventName, listenerFunc);
    return result as any;
  }

  async removeAllListeners(): Promise<void> {
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
  
  async getAppSignature(): Promise<{ signature: string }> {
    console.warn('App signature is not available on web');
    return { signature: '' };
  }
}