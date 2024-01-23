import { useState } from "react";

const SideBar = () => {
  const [selectedFile, setSelectedFile] = useState(null);

  const files = ['File1.pdf', 'File2.pdf', 'File3.pdf'];

  return (
    <div>
      <b>Upload Files</b>
      <ul style={{ listStyleType: 'none', padding: 0 }}>
        {files.map((file, index) => (
          <li key={index} onClick={() => setSelectedFile(file)} style={{
            padding: '8px', 
            backgroundColor: selectedFile === file ? '#d7d7d7' : 'transparent', // Change background color for selected file
          }}>
            {file}
          </li>
        ))}
      </ul>
    </div>
  );
};
export default SideBar;