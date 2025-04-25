# sms-detector

Sms detection for android and ios

## Install

```bash
npm install sms-detector
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`startListening()`](#startlistening)
* [`stopListening()`](#stoplistening)
* [`hasPermission()`](#haspermission)
* [`requestPermission()`](#requestpermission)
* [`addListener(string, ...)`](#addlistenerstring-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

Echo a value

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### startListening()

```typescript
startListening() => Promise<{ success: boolean; }>
```

Start listening for SMS messages to detect OTPs

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------


### stopListening()

```typescript
stopListening() => Promise<{ success: boolean; }>
```

Stop listening for SMS messages

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------


### hasPermission()

```typescript
hasPermission() => Promise<{ granted: boolean; }>
```

Check if the app has SMS permissions

**Returns:** <code>Promise&lt;{ granted: boolean; }&gt;</code>

--------------------


### requestPermission()

```typescript
requestPermission() => Promise<{ granted: boolean; }>
```

Request SMS permissions

**Returns:** <code>Promise&lt;{ granted: boolean; }&gt;</code>

--------------------


### addListener(string, ...)

```typescript
addListener(eventName: string, listenerFunc: (...args: any[]) => any) => Promise<PluginListenerHandle>
```

Add listener for OTP detection

| Param              | Type                                    |
| ------------------ | --------------------------------------- |
| **`eventName`**    | <code>string</code>                     |
| **`listenerFunc`** | <code>(...args: any[]) =&gt; any</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Remove listeners for OTP detection

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
