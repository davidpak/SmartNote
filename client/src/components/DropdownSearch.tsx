import { Dispatch, Fragment, SetStateAction, useState } from 'react';
import { Listbox, Transition } from '@headlessui/react';
import { LuChevronsUpDown as Chevrons, LuFile as File } from 'react-icons/lu';

export interface File {
  name: string;
  icon?: string;
}

const DropdownSearch = ({
  label,
  options,
  selectPage,
}: {
  label: string;
  options: File[];
  selectPage: Dispatch<SetStateAction<File>>;
}) => {
  const [searchOptions, setSearchOptions] = useState(options);
  const placeholder: File = {
    name: label,
  };

  return (
    <div>
      <Listbox defaultValue={placeholder} onChange={selectPage}>
        <Listbox.Button
          className='border border-neutral-450 focus:outline-accent relative w-full py-2 pl-4 text-left rounded-lg shadow'
          onClick={() => setSearchOptions(options)}
        >
          {({ value }: { value: File }) => (
            <div className='flex justify-between'>
              <div className='flex items-center gap-4'>
                <span
                  aria-hidden='true'
                  className={`'shrink-0' ${value.name === label && 'hidden'}`}
                >
                  {value.icon ?? <File />}
                </span>
                <span
                  className={`${value.name === label && 'text-neutral-450'}`}
                >
                  {value.name}
                </span>
              </div>
              <span className='self-center pr-3'>
                <Chevrons className='text-neutral-450' aria-hidden='true' />
              </span>
            </div>
          )}
        </Listbox.Button>

        <Transition
          as={Fragment}
          enterFrom='opacity-0'
          enterTo='opacity-100'
          enter='transition ease-in duration-100'
          leave='transition ease-in duration-100'
          leaveFrom='opacity-100'
          leaveTo='opacity-0'
        >
          <Listbox.Options className='flex flex-col absolute max-h-60 w-full overflow-y-scroll rounded-md shadow-lg mt-1'>
            <div className='sticky top-0 w-full p-2 bg-white'>
              <input
                type='text'
                placeholder='Search'
                aria-label='Search for a page'
                className='w-5.5/6 py-2 pl-4 border border-neutral-300 shadow rounded-lg w-full focus:outline-accent'
                onChange={(e) => {
                  setSearchOptions(
                    options.filter((option) => {
                      // Get search results
                      return option.name
                        .toLowerCase()
                        .includes(e.target.value.toLowerCase());
                    })
                  );
                }}
                onKeyDown={(e) => {
                  if (e.key === ' ') {
                    e.stopPropagation();
                  }
                }}
              />
            </div>

            {searchOptions.length === 0 ? (
              <div className='cursor-default select-none px-4 py-2 text-gray-700'>
                No results found.
              </div>
            ) : (
              searchOptions.map((option: File, index: number) => (
                <Listbox.Option
                  key={index}
                  value={option}
                  className={({ active }) =>
                    `${
                      active ? 'bg-accent text-white' : 'text-black'
                    } flex items-center gap-4 select-none py-2 pl-3`
                  }
                >
                  <span aria-hidden='true' className='shrink-0'>
                    {option.icon ?? <File />}
                  </span>
                  {option.name}
                </Listbox.Option>
              ))
            )}
          </Listbox.Options>
        </Transition>
      </Listbox>
    </div>
  );
};

export default DropdownSearch;
