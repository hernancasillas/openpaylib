# openpaylib

test description

## Installation

```sh
npm install openpaylib
```

## Usage

```js
import * as OpenPayLib from 'openpaylib';

// ...

OpenPayLib.initialize('MerchantID', 'PK', isProduction)

OpenPayLib.getDeviceId((deviceId) => {
    console.log("s ==> " + deviceId);
})
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
