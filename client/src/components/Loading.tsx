import { AnimatedCircle } from 'react-craftify-spinners';
import { IoMdArrowBack as Arrow } from 'react-icons/io';

import Button from './Button';

const Loading = ({ prev }: { prev: () => void }) => {
  return (
    <div className='max-w-48 m-auto'>
      <Button
        icon={Arrow}
        variant='tertiary'
        className='absolute left-16'
        onClick={() => prev()}
      >
        Back
      </Button>
      <div className='flex flex-col items-center gap-8 text-center py-40'>
        <AnimatedCircle color='#375EF9' />
        <p>Generating your notes...</p>
      </div>
    </div>
  );
};

export default Loading;
