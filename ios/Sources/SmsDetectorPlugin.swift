import Foundation
import Capacitor

@objc(SmsDetectorPlugin)
public class SmsDetectorPlugin: CAPPlugin {
    private var isListening = false
    
    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": value
        ])
    }
    
    @objc func startListening(_ call: CAPPluginCall) {
        isListening = true
        
        // Set up text field for SMS AutoFill
        DispatchQueue.main.async {
            if let rootVC = UIApplication.shared.windows.filter({ $0.isKeyWindow }).first?.rootViewController {
                // Set textContentType to oneTimeCode to enable SMS AutoFill
                self.setupSmsAutoFill(viewController: rootVC)
            }
        }
        
        call.resolve([
            "success": true
        ])
    }
    
    @objc func stopListening(_ call: CAPPluginCall) {
        isListening = false
        call.resolve([
            "success": true
        ])
    }
    
    @objc func hasPermission(_ call: CAPPluginCall) {
        // iOS doesn't require permissions for SMS AutoFill
        call.resolve([
            "granted": true
        ])
    }
    
    @objc func requestPermission(_ call: CAPPluginCall) {
        // iOS doesn't require permissions for SMS AutoFill
        call.resolve([
            "granted": true
        ])
    }
    
    private func setupSmsAutoFill(viewController: UIViewController) {
        // Find text input fields in the view hierarchy
        self.recursivelyFindTextFields(in: viewController.view)
        
        // Notify JavaScript that iOS setup is complete
        self.notifyListeners("iosAutoFillReady", data: [
            "message": "SMS AutoFill is ready. User must tap the text field to see the QuickType bar with OTP."
        ])
    }
    
    private func recursivelyFindTextFields(in view: UIView) {
        for subview in view.subviews {
            if let textField = subview as? UITextField {
                // Set textContentType to oneTimeCode to enable SMS AutoFill
                textField.textContentType = .oneTimeCode
                
                // Add a target to capture when text is changed
                textField.addTarget(self, action: #selector(textFieldDidChange(_:)), for: .editingChanged)
            }
            
            // Recursively search deeper
            recursivelyFindTextFields(in: subview)
        }
    }
    
    @objc private func textFieldDidChange(_ textField: UITextField) {
        if isListening, let text = textField.text, text.count >= 4 {
            // Check if the text looks like an OTP (numeric code)
            let otpPattern = "^[0-9]{4,6}$"
            if let regex = try? NSRegularExpression(pattern: otpPattern, options: []) {
                let range = NSRange(location: 0, length: text.utf16.count)
                if regex.firstMatch(in: text, options: [], range: range) != nil {
                    // Notify JS about detected OTP
                    self.notifyListeners("otpReceived", data: [
                        "code": text
                    ])
                }
            }
        }
    }
}