import { FileRejection, ErrorCode, useDropzone } from 'react-dropzone';
import { IoCloudUploadOutline as Upload } from 'react-icons/io5';

import FileList from './FileList';
import Button from './Button';

interface DropzoneType extends React.HTMLAttributes<HTMLDivElement> {
  files: File[];
  setFiles: (files: File[]) => void;
  errors: (string | null)[];
  setErrors: (errors: (string | null)[]) => void;
}

const Dropzone = ({
  files,
  setFiles,
  errors,
  setErrors,
  className,
  ...rest
}: DropzoneType) => {
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
    <section className={className} {...rest}>
      <div
        {...getRootProps({
          className:
            'dropzone p-8 border border-dashed border-neutral-300 rounded-3xl text-center h-56',
        })}
        tabIndex={-1}
        onClick={(e) => e.stopPropagation()}
      >
        <div className='flex flex-col items-center justify-center gap-4 h-full'>
          <Upload size={40} className='text-neutral-500' aria-hidden />
          <div className='text-center'>
            <label className='font-medium text-lg text-neutral-500'>
              {isDragActive
                ? 'Drop file(s) here'
                : 'Choose a file or drag & drop it here'}
              <input type='file' className='hidden' {...getInputProps()} />
            </label>
            <p className='text-sm text-neutral-450'>PDF and PPTX formats</p>
          </div>
          <Button onClick={open} variant='secondary' className='text-sm'>
            Browse Files
          </Button>
        </div>
      </div>

      <FileList files={files} errors={errors} removeFile={removeFile} />
    </section>
  );
};

export default Dropzone;
