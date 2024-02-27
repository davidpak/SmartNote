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
| `source` | `string` | The name of the summary resource to export. |
| `data` | `string` | Explicitly specify the data to export. |
| `exporter` | `string` | The exporter to use. |
| `output` | `string` | A resource name to use when exporting to a local file. Optional. |
| `remote` | `object` | Remote export location information. |

Either `source` or `data` must be specified, but not both. If `source` is specified, the server will use the data associated with the resource. If `data` is specified, the server will use the markdown data provided. If neither is specified, the request fails.

`exporter` is a case-insensitive `string` describing the type of export to perform. The following types are recognized:

| Value | Description |
| ----- | ----------- |
| `notion` | Exports  to a Notion database. |
| `rtf` | Exports to a Rich Text Format (RTF) file. |
| `json` | Exports to a JSON file. |
| `md` | Exports to a Markdown file. |

`output` is a `string` containing the name of the export resource to write to. The resource will be overwritten if it already exists. The resource will be associated with the client's session and can be fetched using the [`fetch`](FETCH.md) RPC. If `output` is not specified, the server will generate a unique name for the resource which will be returned in the response body. This has special behavior if the export type is remote.

`remote` is an object containing information on how to export to a remote location. The fields of `remote` depend on the type of export to perform. This is only required if the export is to a remote location. If the export is local, this field will be ignored.

#### Notion

When exporting to Notion, the `output` field specifies the title of the page to create. If it is not specified, the server will choose a title based on the content of the document. If it could not be determined, a default title will be used. The `remote` object may have the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `mode` | `string` | How the content should be outputted in Notion. |
| `page` | `string` | A Notion page ID. Semantics depend on the `mode` field. |
| `code` | `string` | Code returned by the Notion OAuth flow to get an access token. |
| `redirectUri` | `string` | The redirect URI used in the Notion OAuth flow. |
| `integration` | `string` | The Notion integration to use. **See below**. |

`mode` can be one of the following values:

| Value | Description |
| ----- | ----------- |
| `new` | Create a new page in Notion; `page` specifies the parent page. |
| `update` | Update an existing page in Notion; `page` specifies an existing page. |
| `append` | Append to an existing page in Notion; `page` specifies an existing page. |

`page` is is a Notion page ID, obtained by looking at the 32-character sequence at the end of the URL of the page. For example, if the URL of the page is `https://www.notion.so/My-Page-1234567890abcdef`, the `parent` field should be `1234567890abcdef`. The integration must be given access to this page explicitly or through the OAuth flow. Consult the [Notion API documentation](https://developers.notion.com/) for more information on how to do this. This is required if the `mode` is not `new`. If `mode` is `new` and `field` is not specified, the server will choose the most recently created page to be the parent. If no such page can be found, the request fails.

`code` is a temporary code returned by the Notion OAuth flow. This is only required if the client has not yet obtained an access token for Notion. The server will use the access token associated with the client's session if it has already been obtained. See the how [Notion authorization](https://developers.notion.com/docs/authorization) works for more information.

`code` can be omitted if the client has already obtained an access token for Notion. The server will use the access token associated with the client's session. The first successful export to Notion will require the client to provide a temporary code so the integration can generate an access token. If the code is provided after a token has been generated, the server will ignore it. The same applies to the `redirectUri` field.

**Note**: `integration` defines a custom Notion integration to use, called *remote integrations*. If specified, the server will use this object as the integration to use. If not, it will use its own integration. Note, however, that this is only allowed if the server is configured to allow it. See the [configuration](CONFIGURATION.md) documentation for more information. They layout of the `integration` object is as follows:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `secret` | `string` | The OAuth client secret or the internal integration secret. Optional. |
| `clientId` | `string` | The OAuth client ID of the integration. Optional. |
| `token` | `string` | The authorization token to use. Optional. |

**Restrictions**:

- If both `token` and `clientId` are not specified, the `secret` field is required.
- If `token` is specified, `secret` and `clientId` must not be specified.
- If `clientId` is specified, the `secret` field is required and the `token` field must not be specified.

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

If `mode` is `new`, the response will include the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `url` | `string` | The URL of the Notion page containing the exported notes. |
| `name` | `string` | The name of the page containing the exported notes. |
| `id` | `string` | The ID of the page containing the exported notes. |

Other modes do not include any additional fields.

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
