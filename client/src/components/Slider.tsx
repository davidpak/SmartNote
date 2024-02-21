import { useState } from 'react';
import { twMerge } from 'tailwind-merge';

interface SliderType extends React.HTMLAttributes<HTMLDivElement> {
  label: string;
  levels: string[];
  updateLevel: (level: number) => void;
}

const Slider = ({
  label,
  levels,
  updateLevel,
  className,
  ...rest
}: SliderType) => {
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
          updateLevel(val / (levels.length - 1)); // scale to between 0 and 1
          e.target.style.backgroundSize =
            (val / (levels.length - 1)) * 100 + '%';
        }}
        className='slider'
      />
    </div>
  );
};

export default Slider;
