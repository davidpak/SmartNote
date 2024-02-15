# `login`

Starts a new session by generating a new JWT. The JWT is valid for a finite amount of time and may be renewed by calling any RPC that returns a successful response. Upon successful login, the client will recieve a new JWT in the `session` cookie of the response which can be used to authenticate future requests.

## Request

`POST /api/v1/login`

### Query Parameters

No query parameters are expected and are ignored if present.

### Body

No body is expected in the request and will be ignored if present.

## Response

### Success

If the request was successful, the server will respond with `200 OK` and the `session` cookie will contain the new JWT. If the `session` cookie already contained a valid JWT, the server will renew the session and the `session` cookie will contain the renewed JWT. If the `session` cookie contained an expired or invalid JWT, the server will respond with a new JWT in the `session` cookie.

### Failure

The server will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `401 Unauthorized` | The session could not be created. |
| `403 Forbidden` | The client is not allowed to create a session. |
| `429 Too Many Requests` | The client is trying to create too many sessions. |