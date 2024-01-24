# Public Resource Access

The server has resources that can be accessed without authentication. Within the server, they are located in the `server/public/` directory. They may be accessed through [the `fetch` interface](./INTERFACES.md#fetch) or through the `public` endpoint.

See [Types](TYPES.md) for a list of data types present in the responses.

## `public`

The `public` endpoint is a special endpoint that allows access to public resources. It is located at `/public/` and is accessible through the `GET` method. It takes a single query parameter, `name`, which is the name of the resource to fetch.

### Request

**Method** `GET`

**Path** `/public/<name>`

**Query Parameters**

No query parameters are expected and are ignored if present.

### Response

#### Success

If the resource was successfully fetched, the server will respond with `200 OK` and the resource's data in the response body.

**Body**

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `data` | `binary` | The resource's data. |

#### Failure

No body will be present in the response and the server will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `400 Bad Request` | The request was malformed. |
| `404 Not Found` | The resource was not found. |
