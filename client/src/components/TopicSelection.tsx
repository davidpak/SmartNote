import { useState, useEffect, useRef } from 'react';
import { CheckTree } from 'rsuite';
import 'rsuite/dist/rsuite.min.css';
import { IoMdArrowBack as Arrow } from 'react-icons/io';
import { twMerge } from 'tailwind-merge';
import Markdown from 'react-markdown';

import H2 from './H2';
import H3 from './H3';
import Button from './Button';
import Sidebar from './Sidebar';
import { File } from './Sidebar';
import jsonFile from './example.json';
import mdFile from './output.md';

interface TopicSelectionType extends React.HTMLAttributes<HTMLDivElement> {
  files: File[];
  prev: () => void;
  next: () => void;
}

interface NodeType {
  value: string;
  label: string;
  children?: NodeType[];
}

interface TextType {
  type: string;
  literal: string;
}

interface EmphasisType {
  type: string;
  openingDelimeter: string;
  closingDelimeter: string;
  children: TextType[];
}

interface ParagraphType {
  type: string;
  children: (EmphasisType | TextType)[];
}

interface ListItemType {
  type: string;
  children: (ParagraphType | BulletListType)[];
}

interface BulletListType {
  type: string;
  bulletMarker: string;
  children: ListItemType[];
}

interface HeadingType {
  type: string;
  level: number;
  children: TextType[];
}

const TopicSelection = ({
  files,
  prev,
  next,
  className,
  ...rest
}: TopicSelectionType) => {
  const [fileIndex, setFileIndex] = useState<number>(0); // keep track of which file is selected

  const originalMd = useRef(''); // store the original markdown
  const [markdown, setMarkdown] = useState(''); // keep track of the updated markdown

  useEffect(() => {
    fetch(mdFile)
      .then(res => res.text())
      .then(text => {
        originalMd.current = text;
        setMarkdown(text);
      });
  }, []);

  function isHeadingType(object: any): object is HeadingType {
    return 'level' in object;
  }

  function isEmphasisType(object: any): object is EmphasisType {
    return 'openingDelimeter' in object;
  }

  const data: NodeType[] = [];
  function parseJson() {
    const entries = jsonFile.children;

    // keep track of which node we need to add children to next
    let parentNode: NodeType = {value: '', label: ''};

    for (let entry of entries) {
      if (isHeadingType(entry)) {
        const node = {
          value: entry.children[0].literal,
          label: entry.children[0].literal,
        };

        if (entry.level == 1 || entry.level == 2) { // parent topics
          data.push(node);
          parentNode = node;
        } else if (entry.level == 3) { // children topics (in "Section by Section Breakdown")
          if (parentNode.children) {
            parentNode.children.push(node);
          } else {
            parentNode.children = [node];
          }
        }
      } else if (entry.type === 'bulletList' && entry.bulletMarker === '-') { // children topics
        const listItems: ListItemType[] = entry.children;
        const children: NodeType[] = [];
        for (let item of listItems) {
          const itemChildren = item.children;
          if (!isEmphasisType(itemChildren[0].children[0])) {
            break;
          }
          const node = {
            value: itemChildren[0].children[0].children[0].literal,
            label: itemChildren[0].children[0].children[0].literal,
          };
          children.push(node);
        }
        if (children.length != 0) {
          parentNode.children = children;
        }
      }
    }
  }
  parseJson();

  // keep track of which boxes/topics are selected, all selected on default
  // selectedTopics type is string[] but use (string | number)[] to align with CheckTree type
  const [selectedTopics, setSelectedTopics] = useState<(string | number)[]>(data.map((node) => node.value));

  function getNotSelectedTopics(selectedTopics: (string | number)[]) {
    const topicsNotSelected: string[] = [];

    for (let node of data) {
      if (!selectedTopics.includes(node.value)) {
        if (node.children) {
          let numChildNotSelected = 0;
          for (let child of node.children) {
            if (!selectedTopics.includes(child.value)) {
              topicsNotSelected.push(child.value);
              numChildNotSelected += 1;
            }
          }

          // include the section title if all its subsections are included
          if (numChildNotSelected == node.children.length) {
            topicsNotSelected.push(node.value);
          }
        } else {
          topicsNotSelected.push(node.value);
        }
      }
    }

    return topicsNotSelected;
  }

  function removeText(originalText: string, textToRemove: string): string {
    // find where textToRemove start & go back a few chars to also remove its markdown syntax
    // it's ok if it's negative since substring() will clamp it to 0 if it is
    let startIndex = originalText.indexOf(textToRemove) - 4;

    // find where textToRemove end (i.e. where next topic start)
    const nextHeadingIndex = originalText.substring(startIndex + 4).indexOf('#');
    const nextBulletIndex = originalText.substring(startIndex + 4).indexOf('- *');
    let endIndex = startIndex + 4;
    if (nextHeadingIndex === -1 && nextBulletIndex === -1) { // no next topic
      endIndex = originalText.length;
    } else if (nextHeadingIndex === -1) { // only sub topic left
      endIndex += nextBulletIndex;
    } else if (nextBulletIndex === -1) { // only big topic left
      endIndex += nextHeadingIndex;
    } else { // get the closest next topic
      endIndex += Math.min(nextHeadingIndex, nextBulletIndex);
    }

    const substringToRemove = originalText.substring(startIndex, endIndex);
    return originalText.replace(substringToRemove, '');
  }

  return (
    <div
      className={twMerge('flex flex-col items-center gap-10', className)}
      {...rest}
    >
      <Button
        icon={Arrow}
        variant='tertiary'
        className='absolute left-16'
        onClick={() => prev()}
      >
        Back
      </Button>
      <H2 className='text-center'>Select Topics to Include</H2>
      <section className='flex bg-neutral-100 max-w-5xl'>
        <Sidebar
          files={files}
          activeIndex={fileIndex}
          selectFile={(index) => setFileIndex(index)}
          className='border-neutral-400 border-r-2 pr-1 pt-5'
        />
        <section className='border-neutral-400 border-r-2 px-2 py-3'>
          <H3 className='text-base self-center pl-3 mb-2 pt-2'>Breakdown</H3>
          <CheckTree
            data={data}
            value={selectedTopics}
            onChange={(values) => {
              setSelectedTopics(values);
              const topicsNotSelected = getNotSelectedTopics(values);
              let newMd = originalMd.current;
              for (let topic of topicsNotSelected) {
                newMd = removeText(newMd, topic);
              }
              setMarkdown(newMd);
            }}
            defaultExpandAll
            showIndentLine
          />
        </section>
        <section className='h-96 p-5 overflow-auto'>
          <H3 className='text-base mb-2'>Output Preview</H3>
          <Markdown
            className='flex flex-col bg-white p-6 gap-3'
            components={{
              h1: H3,
              h2(props) {
                const {node, ...rest} = props
                return <H3 className='text-base mb-2' {...rest} />
              },
              h3(props) {
                const {node, ...rest} = props
                return <p className='font-bold' {...rest} />
              },
              ul(props) {
                const {node, ...rest} = props
                return <ul className='list-disc pl-6 flex flex-col gap-3' {...rest} />
              },
            }}
          >
            {markdown}
          </Markdown>
        </section>
      </section>
      <Button onClick={() => next()}>Continue</Button>
    </div>
  );
};

export default TopicSelection;
