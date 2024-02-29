import { createContext, useContext } from 'react';

interface PageContextType {
  pageIndex: number;
  next: () => void;
  prev: () => void;
  home: () => void;
}

const PageContext = createContext<PageContextType>({
  pageIndex: 0,
  next: () => {},
  prev: () => {},
  home: () => {},
});

export const usePageContext = () => useContext(PageContext);
export const PageContextProvider = PageContext.Provider;
