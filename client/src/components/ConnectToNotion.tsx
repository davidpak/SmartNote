import { IoMdArrowBack as Arrow } from 'react-icons/io';
import { useEffect, useState } from 'react';

import Body from './Body';
import Button from './Button';
import H2 from './H2';
import ExportModal from './ExportModal';
import DropdownMenu from './DropdownMenu';

const CLIENT_ID = import.meta.env.VITE_NOTION_CLIENT_ID;
const REDIRECT_URI = import.meta.env.VITE_REDIRECT_URI;
const BASE_URL = import.meta.env.VITE_SERVER_BASE_URL;

export type FormatType = 'txt' | 'rtf' | 'md' | 'json';

const ConnectToNotion = ({
  prev,
  next,
  setNotesUrl,
  setIsNotion,
}: {
  prev: () => void;
  next: () => void;
  setNotesUrl: (url: string) => void;
  setIsNotion: (isNotion: boolean) => void;
}) => {
  const [format, setFormat] = useState<FormatType>('txt');
  const markdown = localStorage.getItem('markdown')!;

  const authenticate = () => {
    window.location.href = `https://api.notion.com/v1/oauth/authorize?client_id=${encodeURIComponent(
      CLIENT_ID
    )}&response_type=code&owner=user&redirect_uri=${encodeURIComponent(
      REDIRECT_URI
    )}`;
  };

  const handleRedirect = () => {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');

    if (code) {
      setIsNotion(true);
      next();
      exportToNotion(code);
    }
  };

  const padValue = (value: number) => {
    return value.toString().padStart(2, '0');
  };

  const exportToNotion = async (code: string) => {
    try {
      const now = new Date();
      const date = `${now.getFullYear()}-${padValue(now.getMonth() + 1)}-${padValue(now.getDate())}`;
      const time = `${padValue(now.getHours() % 12)}:${padValue(now.getMinutes())}:${padValue(now.getSeconds())}${now.getHours() < 12 ? 'am' : 'pm'}`;
      const pagename = `SmartNote ${date} ${time}`;

      const body = {
        data: markdown,
        exporter: 'notion',
        output: pagename,
        remote: {
          mode: 'new',
          code: code,
          redirectUri: REDIRECT_URI,
        },
      };

      const res = await fetch(`${BASE_URL}/export`, {
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(body),
      });

      if (!res.ok) {
        throw new Error('HTTP error ' + res.status);
      }

      const json = await res.json();
      setNotesUrl(json.url);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    handleRedirect();
  }, []);

  return (
    <div className='flex flex-col items-center gap-10 text-center'>
      <section className='flex flex-col items-center gap-3 text-center'>
        <Button
          icon={Arrow}
          variant='tertiary'
          className='absolute left-16'
          onClick={() => prev()}
        >
          Back
        </Button>
        <H2>
          Connect your <span className='text-accent'>Notion Workspace</span>
        </H2>
        <Body className='max-w-xl mb-3'>
          Connect your Notion workspace to export your notes.{' '}
          <span className='font-bold'>
            Make sure to select one or more Notion pages.
          </span>
        </Body>
        <Button onClick={authenticate}>Connect to Notion</Button>
      </section>
      <section className='flex flex-col items-center gap-3 text-center'>
        <H2>No Notion? No Problem.</H2>
        <Body className='max-w-xl mb-3'>
          Export to your desired alternate file format. Flexibility in learning
          is key.
        </Body>
        <img src='/alternateExport.png' alt='' className='w-64' />
        <ExportModal
          markdown={markdown}
          format={format}
          onExport={() => {
            setIsNotion(false);
            next();
          }}
        >
          <div className='flex flex-col gap-4'>
            <DropdownMenu
              label='Export Format'
              options={['txt', 'rtf', 'md', 'json']}
              selectOption={(value: FormatType) => {
                setFormat(value);
              }}
            />
          </div>
        </ExportModal>
      </section>
    </div>
  );
};

export default ConnectToNotion;
