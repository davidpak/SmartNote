# `upload`

Uploads a single file to the per-session storage. This request requires authentication. See [Server Resources](RESOURCES.md) for more information on resources.

**Note**: This RPC cannot upload multiple files at once. To upload multiple files, call this RPC multiple times.

## Request

`POST /api/v1/upload`

### Query Parameters

| Key | Type | Description |
| --- | ---- | ----------- |
| `name` | `string` | The name of the file to upload. Not a resource name. |
| `disposition` | `string` | The creation disposition of the file. Optional. |

The `disposition` query parameter is optional and is used to specify the creation disposition of the file. The following values are recognized:

| Value | Description |
| ----- | ----------- |
| `create_always` | Always create a new file. If the file already exists, it is truncated to zero length. This is the default value. |
| `create_new` | Create a new file. If the file already exists, the request fails. |

### Body

The body must be present in the request.

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `data` | `binary` | The file's data. |

## Response

### Success

If the file was successfully uploaded, the server will respond with `200 OK`. The JSON object in the response will contain the additional fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `name` | `string` | The name of the uploaded resource. |

The `name` field can be used to identify the resource in future requests.

### Failure

When the server fails to upload the file, it responds with one of the status codes specified in [`Resources`](RESOURCES.md#Errors).