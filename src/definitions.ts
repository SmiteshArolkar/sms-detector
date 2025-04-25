export interface SmsDetectorPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
