# Server Resources

Data can be accessed on the server through resources. Resources are accessed by name and represent arbitrary data. Resources can be uploaded, removed, and downloaded. They are managed through the various [interfaces](#INTERFACES.md).

## Authorities

Resources are grouped into different authorities, each having different access privileges. The following authorities are available:

| Authority | Description |
| --------- | ----------- |
| `public`  | Publicly accessible resources. |
| `private` | Resources accessible only to the server. |
| `session` | Resources associated with the current session. |

### Public Resources

Public resources are accessible to anyone and do not require authentication. Public resources are read-only.

### Private Resources

Private resources are innaccessible to anyone except the server. Any attempt by a client to access a private resource is not allowed.

### Session Resources

Session resources are accessible only to the current session. Session resources have read, write, and delete access. Session resources are automatically deleted when the session ends. Accessing these resources requires authentication. A session can be created with the [`login`](INTERFACES.md#login) interface.

## Accessing Resources

### Identifying Resources

Resources are identified through the following form:

```
<authority>:<name>
```

Where `<authority>` is the authority name and `<name>` is the resource name. For example, the resource `public:foo` is a public resource named `foo`.

Resource names cannot contain a `.` that precedes an entry in an abstract path. For example, the resource `session:.foo.txt` is invalid. The resource `session:foo/.bar/baz.txt` is also invalid.

### Interfacing with Resources

The following interfaces are available for accessing resources:

- [`upload`](INTERFACES.md#upload) - Upload a resource.
- [`fetch`](INTERFACES.md#fetch) - Download a resource.
- [`delete`](INTERFACES.md#delete) - Delete a resource.

### Errors

Below is a description of some of the errors that can occur when accessing resources. An interface may have additional errors not listed here.

| Code | Description |
| ---- | ----------- |
| `400 Bad Request` | The resource name is invalid or the request is malformed. |
| `401 Unauthorized` | The client is attempting to access a session resource and is not authenticated. |
| `403 Forbidden` | The client is attempting to access a private resource or does not have permission to perform the requested action. |
| `404 Not Found` | The resource does not exist and the client is attempting to read or delete it. Also occurs if an unknown authority is specified. |
| `406 Not Acceptable` | The client specified the `Accept` header and the requested resource exists, is accessible, but is not of the requested type. Also occurs if client specified the `Content-Type` header and the server does not support the specified type. The file extension is used to determine the type in the absence of the `Content-Type` header. |
| `413 Payload Too Large` | The client is attempting to upload a resource that is too large or uploading the resource would cause the session to exceed its quota. |
| `429 Too Many Requests` | The client is attempting to upload a resource too quickly. |
