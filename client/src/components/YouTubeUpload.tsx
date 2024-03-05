import { IoLinkOutline as Link } from 'react-icons/io5';
import { twMerge } from 'tailwind-merge';

import FileList from './FileList';
import Button from './Button';

interface YoutubeUploadType extends React.HTMLAttributes<HTMLDivElement> {
  // files: File[];
  // setFiles: (files: File[]) => void;
  // errors: (string | null)[];
  // setErrors: (errors: (string | null)[]) => void;
}

const YouTubeUpload = ({
  // files,
  // setFiles,
  // errors,
  // setErrors,
  className,
  ...rest
}: YoutubeUploadType) => {
  const addFile = () => {};

  return (
    <div
      className={twMerge(
        'p-8 border border-dashed border-neutral-300 rounded-3xl text-center h-56',
        className
      )}
      {...rest}
    >
      <div className='flex flex-col items-center justify-center gap-4 h-full'>
        <Link size={40} className='text-neutral-500' aria-hidden />
        <label className='font-medium text-lg text-neutral-500'>
          Insert a valid YouTube URL
        </label>
        <div className='flex gap-3'>
          <input
            type='text'
            className='border border-neutral-400 rounded-lg px-3 w-56'
            placeholder='youtube.com/watch...'
          />
          <Button onClick={addFile} variant='secondary' className='text-sm'>
            Upload
          </Button>
        </div>
      </div>
    </div>
  );
};

export default YouTubeUpload;
