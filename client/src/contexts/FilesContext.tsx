import { createContext, useContext } from 'react';

interface FilesContextType {
  files: string[];
  setFiles: (files: string[]) => void;
}

const FilesContext = createContext<FilesContextType>({
  files: [],
  setFiles: () => {},
});

export const useFilesContext = () => useContext(FilesContext);
export const FilesContextProvider = FilesContext.Provider;
