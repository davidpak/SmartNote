import React from 'react';
import { IconType } from 'react-icons';

type Variant = 'primary' | 'secondary' | 'tertiary';

interface ButtonType {
  children: React.ReactNode;
  icon?: IconType;
  variant?: Variant;
}

const Button = ({ children, icon: Icon, variant = 'primary' }: ButtonType) => {
  return (
    <button
      className={`font-semibold rounded-lg px-4 py-2 drop-shadow-md transition-all ${
        variant === 'primary'
          ? 'bg-accent text-white hover:bg-accent-dark'
          : variant === 'secondary'
          ? 'bg-white border border-neutral-400 text-black hover:bg-neutral-200'
          : 'font-normal drop-shadow-none hover:text-accent'
      }`}
    >
      <div className='flex items-center gap-1'>
        {Icon && <Icon />}
        {children}
      </div>
    </button>
  );
};

export default Button;
