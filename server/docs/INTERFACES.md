# Public Interfaces

The server exposes several public interfaces, known as Remote Procedure Calls (RPCs). RPCs are dispatched using HTTP requests to the server. Each RPC has an expected request format which is paired with a guaranteed response format, regardless of the success of the request. Some RPCs require a user session to be present in the request which is used to authenticate the user, give access to protected resources, and associate a client with its resources so they can be used in future requests (such as using uploaded files for generation). See [Authentication](AUTH.md) for more information on authentication and sessions.

Below is a list of all RPCs exposed by the server. Each RPC is described by its path, request format, and response format. The request and response formats are described using [data types](TYPES.md).

The response of `POST` requests will always contain a body. The body is defined as follows:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `result` | `json` | A JSON object containing information about the request. |

The `result` object always contains the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `success` | `bool` | Whether the request was successful. |
| `message` | `string` | A message describing the result of the request. |

It is guaranteed that all `POST` requests will have these fields in the response body, regardless of the success of the request. The `result` object may contain additional fields depending on the RPC. See the RPC's description for more information.

**Note**: The name of body fields (in tables) are not included in the body. For example, even if a body field is named `result` and is of type `json`, the underlying JSON object will not necessarily contain the field `result`. The name is only used to describe the contents of the field.

## `login`

Starts a new session by generating a new JWT. The JWT is valid for a finite amount of time and may be renewed by calling any RPC that returns a successful response. Upon successful login, the client will recieve a new JWT in the `session` cookie of the response which can be used to authenticate future requests.

### Request

`POST /api/v1/login`

#### Query Parameters

No query parameters are expected and are ignored if present.

#### Body

No body is expected in the request and will be ignored if present.

### Response

#### Success

If the request was successful, the server will respond with `200 OK` and the `session` cookie will contain the new JWT. If the `session` cookie already contained a valid JWT, the server will renew the session and the `session` cookie will contain the renewed JWT. If the `session` cookie contained an expired or invalid JWT, the server will respond with a new JWT in the `session` cookie.

#### Failure

The server will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `401 Unauthorized` | The session could not be created. |
| `403 Forbidden` | The client is not allowed to create a session. |
| `429 Too Many Requests` | The client is trying to create too many sessions. |

## `upload`

Uploads a single file to the per-session storage. This request requires authentication. See [Server Resources](RESOURCES.md) for more information on resources.

**Note**: This RPC cannot upload multiple files at once. To upload multiple files, call this RPC multiple times.

### Request

`POST /api/v1/upload`

#### Query Parameters

| Key | Type | Description |
| --- | ---- | ----------- |
| `name` | `string` | The name of the file to upload. Not a resource name. |
| `disposition` | `string` | The creation disposition of the file. Optional. |

The `disposition` query parameter is optional and is used to specify the creation disposition of the file. The following values are recognized:

| Value | Description |
| ----- | ----------- |
| `create_always` | Always create a new file. If the file already exists, it is truncated to zero length. This is the default value. |
| `create_new` | Create a new file. If the file already exists, the request fails. |

#### Body

The body must be present in the request.

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `data` | `binary` | The file's data. |

### Response

#### Success

If the file was successfully uploaded, the server will respond with `200 OK`. The `result` field of the response body will contain the additional fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `name` | `string` | The name of the uploaded resource. |

The `name` field can be used to identify the resource in future requests.

#### Failure

When the server fails to upload the file, it responds with one of the status codes specified in [`Resources`](RESOURCES.md#Errors).

## `generate`

Generates summaries from uploaded files. The summaries are associated with the client's session and are stored on the server until the session expires. The summaries can be exported to an external location using the [`export`](#export) RPC. Use the [`upload`](#upload) RPC to upload files to generate summaries for.

### Request

`POST /api/v1/generate`

#### Query Parameters

No query parameters are expected and are ignored if present.

#### Body

The body must be present in the request.

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `options` | `json` | A JSON object describing how to generate the summaries. |

See [Generation Options](GENERATION.md) for a description of the `options` object.

### Response

#### Success

If the request was successful, the server will respond with `200 OK`. The `result` field of the response body will contain the additional fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `name` | `string` | The name of the generated resource. |
| `time` | `number` | The time taken to generate the summaries, in seconds. |

When the server successfully generates the summaries, it will respond with `200 OK`. The `name` field of the `result` object will contain the name of the summary resource. The `time` field will contain the time taken to generate the summaries and is tracked across timeouts. `name` may be used in the 
[`export`](#export) RPC to export the generated summary.

#### Failure

When the server fails to generate the summaries, it will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `400 Bad Request` | The request was malformed. |
| `401 Unauthorized` | The client is not authorized to generate summaries. |
| `403 Forbidden` | The client is not allowed to generate summaries. |
| `404 Not Found` | One or more of the files to generate summaries for does not exist. |
| `408 Request Timeout` | The summary could not generate in time. Retry the request. |
| `409 Conflict` | A request has not finished and `options` is not idempotent. |
| `429 Too Many Requests` | The client is trying to generate too many summaries. |

The following may also occur, but indicate an internal issue with the generator and does not indicate an issue with the request:

| Status Code | Description |
| ----------- | ----------- |
| `502 Bad Gateway` | The generator is unavailable. |
| `503 Service Unavailable` | The generator is unavailable. |
| `504 Gateway Timeout` | The generator timed out. |

The server defines a timeout for generating summaries. The timeout does not abort the generation process, but rather returns a `408 Request Timeout` response to the client. If the client receives a `408 Request Timeout` response, it should retry the request with the same options. The server only allows one generation request to be processed per session at a time. Consequently, if the same options are not used in the retry request, the server will respond with `409 Conflict`. If the underlying generator has timed out (which may be different than the server timeout), the server will respond with `504 Gateway Timeout` instead. When this occurs, the client should stop retrying the request temporarily and try again later.

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
