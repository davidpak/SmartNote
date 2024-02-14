import { IoMdArrowBack as Arrow } from 'react-icons/io';

import Body from './Body';
import Button from './Button';
import H2 from './H2';
import ExportModal from './ExportModal';
import DropdownMenu from './DropdownMenu';
import { useState } from 'react';

type FormatType = 'txt' | 'rtf' | 'md';

const ConnectToNotion = ({
  prev,
  next,
}: {
  prev: () => void;
  next: () => void;
}) => {
  const [format, setFormat] = useState<FormatType>();

  // const exportFile = async () => {
  // need to first export to the intended format and get the name of the resource
  // const fileName; // need name of resource
  // const res = await fetch(
  //   `http://localhost:4567/api/v1/fetch?name=${fileName}`,
  //   {
  //     method: 'GET',
  //     credentials: 'include',
  //   }
  // );
  // if (!res.ok) {
  //   throw new Error('HTTP error ' + res.status);
  // }
  // const fileData = await res.body;
  // const url = window.URL.createObjectURL(new Blob([fileData]));
  // };

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
        <Button>Connect to Notion</Button>
      </section>
      <section className='flex flex-col items-center gap-3 text-center'>
        <H2>No Notion? No Problem.</H2>
        <Body className='max-w-xl mb-3'>
          Export to your desired alternate file format. Flexibility in learning
          is key.
        </Body>
        <ExportModal
          // this is only for demo. exportUrl should be generated using the notes
          // file obtained from back-end (maybe use a Blob object to create a file
          // of `format` type)
          exportUrl=''
          exportFilename=''
          onExport={next}
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
