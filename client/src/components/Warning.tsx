import { useState } from "react";

const Warning = () => {
  return(
    <div className="flex w-96 h-10 pl-4 bg-warning rounded-md items-center">
      <span className="text-xs">Invalid file format.</span>
    </div>
  );
};

export default Warning