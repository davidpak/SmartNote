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

# Public Interfaces

Below are a list of public interfaces that the server exposes. Note that the name (in table) of body fields are not included in the body.

## `upload`

Uploads a single file to the server. Duplicate files are overwritten.

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

#### Success

If the file was successfully uploaded, the server will respond with `200 OK`.

#### Failure

The server fails in the following cases:

* The file is not an accepted file type.
* Uploading the file will exceed the client's storage quota.
* The file name was not specified.
* The file name is invalid.

If any of these cases occur, the server will respond with `400 Bad Request`.

**Body**

The body is always present in the response.

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `result` | `object` | A JSON object with a single field `message` describing the result of the request. |

## `generate`

Generates notes from the uploaded files.

### Request

**Method** `POST`

**Path** `/api/v1/generate`

**Query Parameters**

**Body**

### Response

**Body**

## `export`

Exports the generated notes to an external location.

### Request

**Method**

**Path** `/api/v1/export`

**Query Parameters**

**Body**

### Response

**Body**

# Authentication

The server uses JSON Web Tokens (JWTs) to authenticate users. They are sent through the `Authorization` header.

To generate a new JWT, the client must send a request to the server with no `Authorization` header. The server will then generate a new JWT and return it in the `Authorization` header of the response.

JWTs are valid for `Session.SESSION_LENGTH` seconds. After this time, session data is erased and the user must generate a new JWT. Sending a valid JWT to the server will automatically extend the session, given the request executes successfully, i.e. the server responds with `200 OK`.
