# SmartNote Server

## Configuration

The server is controlled using the configuration file `config.json`. This file is not included in the repository, as it contains sensitive information. One will be generated when the server is first run, and it will need to be filled out with the appropriate information. You may also create one yourself. The layout of the file is as follows:


| Key | Type | Description |
| --- | --- | --- |
| `server` | `object` | Contains information about the server. |
| `resource` | `object` | Contains information about the resource system. |
| `notion` | `object` | Contains information about the Notion API. |
| `generator` | `object` | Contains information for the LLM summarizer. |

### Server Configuration

`server`

| Key | Type | Description |
| --- | --- | --- |
| `port` | `number` | The port to listen on. |
| `host` | `string` | The host to listen on. |
| `usessl` | `boolean` | Whether or not to use SSL. |
| `certFile` | `string` | The path to the SSL certificate file. |
| `origin` | `string` | The origin to use for CORS. |

**Note**: Set `origin` to the address on which the client is hosted. If running locally, this will generally be `http://localhost:5173/`.

### Resource Configuration

`resource`

| Key | Type | Description |
| --- | --- | --- |
| `privateDir` | `string` | The path to the private directory. |
| `publicDir` | `string` | The path to the public directory. |
| `sessionDir` | `string` | The path to the session directory. |
| `maxUploadSize` | `number` | The maximum size of a file upload in bytes. |
| `sessionQuota` | `number` | The maximum number of bytes a user can store in their session. |
| `uploadDir` | `string` | The path to the upload directory, within the session directory. |

### Notion Configuration

`notion`

| Key | Type | Description |
| --- | --- | --- |
| `allowRemoteIntegrations` | `boolean` | Whether or not to allow the client to provide their own integration keys. Set to `false` in production. |
| `clientId` | `string` | The OAuth Client ID for the integration. |
| `secret` | `string` | The OAuth Client Secret for the integration. |

### Generator Configuration

`generator`

| Key | Type | Description |
| --- | --- | --- |
| `summarizer` | `string` | The path to the Python script to use for summarization. |
| `env` | `string` | Path to the `.env` file for the summarizer. |
| `python` | `string` | The path to the Python executable. |
| `debug` | `boolean` | Whether or not to enable debug mode. Set to `false` in production. |
| `debugResource` | `string` | The resource to use for debug mode. |


Set `python` to the path of the Python executable for the environment in which the summarizer will run. Assuming you have set up the Anaconda environment as described in the [root README](../README.md), this will be the path to the Python executable in the Anaconda environment. Use the following command to find the path:

```sh
conda env list
```

Find the environment you created and copy the path to the `python` executable. This must include the file name (not just the directory).

**Note**: The `.env` file should contain the following:

```env
OPENAI_API_KEY=<your-api-key>
```

`<your-api-key>` must be a valid OpenAI secret.

**Note**: Debug mode is used for testing the generator. It will not generate summaries, but will instead always return the data pointed to by `debugResource`.
