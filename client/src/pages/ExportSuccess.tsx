import { IoMdArrowBack as Arrow } from 'react-icons/io';
import { twMerge } from 'tailwind-merge';

import H2 from '../components/H2';
import Body from '../components/Body';
import Button from '../components/Button';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useExportContext } from '../contexts/ExportContext';

const ExportSuccess = ({
  className,
  ...rest
}: React.HTMLAttributes<HTMLDivElement>) => {
  const navigate = useNavigate();
  const { notesUrl } = useExportContext();
  const [searchParams, setSearchParams] = useSearchParams();
  const isToNotion = notesUrl !== '';

  useEffect(() => {
    if (searchParams.has('code')) {
      searchParams.delete('code');
    }
    if (searchParams.has('state')) {
      searchParams.delete('state');
    }
    setSearchParams(searchParams);
  }, []);

  return (
    <div
      className={twMerge('flex flex-col items-center gap-5', className)}
      {...rest}
    >
      <Button
        icon={Arrow}
        variant='tertiary'
        className='absolute left-16'
        onClick={() => navigate('/connect')}
      >
        Back
      </Button>
      <H2>Export Successful!</H2>
      <img src='/export.png' alt='' className='w-64' />
      <Body>
        {isToNotion
          ? 'Congrats! Check out your new notes page :)'
          : 'Congrats! Your notes have been exported :)'}
      </Body>
      <div className='flex gap-5'>
        <Button onClick={() => navigate('/')} variant='secondary'>
          Home
        </Button>
        {isToNotion && (
          <a href={notesUrl} target='_blank' tabIndex={-1}>
            <Button>View in Notion</Button>
          </a>
        )}
      </div>
    </div>
  );
};

export default ExportSuccess;
