import { Dialog, Transition } from '@headlessui/react';
import { useState, Fragment } from 'react';

import Button from './Button';

const ExportModal = ({
  children,
  exportUrl,
  exportFilename,
}: {
  children: React.ReactNode;
  exportUrl: string;
  exportFilename: string;
}) => {
  const [open, setOpen] = useState(false);

  function download(): void {
    const aTag = document.createElement('a');
    aTag.href = exportUrl;
    aTag.download = exportFilename;
    document.body.appendChild(aTag);
    aTag.click();
    document.body.removeChild(aTag);
  }

  return (
    <div>
      <Button
        children='Export'
        onClick={() => setOpen(true)}
      >
      </Button>

      <Transition
        show={open}
        as={Fragment}
        enterFrom='opacity-0'
        enterTo='opacity-100'
        enter='transition ease-in duration-100'
        leave='transition ease-in duration-100'
        leaveFrom='opacity-100'
        leaveTo='opacity-0'
      >
        <Dialog onClose={() => setOpen(false)}>
          <div className="fixed inset-0 bg-black/50"/>
          <div className="fixed inset-0 flex items-center justify-center overflow-y-auto">
            <Dialog.Panel className="w-96 flex flex-col gap-5 rounded-2xl bg-white p-5 shadow-xl transition-all">
              {children}
              <div className='flex justify-center gap-3'>
                <Button
                  children='Cancel'
                  variant='secondary'
                  onClick={() => setOpen(false)}
                />
                <Button
                  children='Export'
                  onClick={() => {
                    download();
                    setOpen(false);
                  }
                  }
                />
              </div>
            </Dialog.Panel>
          </div>
        </Dialog>
      </Transition>
    </div>
  )
};

export default ExportModal;