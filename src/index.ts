import { registerPlugin } from '@capacitor/core';

import type { SmsDetectorPlugin } from './definitions';

const SmsDetector = registerPlugin<SmsDetectorPlugin>('SmsDetector', {
  web: () => import('./web').then((m) => new m.SmsDetectorWeb()),
});

export * from './definitions';
export { SmsDetector };
