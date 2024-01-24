# Data Types

Data types are used to specify the format of data being sent to and from the server. They depend on the context of the data being sent. Generally, plain text data is used for all data except request and response bodies, which use binary data.

[Go back to the main documentation](../README.md)

## Plain Text

Text data is stored in UTF-8. The following types are recognized:

| Type | Description |
| ---- | ----------- |
| `string` | A string of characters. |
| `int` | An integer. |
| `float` | A floating point number. |
| `bool` | A boolean value. Either `1` or `0`. |

## Binary

Binary data is stored in little-endian and is tightly packed. The following types are recognized:

| Type | Description |
| ---- | ----------- |
| `uint32` | 32-bit unsigned integer. |
| `bstring` | A UTF-8 encoded string of characters. Terminated with a single byte `0x00`. Length depends on content of string as UTF-8 is a variable length encoding. |
| `binary` | Stream of binary data. Length is dependent on use case. |
| `json` | A [JSON object](#JSON), stored in UTF-8. Length is dependent on use case. |
| `plain` | A plain text string, stored in UTF-8. Length is dependent on use case. |

## JSON

JSON types are named after their JSON counterparts. All data is stored as UTF-8. The following types are recognized:

| Type | Description |
| ---- | ----------- |
| `object` | A JSON object. |
| `array` | A JSON array. |
| `string` | A JSON string. |
| `number` | A JSON number. |
| `bool` | A JSON boolean. |
| `null` | A JSON `null`. |
