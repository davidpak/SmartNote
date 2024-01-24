# Data Types

Data types are used to specify the format of data being sent to and from the server. They depend on the context of the data being sent.

## Plain Text

Plain text data types are used in HTTP requests outside of the body of the request, such as in query parameters and headers. All data is stored as UTF-8.

| Type | Description |
| ---- | ----------- |
| `string` | A string of characters. |
| `int` | An integer. |
| `float` | A floating point number. |
| `bool` | A boolean value. Either `1` or `0`. |

## Binary

Binary data is sent as part of the body of the request. All data are tightly packed, unless otherwise specified.

| Type | Description |
| ---- | ----------- |
| `uint32` | 32-bit unsigned integer. Stored in big-endian mode. |
| `bstring` | A UTF-8 encoded string of characters. Terminated with a single byte `0x00`. Length depends on content of string as UTF-8 is a variable length encoding. |
| `binary` | Stream of binary data. Length is dependent on use case. |
| `object` | A JSON object, stored in UTF-8. Length is dependent on use case. |
