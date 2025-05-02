import { SplashScreen } from '@capacitor/splash-screen';
import { Camera } from '@capacitor/camera';
import { SmsDetector } from 'sms-detector';

window.customElements.define(
  'capacitor-welcome',
  class extends HTMLElement {
    constructor() {
      super();

      SplashScreen.hide();

      const root = this.attachShadow({ mode: 'open' });

      root.innerHTML = `
    <style>
      :host {
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        display: block;
        width: 100%;
        height: 100%;
      }
      h1, h2, h3, h4, h5 {
        text-transform: uppercase;
      }
      .button {
        display: inline-block;
        padding: 10px;
        background-color: #73B5F6;
        color: #fff;
        font-size: 0.9em;
        border: 0;
        border-radius: 3px;
        text-decoration: none;
        cursor: pointer;
        margin-right: 5px;
      }
      main {
        padding: 15px;
      }
      main hr { height: 1px; background-color: #eee; border: 0; }
      main h1 {
        font-size: 1.4em;
        text-transform: uppercase;
        letter-spacing: 1px;
      }
      main h2 {
        font-size: 1.1em;
      }
      main h3 {
        font-size: 0.9em;
      }
      main p {
        color: #333;
      }
      main pre {
        white-space: pre-line;
      }
      #otp-display {
        font-size: 24px;
        font-weight: bold;
        padding: 10px;
        background-color: #f5f5f5;
        border-radius: 5px;
        margin: 10px 0;
        display: inline-block;
      }
    </style>
    <div>
      <capacitor-welcome-titlebar>
        <h1>Capacitor SMS Detector</h1>
      </capacitor-welcome-titlebar>
      <main>
        <h1>SMS OTP Detector</h1>
        <p>
          This plugin automatically detects OTP codes from incoming SMS messages and forwards them to your web app.
        </p>
        <h2>Detected OTP</h2>
        <div id="otp-display">No OTP detected yet</div>
        <p>
          <button class="button" id="start-listening">Start Listening</button>
          <button class="button" id="stop-listening">Stop Listening</button>
          <button class="button" id="check-permission">Check Permission</button>
        </p>
        <h2>Take a photo</h2>
        <p>
          <button class="button" id="take-photo">Take Photo</button>
        </p>
        <p>
          <img id="image" style="max-width: 100%">
        </p>
        <h2>SMS Format for Testing</h2>
        <p>
          <button class="button" id="show-sms-format">Show SMS Format</button>
        </p>
        <div id="sms-format-container"></div>
        <div id="hash-container"></div>
      </main>
    </div>
    `;
    }

    connectedCallback() {
      const self = this;

      // Photo functionality
      self.shadowRoot.querySelector('#take-photo').addEventListener('click', async function (e) {
        try {
          const photo = await Camera.getPhoto({
            resultType: 'uri',
          });

          const image = self.shadowRoot.querySelector('#image');
          if (!image) {
            return;
          }

          image.src = photo.webPath;
        } catch (e) {
          console.warn('User cancelled', e);
        }
      });

      // Start listening for SMS OTPs and show app hash for testing
      self.shadowRoot.querySelector('#start-listening').addEventListener('click', async function (e) {
        try {
          // Start the listener and get back the app hash
          const result = await SmsDetector.startListening();
          console.log('Started listening for SMS:', result);
          
          // Show the app hash for testing
          if (result.appSignature) {
            const hashDisplay = document.createElement('div');
            hashDisplay.style.padding = '10px';
            hashDisplay.style.backgroundColor = '#f8f9fa';
            hashDisplay.style.border = '1px solid #ddd';
            hashDisplay.style.borderRadius = '5px';
            hashDisplay.style.marginTop = '10px';
            hashDisplay.style.fontFamily = 'monospace';
            
            hashDisplay.innerHTML = `
              <h4>App Hash (copied to clipboard):</h4>
              <code>${result.appSignature}</code>
              <h4>Test SMS Format:</h4>
              <code>&lt;#&gt; Your verification code is: 123456 ${result.appSignature}</code>
            `;
            
            const container = self.shadowRoot.querySelector('#hash-container');
            if (container) {
              container.innerHTML = '';
              container.appendChild(hashDisplay);
            }
          }
          
          // Set up event listeners
          SmsDetector.addListener('otpReceived', (data) => {
            console.log('OTP received:', data.code);
            const otpDisplay = self.shadowRoot.querySelector('#otp-display');
            if (otpDisplay) {
              otpDisplay.textContent = data.code;
            }
          });
          
          alert('Listening for SMS OTPs. App hash copied to clipboard.');
        } catch (err) {
          console.error('Error starting SMS detector:', err);
        }
      });

      // Stop listening for SMS OTPs
      self.shadowRoot.querySelector('#stop-listening').addEventListener('click', async function (e) {
        try {
          const result = await SmsDetector.stopListening();
          console.log('Stopped listening for SMS:', result);
          SmsDetector.removeAllListeners();
          alert('Stopped listening for SMS OTPs');
        } catch (err) {
          console.error('Error stopping SMS detector:', err);
        }
      });

      // Check SMS permission
      self.shadowRoot.querySelector('#check-permission').addEventListener('click', async function (e) {
        try {
          const result = await SmsDetector.hasPermission();
          alert('SMS permission granted: ' + result.granted);
        } catch (err) {
          console.error('Error checking SMS permission:', err);
        }
      });

      // Add a function to show the SMS format required for automatic detection
      async function showSmsFormat() {
        try {
          const signatureResult = await SmsDetector.getAppSignature();
          const appSignature = signatureResult.signature;
          
          if (appSignature) {
            const formatDiv = document.createElement('div');
            formatDiv.style.padding = '10px';
            formatDiv.style.backgroundColor = '#f8f9fa';
            formatDiv.style.border = '1px solid #ddd';
            formatDiv.style.borderRadius = '5px';
            formatDiv.style.marginTop = '10px';
            formatDiv.style.fontFamily = 'monospace';
            formatDiv.style.whiteSpace = 'pre-wrap';
            
            formatDiv.innerHTML = `<strong>SMS Format for Testing:</strong>
<code>&lt;#&gt; Your verification code is: 123456
${appSignature}</code>

<p>Make sure to include the app hash at the end of your SMS for automatic detection!</p>`;
            
            const container = self.shadowRoot.querySelector('#sms-format-container');
            container.innerHTML = '';
            container.appendChild(formatDiv);
          }
        } catch (err) {
          console.error('Error getting app signature:', err);
        }
      }

      // Add this button to the HTML
      self.shadowRoot.querySelector('#show-sms-format').addEventListener('click', showSmsFormat);

      self.shadowRoot.querySelector('#get-hash').addEventListener('click', async function (e) {
        try {
          const signatureResult = await SmsDetector.getAppSignature();
          const appSignature = signatureResult.signature;
          alert('Your app hash: ' + appSignature);
        } catch (err) {
          console.error('Error getting app signature:', err);
        }
      });
    }
  },
);

window.customElements.define(
  'capacitor-welcome-titlebar',
  class extends HTMLElement {
    constructor() {
      super();
      const root = this.attachShadow({ mode: 'open' });
      root.innerHTML = `
    <style>
      :host {
        position: relative;
        display: block;
        padding: 15px 15px 15px 15px;
        text-align: center;
        background-color: #73B5F6;
      }
      ::slotted(h1) {
        margin: 0;
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
        font-size: 0.9em;
        font-weight: 600;
        color: #fff;
      }
    </style>
    <slot></slot>
    `;
    }
  },
);
