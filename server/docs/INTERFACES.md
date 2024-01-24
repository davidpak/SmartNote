# Public Interfaces

The server exposes several public interfaces, called Remote Procedure Calls (RPCs). Note that the name (in table) of body fields are not included in the body.

Information passed to the RPCs is specified in tables, alongside its type. See [Types](TYPES.md) for more information.

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

**Method** `POST`

**Path** `/api/v1/export`

**Query Parameters**

**Body**

### Response

**Body**

## `fetch`

Fetches files from the server.

### Request

**Method** `GET`

**Path** `/api/v1/fetch`

**Query Parameters**

**Body**

### Response

**Body**
