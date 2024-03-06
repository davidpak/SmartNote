import { twMerge } from 'tailwind-merge';

import FileListItem from './FileListItem';
import { VideoType } from './YouTubeUpload';

interface FileListType extends React.HTMLAttributes<HTMLUListElement> {
  files: (File | VideoType)[];
  errors: (string | null)[];
  removeFile: (idx: number) => void;
}

const FileList = ({
  files,
  errors,
  removeFile,
  className,
  ...rest
}: FileListType) => {
  return (
    <>
      {files.length > 0 && (
        <ul
          className={twMerge('flex flex-col gap-4 mt-6', className)}
          {...rest}
        >
          {files.map((file: File | VideoType, index: number) => (
            <li key={index}>
              <FileListItem
                file={file}
                onRemove={() => removeFile(index)}
                errorMessage={errors[index] ?? undefined}
              />
            </li>
          ))}
        </ul>
      )}
    </>
  );
};

export default FileList;
