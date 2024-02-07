import { useState } from 'react';
import { Switch } from '@headlessui/react';
import { twMerge } from 'tailwind-merge';

interface ToggleType extends React.HTMLAttributes<HTMLLabelElement> {
  label: string;
}

const Toggle = ({ label, className, ...rest }: ToggleType) => {
  const [enabled, setEnabled] = useState(false);

  return (
    <label
      className={twMerge(
        'flex justify-between gap-2 text-neutral-500',
        className
      )}
      {...rest}
    >
      {label}
      <Switch
        checked={enabled}
        onChange={setEnabled}
        className={`${
          enabled ? 'bg-accent' : 'bg-neutral-300'
        } relative inline-flex h-6 w-12 items-center rounded-full shrink-0`}
      >
        <span className='sr-only'>{label}</span>
        <span
          className={`${
            enabled ? 'translate-x-7' : 'translate-x-1'
          } inline-block h-4 w-4 transform rounded-full bg-white transition`}
        />
      </Switch>
    </label>
  );
};

export default Toggle;
