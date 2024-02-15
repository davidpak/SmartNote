# `export`

Exports the generated notes to a file or remote location. Exported files are associated with the client's session and are stored on the server until the session expires. The files can be fetched using the [`fetch`](FETCH.md) RPC. Remote locations are specified in the request body. Use the [`generate`](GENERATE.md) RPC to generate notes to export before calling this RPC. The generated summary are identified by the name returned by the `generate` RPC.

## Request

`POST /api/v1/export`

### Query Parameters

No query parameters are expected and are ignored if present.

### Body

The body must be a JSON object with the following fields:

| Name | Type | Contents |
| ---- | ---- | ----------- |
| `name` | `string` | The name of the summary resource to export. |
| `type` | `string` | The type of export to perform. |
| `output` | `string` | A resource name to use when exporting to a local file. Optional. |
| `remote` | `object` | Remote export location information. |

`type` is a case-insensitive `string` describing the type of export to perform. The following types are recognized:

| Value | Description |
| ----- | ----------- |
| `notion` | Export the notes to a Notion database. |
| `rtf` | Export the notes to a Rich Text Format (RTF) file. |
| `json` | Export the notes to a JSON file. |
| `md` | Export the notes to a Markdown file. |

`output` is a `string` containing the name of the export resource to write to. The resource will be overwritten if it already exists. The resource will be associated with the client's session and can be fetched using the [`fetch`](FETCH.md) RPC. If `output` is not specified, the server will generate a unique name for the resource which will be returned in the response body. If a remote location is specified, this field will be ignored.

`remote` is an object containing information on how to export to a remote location. The fields of `remote` depend on the type of export to perform. This is only required if the export type is remote. If the export is local, this field will be ignored.

#### Notion
    
When exporting to Notion, the `remote` object may have the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `parent` | `string` | The ID of the parent page to export to. |
| `code` | `string` | Code returned by the Notion OAuth flow to get an access token. |
| `redirectUri` | `string` | The redirect URI used in the Notion OAuth flow. |
| `secret` | `string` | The Notion secret associated with the integration. **See below.** |
| `token` | `string` | The Notion access token to use. **See below.** |

`parent` is is a Notion page ID, obtained by looking at the 32-character sequence at the end of the URL of the page. For example, if the URL of the page is `https://www.notion.so/My-Page-1234567890abcdef`, the `parent` field should be `1234567890abcdef`. The integration must be given access to this page explicitly. Consult the [Notion API documentation](https://developers.notion.com/) for more information on how to do this.

`code` is a temporary code returned by the Notion OAuth flow. This is only required if the client has not yet obtained an access token for Notion. The server will use the access token associated with the client's session if it has already been obtained. See the how [Notion authorization](https://developers.notion.com/docs/authorization) works for more information.

`code` can be omitted if the client has already obtained an access token for Notion. The server will use the access token associated with the client's session. The first successful export to Notion will require the client to provide a temporary code so the integration can generate an access token. If the code is provided after a token has been generated, the server will ignore it. The same applies to the `redirectUri` field.

**Note**: `secret` is the Notion secret associated with a Notion integration. If specifying this in a request without `code`, the server will use this secret as the access token to allow interfacing with internal integrations. If `code` is specified, the server will create a new integration token with the given secret and use that to interface with Notion in the future. This field is only allowed if the server has been configured to allow it. See the [configuration](CONFIGURATION.md) documentation for more information.

**Note**: `token` is the Notion access token to use. If this is specified, the server will use this token to interface with Notion. This field is only allowed if the server has been configured to allow it. See the [configuration](CONFIGURATION.md) documentation for more information.

## Response

### Body

### Success

If the request was successful, the server will respond with `200 OK`. The response body depends on the type of export performed.

#### Local Export

| Name | Type | Contents |
| ---- | ---- | -------- |
| `name` | `string` | The name of the exported resource. |

`name` can be used in the [`fetch`](FETCH.md) RPC to fetch the exported resource.

#### Notion

| Name | Type | Contents |
| ---- | ---- | -------- |
| `url` | `string` | The URL of the Notion page containing the exported notes. |
| `name` | `string` | The name of the page containing the exported notes. |
| `id` | `string` | The ID of the page containing the exported notes. |

### Failure

The server will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `400 Bad Request` | The request was malformed. |
| `401 Unauthorized` | The client is not authorized to export summaries. |
| `403 Forbidden` | The client is not allowed to export summaries.  |
| `404 Not Found` | The resource to export was not found. |

The following may occur when exporting to a remote location, such as Notion:

| Status Code | Description |
| ----------- | ----------- |
| `502 Bad Gateway` | The server was unable to connect to the remote location. |
| `503 Service Unavailable` | The remote location is unavailable. |
| `504 Gateway Timeout` | The remote location timed out. |

**Note**: For Notion exporting, it is not enough for the user to only complete the OAuth flow. The user must also grant the integration access to their workspace. The server is unable to tell if the user has granted access, so it is up to the user to ensure the integration has access to their workspace. If the integration does not have access, the server will respond with a `502 Bad Gateway` status code. This code is not unique to this situation; check the response body for more information. If `secret` was specified in the request and the server was not configured to allow it, the server will respond with a `403 Forbidden` status code.
