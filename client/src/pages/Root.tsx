import { Outlet } from 'react-router-dom';
import { useEffect, useState } from 'react';

import Navbar from '../components/Navbar';
import { FilesContextProvider } from '../contexts/FilesContext';
import { OutputContextProvider } from '../contexts/OutputContext';
import { ExportContextProvider } from '../contexts/ExportContext';
import { JsonType } from './TopicSelection';

const Root = () => {
  const [files, setFiles] = useState<string[]>([]);
  const [markdown, setMarkdown] = useState<string>('');
  const [json, setJson] = useState<JsonType | undefined>(undefined);
  const [notesUrl, setNotesUrl] = useState<string>('https://www.notion.so/');

  return (
    <FilesContextProvider
      value={{ files: files, setFiles: (files: string[]) => setFiles(files) }}
    >
      <OutputContextProvider
        value={{
          markdown: markdown,
          json: json,
          setMarkdown: (markdown: string) => setMarkdown(markdown),
          setJson: (json: JsonType) => setJson(json),
        }}
      >
        <ExportContextProvider
          value={{
            notesUrl: notesUrl,
            setNotesUrl: (url: string) => setNotesUrl(url),
          }}
        >
          <Navbar />
          <main className='px-16 py-14'>
            <Outlet />
          </main>
        </ExportContextProvider>
      </OutputContextProvider>
    </FilesContextProvider>
  );
};

export default Root;
