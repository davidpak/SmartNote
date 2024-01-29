import { useState } from "react";

const Slider = ({
  label,
  levels,
}: {
  label: string;
  levels: string[];
}) => {
  const [level, setLevel] = useState(0);

  return (
    <div className='flex flex-col gap-3'>
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
          e.target.style.backgroundSize = (val / (levels.length - 1)) * 100 + "%"
        }}
        className="slider"
      />
    </div>
  );
};

export default Slider;
