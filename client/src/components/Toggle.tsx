import { useState } from "react";

const Toggle = () => {
  // useState hook --> tracks toggle state
  const [toggleState, setToggle] = useState(false);

  function handleOnChange() {
    setToggle(!toggleState);
  }

  return(
    <label className="flex w-20 h-10 scale-50 bg-neutral-300 rounded-full relative has-[:checked]:bg-accent">
      <input type="checkbox" checked={toggleState} onChange={handleOnChange} className="sr-only peer"></input>
      <span className="w-10 h-10 scale-75 bg-background rounded-full absolute peer-checked:left-10"></span>
    </label>
  );
};

export default Toggle