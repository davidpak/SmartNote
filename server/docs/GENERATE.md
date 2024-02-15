# `generate`

Generates summaries from uploaded files. The summaries are associated with the client's session and are stored on the server until the session expires. The summaries can be exported to an external location using the [`export`](EXPORT.md) RPC. Use the [`upload`](UPLOAD.md) RPC to upload files to generate summaries for.

## Request

`POST /api/v1/generate`

### Query Parameters

No query parameters are expected and are ignored if present.

### Body

The body must be a JSON object describing how to generate the summaries. See [Generation Options](GENERATION.md) for a description of the object.

## Response

### Success

If the request was successful, the server will respond with `200 OK`. The response body will contain the additional fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `name` | `string` | The name of the generated resource. |
| `time` | `number` | The time taken to generate the summaries, in seconds. |
| `result` | `object` | A JSON object containing the result of the generation. |

When the server successfully generates the summaries, it will respond with `200 OK`. The `name` field will contain the name of the summary resource. The `time` field will contain the time taken to generate the summaries and is tracked across timeouts. `name` may be used in the [`export`](EXPORT.md) RPC to export the generated summary. The `result` object will contain the same content as the content of the generated resource referenced by `name`.

The `result` field is a JSON object containing the contents of the generated summaries. It's format is closely tied to markdown and its format is as follows:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `type` | `string` | The type of this node. |
| `children` | `array` | An array of child nodes. |

`type` is a `string` describing the type of the node. The following types are recognized:

#### Leaf Nodes

| Value | Description |
| ----- | ----------- |
| `fencedCodeBlock` | A fenced code block. |
| `indentedCodeBlock` | An indented code block. |
| `text` | A text node. |

#### Container Nodes

| Value | Description |
| ----- | ----------- |
| `blockQuote` | A block quote. |
| `bulletList` | A bullet list. |
| `document` | The root node. |
| `hardLineBreak` | A hard line break. |
| `heading` | A heading. |
| `thematicBreak` | A thematic break. |
| `listItem` | A list item. |
| `orderedList` | An ordered list. |
| `paragraph` | A paragraph. |
| `softLineBreak` | A soft line break. |

`text` nodes contain the additional fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `literal` | `string` | The literal text of the node. |
| `style` | `object` | A JSON object containing the style of the node. |

`style` is an optional JSON object containing the style of the node. It may contain the following fields:

| Name | Type | Contents |
| ---- | ---- | -------- |
| `bold` | `boolean` | Whether the text is bold. |
| `italic` | `boolean` | Whether the text is italic. |
| `code` | `boolean` | Whether the text is code. |
| `href` | `string` | The URL the text links to. |

The fields are only present if the style is applied to the text. The `literal` field is always present. If `code` is `true`, an additional `language` field may be present containing the language of the code.

If the node is a `heading`, an additional `level` field may be present containing the level of the heading.

### Failure

When the server fails to generate the summaries, it will respond with one of the following status codes:

| Status Code | Description |
| ----------- | ----------- |
| `400 Bad Request` | The request was malformed. |
| `401 Unauthorized` | The client is not authorized to generate summaries. |
| `403 Forbidden` | The client is not allowed to generate summaries. |
| `404 Not Found` | One or more of the files to generate summaries for does not exist. |
| `408 Request Timeout` | The summary could not generate in time. Retry the request. |
| `409 Conflict` | A request has not finished and `options` is not idempotent. |
| `429 Too Many Requests` | The client is trying to generate too many summaries. |

The following may also occur, but indicate an internal issue with the generator and does not indicate an issue with the request:

| Status Code | Description |
| ----------- | ----------- |
| `502 Bad Gateway` | The generator is unavailable. |
| `503 Service Unavailable` | The generator is unavailable. |
| `504 Gateway Timeout` | The generator timed out. |

The server defines a timeout for generating summaries. The timeout does not abort the generation process, but rather returns a `408 Request Timeout` response to the client. If the client receives a `408 Request Timeout` response, it should retry the request with the same options. The server only allows one generation request to be processed per session at a time. Consequently, if the same options are not used in the retry request, the server will respond with `409 Conflict`. If the underlying generator has timed out (which may be different than the server timeout), the server will respond with `504 Gateway Timeout` instead. When this occurs, the client should stop retrying the request temporarily and try again later.