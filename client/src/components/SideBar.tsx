import { useState } from "react";

const SideBar = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [hoveredFile, setHoveredFile] = useState(null);

  const files = ['File1.pdf', 'File2.pdf', 'File3.pdf']; // Place holder

  return (
    <div>
      <b>Upload Files</b>
      <ul style={{ listStyleType: 'none', padding: 0 }}>
        {files.map((file, index) => (
          <li key={index} onClick={() => setSelectedFile(file)} 
          onMouseOver={() => setHoveredFile(file)}
          onMouseOut={() => setHoveredFile(null)}
          style={{
            padding: '8px', 
            backgroundColor: selectedFile === file ? '#d7d7d7' : hoveredFile === file
            ? '#f0f0f0'
            : 'transparent'
          }}>
            {file}
          </li>
        ))}
      </ul>
    </div>
  );
};
export default SideBar;