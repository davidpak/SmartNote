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

## Available RPCs

- [`login`](LOGIN.md)
- [`upload`](UPLOAD.md)
- [`generate`](GENERATE.md)
- [`export`](EXPORT.md)
- [`fetch`](FETCH.md)
- [`remove`](REMOVE.md)
- [`rescinfo`](RESCINFO.md)
