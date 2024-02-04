import { FileRejection, ErrorCode, useDropzone } from 'react-dropzone';
import { IoCloudUploadOutline as Upload } from 'react-icons/io5';

import FileListItem from './FileListItem';

const Dropzone = ({
  files,
  setFiles,
  errors,
  setErrors,
}: {
  files: File[];
  setFiles: (files: File[]) => void;
  errors: (string | null)[];
  setErrors: (errors: (string | null)[]) => void;
}) => {
  const handleDrop = (
    acceptedFiles: File[],
    fileRejections: FileRejection[]
  ) => {
    const rejectedFiles = fileRejections.map(({ file }) => file);

    const newErrors = fileRejections.map(({ errors }) => {
      const error = errors[0]; // Get first error for each file
      if (error.code === ErrorCode.FileInvalidType) {
        return 'Invalid file type';
      }
      return error.message;
    });

    setFiles([...files, ...acceptedFiles, ...rejectedFiles]);
    setErrors([...errors, ...acceptedFiles.map(() => null), ...newErrors]);
  };

  const { getRootProps, getInputProps, isDragActive, open } = useDropzone({
    accept: {
      'application/vnd.openxmlformats-officedocument.presentationml.presentation':
        [], // .pptx
      'application/pdf': [],
    },
    onDrop: handleDrop,
  });

  const removeFile = (idx: number) => {
    setFiles(files.filter((_, index) => idx !== index));
    setErrors(errors.filter((_, index) => idx !== index));
  };

  return (
    <>
      <div
        {...getRootProps({
          className:
            'dropzone px-8 py-12 border border-dashed border-neutral-300 rounded-3xl text-center',
        })}
        tabIndex={-1}
      >
        <div className='flex flex-col items-center gap-4'>
          <Upload size={40} className='text-neutral-500' />
          <div className='text-center'>
            <label className='font-medium text-lg text-neutral-500'>
              {isDragActive
                ? 'Drop file(s) here'
                : 'Choose a file or drag & drop it here'}
              <input type='file' className='hidden' {...getInputProps()} />
            </label>
            <p className='text-sm text-neutral-450'>PDF and PPTX formats</p>
          </div>

          <button
            onClick={open}
            className='text-sm text-neutral-475 border border-neutral-300 rounded-xl px-4 py-2 hover:bg-neutral-150 transition'
          >
            Browse Files
          </button>
        </div>
      </div>

      {files.length > 0 && (
        <ul className='flex flex-col gap-4'>
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

export default Dropzone;
