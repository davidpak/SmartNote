import prettyBytes from 'pretty-bytes';
import { FiX as Remove } from 'react-icons/fi';
import { IoMdWarning as Error } from 'react-icons/io';
import { twMerge } from 'tailwind-merge';
import Truncate from 'react-truncate-inside';

import { VideoType } from './YouTubeUpload';

export interface File {
  name: string;
  size: number; // in bytes
}

interface FileListItemType extends React.HTMLAttributes<HTMLDivElement> {
  file: File | VideoType;
  errorMessage?: string;
  onRemove?: () => void;
}

const FileListItem = ({
  file,
  errorMessage,
  onRemove,
  className,
  ...rest
}: FileListItemType) => {
  let name: string,
    size: string | undefined,
    url: string | undefined,
    type: string;
  if (file.hasOwnProperty('url')) {
    file = file as VideoType;
    name = file.name;
    url = file.url;
    type = 'video';
  } else {
    file = file as File;
    name = file.name;
    size = prettyBytes(file.size);
    type = name.split('.').slice(-1)[0];
  }

  return (
    <div
      className={twMerge('flex w-full justify-between items-center', className)}
      {...rest}
    >
      <div className='flex items-center gap-5'>
        <img
          src={`/${type === 'pdf' || type === 'pptx' || type === 'video' ? type : 'default'}.svg`}
          alt=''
          className='w-6 drop-shadow-sm'
        />
        <div className='flex gap-5'>
          <div className='flex flex-col items-start text-start'>
            <div className='font-medium text-neutral-500' title={name}>
              <Truncate text={name} width={300} />
            </div>
            <div className='flex gap-1 items-start'>
              {url && (
                <div className='text-sm text-neutral-450' title={url}>
                  <Truncate text={url} width={250} />
                </div>
              )}
              {size && <p className='text-sm text-neutral-450 w-16'>{size}</p>}
              <div className='flex items-center gap-2 text-sm text-neutral-500'>
                {errorMessage && (
                  <>
                    <Error
                      size={18}
                      className='text-warning shrink-0'
                      aria-hidden
                    />
                    <p>{errorMessage}</p>
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
        <Remove size={24} className='text-neutral-500' aria-hidden />
      </button>
    </div>
  );
};

export default FileListItem;
