# Public Resource Access

The server has resources that can be accessed without authentication. [The `fetch` interface](./INTERFACES.md#fetch) can also be used to access resources, but the accessors have slight differences.

See [Types](TYPES.md) for a list of data types present in the responses.

## `public`

The `public` endpoint is a special endpoint that allows access to public resources. It is located at `/public/` and is accessible through the `GET` method. It takes a path as a parameter, following the `public` endpoint.

### Request

`GET /public/[path...]`

**Query Parameters**

No query parameters are expected and are ignored if present.

**Body**

No body is expected and is ignored if present.

### Response

#### Success

If the resource was successfully fetched, the server will respond with `200 OK` with a body containing the resource's data. The `Content-Type` header will be set to to the appropriate MIME type for the resource. The body is defined as follows:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `data` | `binary` | The resource's data. |

#### Failure

No body will be present in the response and the server will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `400 Bad Request` | The request was malformed. |
| `404 Not Found` | The resource was not found. |
| `406 Not Acceptable` | The resource is not available in the requested format. |

The body will contain a document describing the error. Its type is given by the `Content-Type` header, but is usually `text/html`.
