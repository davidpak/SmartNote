import React from 'react';
import { IconType } from 'react-icons';
import { twMerge } from 'tailwind-merge';

type Variant = 'primary' | 'secondary' | 'tertiary';

interface ButtonType extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  icon?: IconType;
  variant?: Variant;
}

const Button = ({
  children,
  icon: Icon,
  variant = 'primary',
  className,
  ...rest
}: ButtonType) => {
  return (
    <button
      className={twMerge(
        `rounded-lg px-4 py-2 drop-shadow-md transition-all w-fit ${
          variant === 'primary'
            ? 'bg-accent text-white hover:bg-accent-dark font-semibold'
            : variant === 'secondary'
            ? 'bg-white border border-neutral-400 text-black hover:bg-neutral-200 font-medium'
            : 'font-normal drop-shadow-none hover:text-accent'
        }`,
        className
      )}
      {...rest}
    >
      <div className='flex items-center gap-2'>
        {Icon && <Icon aria-hidden='true' className='shrink-0' />}
        {children}
      </div>
    </button>
  );
};

export default Button;
