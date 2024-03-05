import FileListItem from './FileListItem';

interface FileListType {
  files: File[];
  errors: (string | null)[];
  removeFile: (idx: number) => void;
}

const FileList = ({ files, errors, removeFile }: FileListType) => {
  return (
    <>
      {files.length > 0 && (
        <ul className='flex flex-col gap-4 mt-6'>
          {files.map((file: File, index: number) => (
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
