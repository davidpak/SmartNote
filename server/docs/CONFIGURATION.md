# Configuration

This describes the configuration file for the server, `config.json`. It searches for the file on the current working directory of the server. If it is not found, the server will create a new one with default values.

The configuration file is a JSON object with the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `server` | `object` | The server configuration. |
| `resource` | `object` | The resource configuration. |
| `notion` | `object` | The Notion configuration. |

## `server`

The `server` object contains the configuration for the server itself. It has the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `port` | `number` | The port to listen on. |
| `host` | `string` | The host to listen on. |
| `usessl` | `boolean` | Whether to use SSL. |
| `certFile` | `string` | The path to the SSL certificate file. |
| `origin` | `string` | The origin to use for CORS. |

## `resource`

The `resource` object contains the configuration for the resource system. See the [resource documentation](RESOURCES.md) for information on the resource system. It has the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `privateDir` | `string` | The directory to store private resources. |
| `publicDir` | `string` | The directory to store public resources. |
| `sessionDir` | `string` | The directory to store per-session resources. |
| `maxUploadSize` | `number` | The maximum size a client can upload, in bytes. |
| `sessionQuota` | `number` | The maximum number of bytes a session can store. |
| `uploadDir` | `string` | The directory within a session to store uploaded files. |

## `notion`

The `notion` object contains the configuration for the Notion integration. It has the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `allowRemoteIntegrations` | `boolean` | Whether to allow remote integrations. Only use for development. |
| `clientId` | `string` | The OAuth client ID. |
| `secret` | `string` | The OAuth client secret or internal integration secret. |
