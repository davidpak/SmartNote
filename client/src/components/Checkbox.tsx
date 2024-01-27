import { useEffect, useRef } from 'react';

const Checkbox = ({
  label,
  isChecked = false,
  isIndeterminate = false,
  onChange,
}: {
  label: string;
  isChecked?: boolean;
  isIndeterminate?: boolean;
  onChange: () => void;
}) => {
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (inputRef.current) {
      inputRef.current.indeterminate = isIndeterminate;
    }
  });

  return (
    <div className='p-4'>
      <label className='flex gap-3 items-center'>
        <input
          type='checkbox'
          checked={isChecked}
          onChange={onChange}
          ref={inputRef}
          className={`appearance-none w-[22px] h-[22px] rounded-sm cursor-pointer transition relative${
            isIndeterminate
              ? ' bg-accent before:content-minusIcon before:absolute before:m-auto before:inset-x-0 before:-top-1.5 before:text-center'
              : isChecked
              ? ' bg-accent before:content-checkIcon before:absolute before:mx-auto before:inset-x-0 before:top-0.5 before:text-center'
              : ' bg-neutral-300'
          }`}
        />
        {label}
      </label>
    </div>
  );
};

export default Checkbox;
