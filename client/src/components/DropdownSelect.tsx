import { useState } from 'react'
import { Listbox } from '@headlessui/react'
import { LuChevronsUpDown } from "react-icons/lu";

const DropdownSelect = ({
  label,
  options,
}: {
  label: string;
  options: string[];
}) => {
  const [selected, setSelected] = useState(options[0])

  return (
    <div className="fixed w-1/2">
      <Listbox defaultValue={label} onChange={setSelected}>
        <div>
          <Listbox.Button
            className={({ open }) => `${
              open ? 'border-2 border-accent' : 'border border-neutral-400'
            } relative w-full py-2 pl-3 text-left rounded-lg shadow`}
          >
            {({ value }: { value : string }) =>
              <div className='flex flex-row'>
                {value}
                <span className="absolute inset-y-0 right-0 flex items-center pr-1">
                  <LuChevronsUpDown
                    className="size-8/12 text-neutral-400"
                    aria-hidden="true"
                  />
                </span>
              </div>
            }
          </Listbox.Button>
          <Listbox.Options className="flex flex-col absolute max-h-60 w-full overflow-auto rounded-md py-1 shadow-lg">
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
        </div>
      </Listbox>
    </div>

  );
};

export default DropdownSelect;
