import {
  FiFileText as Text,
  FiMusic as Audio,
  FiFilm as Video,
} from 'react-icons/fi';
import Truncate from 'react-truncate-inside';

import H2 from './H2';

type FileType = 'text' | 'audio' | 'video';

interface File {
  name: string;
  type: FileType;
}

const Sidebar = ({
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
      <H2 className='text-base mb-2 px-8'>Upload Files</H2>
      <ul className='p-0 list-none'>
        {files.map((file, index) => (
          <li key={index}>
            <button
              className={`w-full pl-8 pr-4 py-2 flex items-center gap-2 rounded-r cursor-pointer transition hover:bg-neutral-150 ${
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
                <Text aria-hidden='true' className='shrink-0' />
              ) : file.type === 'audio' ? (
                <Audio aria-hidden='true' className='shrink-0' />
              ) : (
                <Video aria-hidden='true' className='shrink-0' />
              )}
              <p title={file.name} className='text-left'>
                <Truncate text={file.name} width={100} />
              </p>
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};
export default Sidebar;
