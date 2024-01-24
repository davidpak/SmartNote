# Public Interfaces

The server exposes several public interfaces, called Remote Procedure Calls (RPCs). RPCs are accessed via HTTP requests to the server and are selected by the path of the request. Each RPC has an expected request format which is paired with a guaranteed response format, regardless of the success of the request.

Before continuing, see [Types](TYPES.md) for a description of the types used in the RPCs, which depend on both the RPC and the location in the request or response. Furthermore, most RPCs require a valid session token to be present in the `Authorization` header of the request. See [Authentication](AUTH.md) for more information.

**Note**: The name of body fields (in tables) are not included in the body. For example, even if a body field is named `result` and is of type `object`, the object will not contain the field `result`, unless otherwise specified. The name is only used to describe the contents of the field.


## Quick Links

* [`login`](#authentication)
* [`upload`](#session)
* [`generate`](#generate)
* [`export`](#export)
* [`fetch`](#fetch)

## `login`

Starts a new session.

### Request

**Method** `POST`

**Path** `/api/v1/login`

**Query Parameters**

No query parameters are expected and are ignored if present.

**Body**

No body is expected in the request.

### Response

#### Success

If the request was successful, the server will respond with `200 OK` and a new JWT in the `Authorization` header of the response. A body will be present in the response with the following format:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `success` | `plain` | The plain text string `"Success"`. |

#### Failure

On failure, no body will be present. The server will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `401 Unauthorized` | The session could not be created. |
| `403 Forbidden` | The client is not allowed to create a session. |
| `429 Too Many Requests` | The client is trying to create too many sessions. |

## `upload`

Uploads a single file to the server. If the file already exists, it will be overwritten.

### Request

**Method** `POST`

**Path** `/api/v1/upload`

**Query Parameters**

| Key | Type | Description |
| -- | ---- | ----------- |
| `name` | `string` | The name of the file to upload. |

**Body**

The body must be present in the request.

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `data` | `binary` | The file's data. |

### Response

**Body**

The body is always present in the response. It is defined as follows:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `result` | `object` | A JSON object with a single field `message` describing the result of the request.

#### Success

If the file was successfully uploaded, the server will respond with `200 OK`.

#### Failure

When the server fails to upload the file, it will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `400 Bad Request` | The request was malformed. |
| `401 Unauthorized` | The client is not authorized to upload files. |
| `403 Forbidden` | The client is not allowed to upload files. |
| `406 Not Acceptable` | The type of file being uploaded is not allowed. |
| `413 Payload Too Large` | The file is too large or will cause the session quota to be exceeded. |

## `generate`

Generates summaries from uploaded files.

### Request

**Method** `POST`

**Path** `/api/v1/generate`

**Query Parameters**

No query parameters are expected and are ignored if present.

**Body**

The body must be present in the request.

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `options` | `object` | A JSON object describing how to generate the summaries. |

See [Generation Options](GENERATION.md) for a description of the `options` object.

### Response

The body is always present in the response. It is defined as follows:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `result` | `object` | A JSON object with contains information about the result of the request. |

The `result` object always contains the following fields:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `message` | `string` | A message describing the result of the request. |

#### Success

If the request was successful, the server will respond with `200 OK`. The `result` field of the response body will contain the additional fields:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `time` | `float` | The time taken to generate the summaries, in seconds. |
| `files` | `object` | A JSON object containing information about the files that were summarized. |

The `files` object contains a field for each file that was summarized. The field name is the name of the file. Each object contains the following fields:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `success` | `bool` | Whether the file was successfully summarized. |

#### Failure

When the server fails to generate the summaries, the `message` field of the `result` object will contain a description of the error. The status code will be one of the following:

| Status Code | Description |
| ----------- | ----------- |
| `400 Bad Request` | The request was malformed. |
| `401 Unauthorized` | The client is not authorized to generate summaries. |
| `403 Forbidden` | The client is not allowed to generate summaries. |
| `404 Not Found` | One or more of the files to generate summaries for does not exist. |

## `export`

Exports the generated notes to an external location.

### Request

**Method** `POST`

**Path** `/api/v1/export`

**Query Parameters**

No query parameters are expected and are ignored if present.

**Body**

The body must be present in the request.

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `options` | `object` | A JSON object describing how to export the file. |

The `options` object must contain the case-sensitive field `type`, a `string` describing the type of export to perform. `options` contains additional fields depending on `type`. The following types are supported:

| Value | Description |
| ----- | ----------- |
| `Notion` | Export the notes to a Notion database. |
| `rtf` | Export the notes to a Rich Text Format (RTF) file. |

If `type` is `Notion`, the following fields are expected:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `token` | `string` | The Notion API token to use. |

If `type` is `rtf`, the following fields are expected:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `name` | `string` | The name of the file to export to. |

### Response

The response will always contain a body. Its contents depend on the success of the request and the `type` field in `options`.

**Body**

The body may be overridden by the server on some occasions, see below.

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `success` | `bool` | Whether the export was successful. |
| `message` | `string` | A message describing the result of the export. |

#### Success

If the request was successful, the server will respond with `200 OK`. The body depends on the `type` field in `options`.

If `type` was not `Notion`, the `data` field will contain the exported data, in the format specified by `type`. The `Content-Type` header of the response will be set to the appropriate MIME type. The body will be a binary stream of the exported data as follows:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `data` | `binary` | The content of the exported data. |

#### Failure

The `success` field of the response body will be set to `false` and the `message` field will contain a description of the error. The server will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `400 Bad Request` | The request was malformed. |
| `401 Unauthorized` | The client is not authorized to export summaries. |
| `403 Forbidden` | The client is not allowed to export summaries. |
| `404 Not Found` | The summary was not generated. |
| `406 Not Acceptable` | The `Accept` header does not match the resource's content type. |

The following may occur when exporting to a remote location, such as Notion:

| Status Code | Description |
| ----------- | ----------- |
| `502 Bad Gateway` | The server was unable to connect to the remote location. |
| `503 Service Unavailable` | The remote location is unavailable. |
| `504 Gateway Timeout` | The remote location timed out. |

## `fetch`

Fetches a resource from the server.

### Request

**Method** `GET`

**Path** `/api/v1/fetch`

**Query Parameters**

| Key | Type | Description |
| -- | ---- | ----------- |
| `name` | `string` | The name of the resource to fetch. |
| `authority` | `string` | The authority of the resource to fetch. Optional. |

`authority` is case-sensitive and must take one of the following values, if present:

| Value | Description |
| ----- | ----------- |
| `public` | Fetch a public resource. |
| `session` | Fetch a session-specific resource. |

If `authority` is not specified, it will default to `public`. If `authority` is `session`, the request must contain a valid session token. Public resources may also be accessed through the [`public` endpoint](PUBLIC.md).

**Body**

No body is expected in the request and will be ignored if present.

**Note**: If the `Accept` header is set in the HTTP request, the server will only return the content of the resource if the `Accept` header matches the resource's content type. See the failure response for more information.

### Response

A body will always be present in the response.

#### Success

If the resource was successfully fetched, the server will respond with `200 OK` and the resource's data in the response body. The `Content-Type` header of the response will be set to the appropriate MIME type. The body will be a binary stream of the resource's data as follows:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `data` | `binary` | The resource's data. |

#### Failure

The body of the response will contain a JSON object describing the error. It is defined as follows:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `result` | `object` | A JSON object with a single field `message` describing the result of the request. |

The server will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `400 Bad Request` | The request was malformed. |
| `401 Unauthorized` | The client is not authorized to access the resource. |
| `403 Forbidden` | The client is not allowed to access the resource. |
| `404 Not Found` | The resource does not exist. |
| `406 Not Acceptable` | The `Accept` header does not match the resource's content type. |
