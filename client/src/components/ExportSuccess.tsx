import { IoMdArrowBack as Arrow } from 'react-icons/io';
import { twMerge } from 'tailwind-merge';

import H2 from './H2';
import Body from './Body';
import Button from './Button';

interface ExportSuccessType extends React.HTMLAttributes<HTMLDivElement> {
  prev: () => void;
  goHome: () => void;
  notesUrl: string;
}

const ExportSuccess = ({
  prev,
  goHome,
  notesUrl,
  className,
  ...rest
}: ExportSuccessType) => {
  return (
    <div
      className={twMerge('flex flex-col items-center gap-5', className)}
      {...rest}
    >
      <Button
        icon={Arrow}
        variant='tertiary'
        className='absolute left-16'
        onClick={() => prev()}
      >
        Back
      </Button>
      <H2>Export Successful!</H2>
      <img src='/export.png' alt='' className='w-64' />
      <Body>Congrats! Check out your new notes page :&#41;</Body>
      <div className='flex gap-5'>
        <Button onClick={goHome} variant='secondary'>
          Home
        </Button>
        <a href={notesUrl} target='_blank' tabIndex={-1}>
          <Button>Go to Notion</Button>
        </a>
      </div>
    </div>
  );
};

export default ExportSuccess;
