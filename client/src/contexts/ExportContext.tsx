import { createContext, useContext } from 'react';

interface ExportContextType {
  notesUrl: string;
  setNotesUrl: (value: string) => void;
}

const ExportContext = createContext<ExportContextType>({
  notesUrl: '',
  setNotesUrl: () => {},
});

export const useExportContext = () => useContext(ExportContext);
export const ExportContextProvider = ExportContext.Provider;
