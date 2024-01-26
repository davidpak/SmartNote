import { useState } from 'react'
import { Listbox } from '@headlessui/react'
import { LuChevronsUpDown, LuRefreshCcw } from "react-icons/lu";

const DropdownSelect = ({
  label,
  options,
  refresh,
}: {
  label: string;
  options: string[];
  refresh: () => void;
}) => {
  const [, setSelected] = useState(options[0])

  return (
    <div className="fixed w-1/4 ml-3">
      <button
        className='flex flex-row items-center text-sm text-neutral-450'
        onClick={() => {
          refresh();
        }}
      >
        Refresh
        <LuRefreshCcw className='ml-1'/>
      </button>
      <Listbox defaultValue={label} onChange={setSelected}>
        <Listbox.Button
          className={({ open }) => `${
            open ? 'border-2 border-accent' : 'border border-neutral-450'
          } relative w-full py-2 pl-4 text-left rounded-lg shadow`}
        >
          {({ value }: { value : string }) =>
            <div
              className={`flex flex-row ${ value === label && 'text-neutral-450' }`}
            >
              {value}
              <span className="absolute inset-y-0 right-0 flex items-center pr-1">
                <LuChevronsUpDown
                  className="size-8/12 text-neutral-450"
                  aria-hidden="true"
                />
              </span>
            </div>
          }
        </Listbox.Button>
        <Listbox.Options className="flex flex-col absolute max-h-60 w-full overflow-y-scroll rounded-md py-1 shadow-lg">
          <input
            type='text'
            placeholder='Search...'
            className='relative w-5.5/6 py-2 pl-4 m-2 border border-neutral-300 shadow rounded-lg focus:outline-none'
          ></input>
          {options.map((option, index) => (
            <Listbox.Option
              key={index}
              value={option}
              className={({ active }) =>`${
                active ? 'bg-accent text-white' : 'text-black'
              } relative select-none py-2 pl-3`}
            >
              {option}
            </Listbox.Option>
          ))}
        </Listbox.Options>
      </Listbox>
    </div>

  );
};

export default DropdownSelect;
