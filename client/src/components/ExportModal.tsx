import { Dialog, Transition } from '@headlessui/react';
import { useState, Fragment } from 'react';

import Button from './Button';

import { FormatType } from './ConnectToNotion';

const BASE_URL = import.meta.env.VITE_SERVER_BASE_URL;

interface ExportModalType extends React.HTMLAttributes<HTMLDivElement> {
  markdown: string;
  format: FormatType;
  onExport: () => void;
}

const ExportModal = ({
  markdown,
  format,
  onExport,
  children,
  className,
  ...rest
}: ExportModalType) => {
  const [isOpen, setIsOpen] = useState(false);

  const exportNotes = async (format: FormatType) => {
    if (format === 'md') {
      return markdown;
    } else {
      try {
        const body = {
          data: markdown,
          exporter: format,
        };

        const res = await fetch(`${BASE_URL}/export`, {
          method: 'POST',
          credentials: 'include',
          body: JSON.stringify(body),
        });

        if (!res.ok) {
          throw new Error('HTTP error ' + res.status);
        }

        const json = await res.json();
        const notes = await getNotes(json.name);
        return notes;
      } catch (e) {
        console.error(e);
      }
    }
  };

  const getNotes = async (name: string) => {
    try {
      const res = await fetch(`${BASE_URL}/fetch?name=${name}`, {
        method: 'GET',
        credentials: 'include',
      });
      const text = await res.text();
      return text;
    } catch (e) {
      console.error(e);
    }
  };

  const padValue = (value: number) => {
    return value.toString().padStart(2, '0');
  };

  const download = async () => {
    const data = await exportNotes(format);
    const file = new Blob([data!], {
      type:
        format === 'txt'
          ? 'text/plain'
          : format === 'rtf'
            ? 'application/rtf'
            : 'text/markdown',
    });
    const exportUrl = URL.createObjectURL(file);

    const now = new Date();
    const date = `${now.getFullYear()}-${padValue(now.getMonth() + 1)}-${padValue(now.getDate())}`;
    const hour =
      now.getHours() === 0 || now.getHours() === 12 ? 12 : now.getHours() % 12;
    const time = `${padValue(hour)}.${padValue(now.getMinutes())}.${padValue(now.getSeconds())}${now.getHours() < 12 ? 'am' : 'pm'}`;
    const filename = `smartnote-${date}-at-${time}.${format}`;

    const aTag = document.createElement('a');
    aTag.href = exportUrl;
    aTag.download = filename;
    document.body.appendChild(aTag);
    aTag.click();
    document.body.removeChild(aTag);
    onExport();
  };

  return (
    <div className={className} {...rest}>
      <Button children='Export' onClick={() => setIsOpen(true)}></Button>

      <Transition
        show={isOpen}
        as={Fragment}
        enterFrom='opacity-0'
        enterTo='opacity-100'
        enter='transition ease-in duration-100'
        leave='transition ease-in duration-100'
        leaveFrom='opacity-100'
        leaveTo='opacity-0'
      >
        <Dialog onClose={() => setIsOpen(false)}>
          <div className='fixed inset-0 bg-black/50' />
          <div className='fixed inset-0 flex items-center justify-center overflow-y-auto'>
            <Dialog.Panel className='w-96 flex flex-col gap-5 rounded-2xl bg-white p-5 shadow-xl transition-all'>
              {children}
              <div className='flex justify-center gap-3'>
                <Button
                  children='Cancel'
                  variant='secondary'
                  onClick={() => setIsOpen(false)}
                />
                <Button
                  children='Export'
                  onClick={() => {
                    download();
                    setIsOpen(false);
                  }}
                />
              </div>
            </Dialog.Panel>
          </div>
        </Dialog>
      </Transition>
    </div>
  );
};

export default ExportModal;
