# `fetch`

Fetches a resource from the server. See [Server Resources](RESOURCES.md) for more information on resources.

## Request

`GET /api/v1/fetch`

### Query Parameters

| Key | Type | Description |
| --- | ---- | ----------- |
| `name` | `string` | The name of the resource to fetch. |

### Body

No body is expected in the request and will be ignored if present.

## Response

### Success

If the resource was successfully fetched, the server will respond with `200 OK` and the resource's data in the response body. The `Content-Type` header of the response will be set to the appropriate MIME type. The body will be a binary stream of the resource's data as follows:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `data` | `binary` | The resource's data. |

### Failure

Upon failure, the server will respond with one of the following status codes specified in [`Resources`](RESOURCES.md#Errors).
