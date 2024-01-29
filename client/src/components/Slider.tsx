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
    <div className='flex flex-col gap-1'>
      <label className='flex justify-between'>
        <span className='text-neutral-500'>{label}</span>
        <output className='text-accent'>{levels[level]}</output>
      </label>
      <input
        type='range'
        min='0'
        max={levels.length - 1}
        defaultValue={0}
        onChange={(e) => {
          setLevel(parseInt(e.target.value));
        }}
      />
    </div>
  );
};

export default Slider;
