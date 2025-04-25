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

      // Start listening for SMS OTPs
      self.shadowRoot.querySelector('#start-listening').addEventListener('click', async function (e) {
        try {
          const permResult = await SmsDetector.hasPermission();
          if (!permResult.granted) {
            console.log('Requesting SMS permission...');
            await SmsDetector.requestPermission();
          }

          const result = await SmsDetector.startListening();
          console.log('Started listening for SMS:', result);
          
          // Add a listener for detected OTPs
          SmsDetector.addListener('otpReceived', (data) => {
            const otpDisplay = self.shadowRoot.querySelector('#otp-display');
            if (otpDisplay) {
              otpDisplay.textContent = data.code;
              
              // You could also send this to a web form
              // For example, if you're using this with a WebView:
              // webView.postMessage(JSON.stringify({ type: 'otp', code: data.code }));
            }
          });
          
          alert('Listening for SMS OTPs');
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
