import { IoMdArrowBack as Arrow } from 'react-icons/io';
import { useEffect, useState } from 'react';

import Body from './Body';
import Button from './Button';
import H2 from './H2';
import ExportModal from './ExportModal';
import DropdownMenu from './DropdownMenu';

export type FormatType = 'txt' | 'rtf' | 'md';

const CLIENT_ID = '42429aa5-68fe-48dd-9cae-d0702fb33b39';
const REDIRECT_URI = 'http://localhost:5173';

const ConnectToNotion = ({
  prev,
  next,
  setNotesUrl,
}: {
  prev: () => void;
  next: () => void;
  setNotesUrl: (url: string) => void;
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
      next();
      exportToNotion(code);
    }
  };

  const exportToNotion = async (code: string) => {
    try {
      const body = {
        data: markdown,
        exporter: 'notion',
        remote: {
          mode: 'new',
          code: code,
          redirectUri: 'http://localhost:5173',
        },
      };

      const res = await fetch('http://localhost:4567/api/v1/export', {
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
        <ExportModal
          markdown={markdown}
          filename={`smartnote.${format}`}
          format={format}
          onExport={() => {
            next();
          }}
        >
          <div className='flex flex-col gap-4'>
            <DropdownMenu
              label='Export Format'
              options={['TXT', 'RTF', 'Markdown']}
              selectOption={(value) => {
                setFormat(
                  value === 'TXT ' ? 'txt' : value === 'RTF' ? 'rtf' : 'md'
                );
              }}
            />
          </div>
        </ExportModal>
      </section>
    </div>
  );
};

export default ConnectToNotion;
