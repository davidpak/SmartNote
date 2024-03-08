import { createContext, useContext } from 'react';
import { JsonType } from '../pages/TopicSelection';

interface OutputContextType {
  markdown: string;
  json: JsonType | undefined;
  setMarkdown: (markdown: string) => void;
  setJson: (json: JsonType) => void;
}

const OutputContext = createContext<OutputContextType>({
  markdown: '',
  json: undefined,
  setMarkdown: () => {},
  setJson: () => {},
});

export const useOutputContext = () => useContext(OutputContext);
export const OutputContextProvider = OutputContext.Provider;
