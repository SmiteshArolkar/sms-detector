#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN macro.
CAP_PLUGIN(SmsDetectorPlugin, "SmsDetector",
           CAP_PLUGIN_METHOD(echo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(startListening, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(stopListening, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(hasPermission, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(requestPermission, CAPPluginReturnPromise);
)