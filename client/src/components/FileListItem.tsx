import prettyBytes from 'pretty-bytes';
import { FiX as Remove } from 'react-icons/fi';
import { FaCircleCheck as Success } from 'react-icons/fa6';
import { IoMdWarning as Error } from 'react-icons/io';

export interface File {
  name: string;
  size: number; // in bytes
}

const FileListItem = ({
  file,
  errorMessage,
  onRemove,
  className,
}: {
  file: File;
  errorMessage?: string;
  onRemove?: () => void;
  className?: string;
}) => {
  const { name, size } = file;
  const type = name.split('.').slice(-1)[0];

  return (
    <div
      className={`${className ?? ''} flex w-full justify-between items-center`}
    >
      <div className='flex items-center gap-5'>
        <img
          src={`/${type === 'pdf' || type === 'pptx' ? type : 'default'}.svg`}
          alt=''
          className='w-6 drop-shadow-sm'
        />
        <div className='flex items-end gap-5'>
          <div className='flex flex-col'>
            <p className='font-medium text-neutral-500'>{name}</p>
            <div className='flex gap-1'>
              <p className='text-sm text-neutral-450 w-16'>
                {prettyBytes(size)}
              </p>
              <div className='flex items-center gap-2 text-sm text-neutral-500'>
                {errorMessage ? (
                  <>
                    <Error size={18} className='text-warning shrink-0' />
                    <p>{errorMessage}</p>
                  </>
                ) : (
                  <>
                    <Success className='text-success shrink-0' />
                    <p>Completed</p>
                  </>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
      <button
        onClick={onRemove}
        aria-label={`Remove ${name}`}
        className='h-fit'
      >
        <Remove size={24} className='text-neutral-500' />
      </button>
    </div>
  );
};

export default FileListItem;
