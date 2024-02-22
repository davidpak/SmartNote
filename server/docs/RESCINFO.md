# `rescinfo`

Query capabilities and settings of the resource system. See [Server Resources](RESOURCES.md) for more information on resources.

## Request

`GET /api/v1/rescinfo`

### Query Parameters

No query parameters are expected and are ignored if present.

### Body

No body is expected in the request and will be ignored if present.

## Response

### Success

If the request was successful, the server will respond with `200 OK`. The `result` field of the response body will be a JSON object with the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `sessionQuota` | `number` | The maximum amount of data a single session can store, in bytes. |
| `maxUploadSize` | `number` | The maximum size of a single file upload, in bytes. |
| `supportedTypes` | `array` | An array of strings containing supported MIME types. |
| `authorities` | `array` | An array of strings containing supported authorities. |

### Failure

This request never fails.