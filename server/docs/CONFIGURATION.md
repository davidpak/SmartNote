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

## `generator`

The `generator` object contains the configuration for the summary generator. It has the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `summarizer` | `string` | Path to the summarizer script. |
| `env` | `string` | Path to the `.env` file used to configure the summarizer. |
| `python` | `string` | Path to the Python interpreter to use. |
| `debug` | `boolean` | Whether to enable debug mode. |
| `debugResource` | `string` | The resource to use for debug mode. |

Note that the Pyhon interpreter must be able to run the summarizer script. That is, it must have the required dependencies installed. This will likely be the location of an Anaconda environment or a virtual environment.

**Note**: If `debug` is `true`, the generator script will not be ran, and instead the server will treat the contents of `debugResource` as the result of the generation. This is useful for development and testing and to avoid making requests to the LLM, which may cost money.
