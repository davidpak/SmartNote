import { Fragment, useState } from 'react';
import { Listbox, Transition } from '@headlessui/react';
import { LuChevronDown as Chevrons } from "react-icons/lu";

const DropdownMenu = ({
  label,
  options,
  selectOption,
}: {
  label: string;
  options: string[];
  selectOption: (option: string) => void;
}) => {
  const [selected, setSelected] = useState(options[0]);

  return (
    <Listbox
      value={selected}
      onChange={(e) => {
        setSelected(e);
        selectOption(e);
      }}
    >
      <div className='flex justify-between gap-2'>
        <Listbox.Label className='font-bold'>{label}</Listbox.Label>
        <div className='w-1/2'>
          <Listbox.Button
            className='flex justify-end gap-1 w-full'
          >
            {selected}
            <Chevrons className='self-center shrink-0 text-neutral-450' aria-hidden='true' />
          </Listbox.Button>
          <Transition
            as={Fragment}
            leave='transition ease-in duration-100'
            leaveFrom='opacity-100'
            leaveTo='opacity-0'
          >
            <Listbox.Options className='absolute bg-white z-10 rounded-lg shadow-lg border border-neutral-300 p-1'>
              {options.map((option, index) => (
                <Listbox.Option
                  key={index}
                  className={({ active }) =>
                    `${
                      active ? 'bg-accent text-white rounded-lg' : 'text-black'
                    } flex items-center gap-4 select-none py-1 px-3`
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