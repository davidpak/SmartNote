# Public Interfaces

The server exposes several public interfaces, known as Remote Procedure Calls (RPCs). RPCs are dispatched using HTTP requests to the server. Each RPC has an expected request format which is paired with a guaranteed response format, regardless of the success of the request. Some RPCs require a user session to be present in the request which is used to authenticate the user, give access to protected resources, and associate a client with its resources so they can be used in future requests (such as using uploaded files for generation). See [Authentication](AUTH.md) for more information on authentication and sessions.

Below is a list of all RPCs exposed by the server. Each RPC is described by its path, request format, and response format. The request and response formats are described using [data types](TYPES.md).

The response of `POST` requests will always contain a response body with a JSON object:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `success` | `bool` | Whether the request was successful. |
| `message` | `string` | A message describing the result of the request. |

It is guaranteed that all `POST` requests will have these fields in the response body, regardless of the success of the request. The `result` object may contain additional fields depending on the RPC. See the RPC's description for more information.

**Note**: The name of body fields (in tables) are not included in the body. For example, even if a body field is named `result` and is of type `json`, the underlying JSON object will not necessarily contain the field `result`. The name is only used to describe the contents of the field.

## `login`







## `export`

Exports the generated notes to a file or remote location. Exported files are associated with the client's session and are stored on the server until the session expires. The files can be fetched using the [`fetch`](#fetch) RPC. Remote locations are specified in the request body. Use the [`generate`](#generate) RPC to generate notes to export before calling this RPC. The generated summary are identified by the name returned by the `generate` RPC.

### Request

`POST /api/v1/export`

#### Query Parameters

No query parameters are expected and are ignored if present.

#### Body

The body must be present in the request.

| Name | Type | Contents |
| ---- | ---- | -------- |
| `options` | `json` | A JSON object describing how to export the file. |

`options` recognizes the following fields:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `name` | `string` | The name of the summary resource to export. |
| `type` | `string` | The type of export to perform. |
| `token` | `string` | A token to use when exporting to a remote location. |
| `output` | `string` | A resource name to use when exporting to a local file. |

`type` is a case-insensitive `string` describing the type of export to perform. The following types are recognized:

| Value | Description |
| ----- | ----------- |
| `notion` | Export the notes to a Notion database. |
| `rtf` | Export the notes to a Rich Text Format (RTF) file. |
| `json` | Export the notes to a JSON file. |

`token` is a `string` containing a token to use when exporting to a remote location. The token is dependent on the export type.

`output` is a `string` containing the name of the export resource to write to. The resource will be overwritten if it already exists. The resource will be associated with the client's session and can be fetched using the [`fetch`](#fetch) RPC.

### Response

#### Body

#### Success

If the request was successful, the server will respond with `200 OK`.

If the export was to a local resource, the response body will contain the additional fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `name` | `string` | The name of the exported resource. |

`name` can be used in the [`fetch`](#fetch) RPC to fetch the exported resource.

If the export was to a remote location, the `name` field will not be present in the response body.

#### Failure

The server will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `400 Bad Request` | The request was malformed. |
| `401 Unauthorized` | The client is not authorized to export summaries. |
| `403 Forbidden` | The client is not allowed to export summaries. |
| `404 Not Found` | The resource to export was not found. |

The following may occur when exporting to a remote location, such as Notion:

| Status Code | Description |
| ----------- | ----------- |
| `502 Bad Gateway` | The server was unable to connect to the remote location. |
| `503 Service Unavailable` | The remote location is unavailable. |
| `504 Gateway Timeout` | The remote location timed out. |

## `fetch`

Fetches a resource from the server. See [Server Resources](RESOURCES.md) for more information on resources.

### Request

`GET /api/v1/fetch`

#### Query Parameters

| Key | Type | Description |
| --- | ---- | ----------- |
| `name` | `string` | The name of the resource to fetch. |

#### Body

No body is expected in the request and will be ignored if present.

### Response

#### Success

If the resource was successfully fetched, the server will respond with `200 OK` and the resource's data in the response body. The `Content-Type` header of the response will be set to the appropriate MIME type. The body will be a binary stream of the resource's data as follows:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `data` | `binary` | The resource's data. |

#### Failure

Upon failure, the server will respond with one of the following status codes specified in [`Resources`](RESOURCES.md#Errors).

## `remove`

Remove a resource from the server. See [Server Resources](RESOURCES.md) for more information on resources.
 
### Request

`POST /api/v1/remove`

#### Query Parameters

| Key | Type | Description |
| --- | ---- | ----------- |
| `name` | `string` | The name of the resource to remove. |

#### Body

No body is expected in the request and will be ignored if present.

### Response

#### Success

If the resource was successfully removed, the server will respond with `200 OK`.

#### Failure

Upon failure, the server will respond with one of the following status codes specified in [`Resources`](RESOURCES.md#Errors).

## `rescinfo`

Query capabilities and settings of the resource system. See [Server Resources](RESOURCES.md) for more information on resources.

### Request

`GET /api/v1/rescinfo`

#### Query Parameters

No query parameters are expected and are ignored if present.

#### Body

No body is expected in the request and will be ignored if present.

### Response

#### Success

If the request was successful, the server will respond with `200 OK`. The `result` field of the response body will contain the additional fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `capabilities` | `json` | A JSON object describing the capabilities of the resource system. |

`capabilities` is a JSON object with the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `sessionQuota` | `number` | The maximum amount of data a single session can store, in bytes. |
| `maxUploadSize` | `number` | The maximum size of a single file upload, in bytes. |
| `supportedTypes` | `array` | An array of strings containing supported MIME types. |
| `authorities` | `array` | An array of strings containing supported authorities. |

#### Failure

This request never fails.
