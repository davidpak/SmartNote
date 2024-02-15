# Summary Generation Options

Summaries are generated through the [`generate` interface](INTERFACES.md#generate). The `options` object passed to the interface describes how to generate the summaries. The `options` object is structured as follows:

| Field | Type | Description |
| ----- | ---- | ----------- |
| `general` | `object` | General options. |
| `llm` | `object` | LLM options. |

See [Types](TYPES.md) for a list of data types present in the responses.

## General Options

These options are used to control the general behavior of the summary generation process. They are stored in the `general` field of the `options` object. The following options are recognized:

| Field | Type | Description |
| ----- | ---- | ----------- |
| `files` | `array` | List of files to summarize. |

### `files`

The `files` field is a `string` array that specifies the files to summarize. Each element is the name of an uploaded file to give to the summarizer. The name must match the name given to the file when it was uploaded. The order is of the files in the array is the order in which they will be given to the summarizer, which may affect the output. See [`upload`](INTERFACES.md#upload) for more information on uploading files.

The `generate` interface will fail if the `files` field is empty or if any of the files specified do not exist. The error information will include the names of the files that do not exist.

## LLM Options

These options are used to control the behavior of the LLM summarizer. The following options are recognized:

| Field | Type | Description |
| ----- | ---- | ----------- |
| `velocity` | `number` | The velocity of the LLM summarizer. `[0.0, 1.0]` |
