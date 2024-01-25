import {
  FiFileText as Text,
  FiMusic as Audio,
  FiFilm as Video,
} from 'react-icons/fi';

import H2 from './H2';

type FileType = 'text' | 'audio' | 'video';

interface File {
  name: string;
  type: FileType;
}

const SideBar = ({
  files,
  activeIndex,
  selectFile,
}: {
  files: File[];
  activeIndex: number;
  selectFile: (index: number) => void;
}) => {
  return (
    <div>
      <H2 className='text-base mb-2 px-4'>Upload Files</H2>
      <ul className='p-0 list-none'>
        {files.map((file, index) => (
          <li key={index}>
            <button
              className={`w-full px-4 py-2 flex items-center gap-2 rounded-r cursor-pointer transition ${
                activeIndex === index && 'bg-neutral-200 relative'
              }`}
              onClick={() => {
                selectFile(index);
              }}
            >
              {activeIndex === index && (
                <span
                  className='absolute block inset-0 w-1 bg-accent'
                  aria-hidden='true'
                ></span>
              )}
              {file.type === 'text' ? (
                <Text aria-hidden='true' />
              ) : file.type === 'audio' ? (
                <Audio aria-hidden='true' />
              ) : (
                <Video aria-hidden='true' />
              )}
              {file.name}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};
export default SideBar;