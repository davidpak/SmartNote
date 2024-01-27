import { useState } from 'react'
import { Listbox } from '@headlessui/react'
import { LuChevronsUpDown, LuRefreshCcw, LuFile } from 'react-icons/lu';

interface File {
  name: string;
  icon?: string;
}

const DropdownSelect = ({
  label,
  options,
  refresh,
}: {
  label: string;
  options: File[];
  refresh: () => void;
}) => {
  const [, setSelected] = useState(options[0]);
  const [searchItem, setOptions] = useState('');
  const placeholder : File = {
    name: label,
  };

  // filter out options that match the search string
  const filteredOptions = options.filter((option) => {
    return option.name.toLowerCase().includes(searchItem.toLowerCase())
  });

  return (
    <div>
      <button
        className='flex items-center text-sm text-neutral-450'
        onClick={() => {
          refresh();
        }}
      >
        Refresh
        <LuRefreshCcw className='ml-1' aria-hidden='true'/>
      </button>
      <Listbox defaultValue={placeholder} onChange={setSelected}>
        <Listbox.Button
          className={({ open }) => `${
            open ? 'border-2 border-accent' : 'border border-neutral-450'
          } relative w-full py-2 pl-4 text-left rounded-lg shadow`}
          onClick={() => setOptions('')}
        >
          {({ value }: { value : File }) =>
            <div className='flex justify-between'>
              <div className='flex items-center gap-4'>
                <span aria-hidden='true' className={`'shrink-0' ${ value.name === label && 'hidden' }`}>
                  {value.icon ? value.icon : <LuFile/>}
                </span>
                <span className={`${ value.name === label && 'text-neutral-450' }`}>
                  {value.name}
                </span>
              </div>
              <span className='self-center pr-3'>
                <LuChevronsUpDown
                  className='text-neutral-450'
                  aria-hidden='true'
                />
              </span>
            </div>
          }
        </Listbox.Button>
        <Listbox.Options className='flex flex-col absolute max-h-60 w-full overflow-y-scroll rounded-md py-1 shadow-lg'>
          <input
            type='text'
            placeholder='Search...'
            aria-label="Search through your content"
            className='relative w-5.5/6 py-2 pl-4 m-2 border border-neutral-300 shadow rounded-lg focus:outline-none'
            onChange={(e) => setOptions(e.target.value)}
            onKeyDown={(e) => { if (e.key === ' ') { e.stopPropagation(); } }}
          />
          {filteredOptions.length === 0 ? (
            <div className="relative cursor-default select-none px-4 py-2 text-gray-700">
              No results found.
            </div>
          ) : (
            filteredOptions.map((option, index) => (
              <Listbox.Option
                key={index}
                value={option}
                className={({ active }) =>`${
                  active ? 'bg-accent text-white' : 'text-black'
                } flex items-center gap-4 select-none py-2 pl-3`}
              >
                <span aria-hidden='true' className='shrink-0'>
                  {option.icon ? option.icon : <LuFile/>}
                </span>
                {option.name}
              </Listbox.Option>
            ))
          )}
        </Listbox.Options>
      </Listbox>
    </div>
  );
};

export default DropdownSelect;
