import { AnimatedCircle } from 'react-craftify-spinners';
import { IoMdArrowBack as Arrow } from 'react-icons/io';

import Button from './Button';
import { useNavigate } from 'react-router-dom';

const Loading = () => {
  const navigate = useNavigate();

  return (
    <div className='max-w-lg m-auto'>
      <Button
        icon={Arrow}
        variant='tertiary'
        className='absolute left-16'
        onClick={() => navigate('/customize')}
      >
        Back
      </Button>
      <div className='flex flex-col items-center gap-8 text-center py-40'>
        <AnimatedCircle color='#375EF9' />
        <p className='text-xl font-bold text-black'>Generating your notes...</p>
      </div>
    </div>
  );
};

export default Loading;
