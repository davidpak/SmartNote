import { Fragment, useState } from 'react';
import { Listbox, Transition } from '@headlessui/react';
import { LuChevronDown as Chevrons } from "react-icons/lu";

const DropdownMenu = ({
  label,
  options,
}: {
  label: string;
  options: String[];
}) => {
  const [selected, setSelected] = useState(options[0])

  return (
    <Listbox value={selected} onChange={setSelected}>
      <div className='flex justify-between gap-2'>
        <Listbox.Label>{label}</Listbox.Label>
        <div>
          <Listbox.Button
            className='flex justify-end gap-1 w-full'
          >
            {selected}
            <span className='self-center'>
              <Chevrons className='text-neutral-450' aria-hidden='true' />
            </span>
          </Listbox.Button>
          <Transition
            as={Fragment}
            leave='transition ease-in duration-100'
            leaveFrom='opacity-100'
            leaveTo='opacity-0'
          >
            <Listbox.Options className='w-full overflow-y-auto rounded-lg shadow-lg border border-neutral-300 mt-1 p-2'>
              {options.map((option, index) => (
                <Listbox.Option
                  key={index}
                  className={({ active }) =>
                    `${
                      active ? 'bg-accent text-white rounded-lg' : 'text-black'
                    } flex items-center gap-4 select-none py-2 pl-3`
                  }
                  value={option}
                >
                  {option}
                </Listbox.Option>
              ))}
            </Listbox.Options>
          </Transition>
        </div>
      </div>
    </Listbox>
  );
};

export default DropdownMenu;