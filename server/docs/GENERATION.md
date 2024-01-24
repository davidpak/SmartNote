# Summary Generation Options

Summaries are generated through the [`generate` interface](INTERFACES.md#generate). The `options` object passed to the interface describes how to generate the summaries. The `options` object is structured as follows:

| Field | Type | Description |
| ----- | ---- | ----------- |
| `general` | `object` | General options. |
| `llm` | `object` | LLM options. |

See [Types](TYPES.md) for a list of data types present in the responses.

## General Options

These options are used to control the general behavior of the summary generation process. They are stored in the `general` field of the `options` object.

## LLM Options

These options are used to control the behavior of the LLM summarizer.
