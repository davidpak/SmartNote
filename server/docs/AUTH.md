# Authentication

The [server](../README.md) uses [JSON Web Tokens](https://en.wikipedia.org/wiki/JSON_Web_Token) (JWTs) to authenticate users and manage sessions.

## Sessions

Sessions are finite periods of time in which a user is authenticated. They allow a client access to certain protected resources. Sessions are implemented using JWTs and are passed using the `session` cookie. The content of the token is opaque to the client and is managed by the server.

Tokens expire after a certain amount of time, after which they are not accepted. Resources associated with an invalid session are deleted. Sessions can be [renewed](#renewal).

### Creation

A session may be created through the [login interface](INTERFACES.md#login). If the request is successful, the server will generate a new session for the client and return a JWT in the session cookie of the response. This cookie can then be used in subsequent requests to access session-protected resources.

### Renewal

Sessions may be renewed by making a request to the server with a valid JWT session cookie. If the request is successful, the server will return a renewed JWT in the `session` cookie. The validity of the old JWT is not affected and will persist until it expires.
