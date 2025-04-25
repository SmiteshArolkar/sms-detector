import { WebPlugin } from '@capacitor/core';

import type { SmsDetectorPlugin } from './definitions';

export class SmsDetectorWeb extends WebPlugin implements SmsDetectorPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
