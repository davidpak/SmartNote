import { useState } from 'react';
import { twMerge } from 'tailwind-merge';

interface SliderType extends React.HTMLAttributes<HTMLDivElement> {
  label: string;
  levels: string[];
}

const Slider = ({ label, levels, className, ...rest }: SliderType) => {
  const [level, setLevel] = useState(0);

  return (
    <div className={twMerge('flex flex-col gap-3', className)} {...rest}>
      <label htmlFor='verbosity' className='flex justify-between gap-3'>
        <span className='text-neutral-500'>{label}</span>
        <output className='text-accent'>{levels[level]}</output>
      </label>
      <input
        id='verbosity'
        type='range'
        min={0}
        max={levels.length - 1}
        defaultValue={0}
        onChange={(e) => {
          const val = parseInt(e.target.value);
          setLevel(val);
          e.target.style.backgroundSize =
            (val / (levels.length - 1)) * 100 + '%';
        }}
        className='slider'
      />
    </div>
  );
};

export default Slider;
