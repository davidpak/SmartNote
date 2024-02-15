# `remove`

Remove a resource from the server. See [Server Resources](RESOURCES.md) for more information on resources.
 
## Request

`DELETE /api/v1/remove`

### Query Parameters

| Key | Type | Description |
| --- | ---- | ----------- |
| `name` | `string` | The name of the resource to remove. |

### Body

No body is expected in the request and will be ignored if present.

## Response

### Success

If the resource was successfully removed, the server will respond with `200 OK`. If the resource cannot be deleted immediately but can be deleted in the future, the server will respond with `202 Accepted`.

### Failure

Upon failure, the server will respond with one of the following status codes specified in [`Resources`](RESOURCES.md#Errors).