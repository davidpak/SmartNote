import React from 'react';
import { IconType } from 'react-icons';
import { twMerge } from 'tailwind-merge';

type Variant = 'primary' | 'secondary' | 'tertiary';

interface ButtonType extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children: React.ReactNode;
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
        'rounded-lg px-4 py-2 drop-shadow-sm transition-all w-fit disabled:cursor-not-allowed',
        variant === 'primary'
          ? 'bg-accent text-white hover:bg-accent-dark font-semibold disabled:bg-neutral-200 disabled:text-neutral-425'
          : variant === 'secondary'
          ? 'bg-white border border-neutral-400 text-black enabled:hover:bg-neutral-200 font-medium disabled:border-neutral-200 disabled:text-neutral-425'
          : 'font-normal drop-shadow-none hover:text-accent disabled:text-neutral-425',
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
