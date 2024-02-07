import { Fragment, useState } from 'react';
import { Listbox, Transition } from '@headlessui/react';
import { LuChevronDown as Chevron } from "react-icons/lu";
import { twMerge } from 'tailwind-merge';

interface DropdownMenuType extends React.HTMLAttributes<HTMLDivElement> {
  label: string;
  options: string[];
  selectOption: (option: string) => void;
}

const DropdownMenu = ({
  label,
  options,
  selectOption,
  className,
  ...rest
}: DropdownMenuType) => {
  const [selected, setSelected] = useState(options[0]);

  return (
    <Listbox
      value={selected}
      onChange={(e) => {
        setSelected(e);
        selectOption(e);
      }}
    >
      <div className={twMerge('flex justify-between gap-2', className)} {...rest}>
        <Listbox.Label className='font-bold'>{label}</Listbox.Label>
        <div className='w-1/2'>
          <Listbox.Button
            className='flex justify-end gap-1 w-full'
          >
            {selected}
            <Chevron className='self-center shrink-0 text-neutral-450' aria-hidden='true' />
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