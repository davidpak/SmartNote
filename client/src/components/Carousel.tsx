import { useState } from 'react';
import { twMerge } from 'tailwind-merge';
import { LuChevronLeft as LeftChevron, LuChevronRight as RightChevron } from 'react-icons/lu';

import H3 from './H3';
import Body from './Body';
import Button from './Button';

interface CarouselType extends React.HTMLAttributes<HTMLDivElement> {
  imagePaths: string[];
  titles: string[];
  descriptions: string[];
}

const Carousel = ({
  imagePaths,
  titles,
  descriptions,
  className,
  ...rest
}: CarouselType) => {
  const [slideIndex, setSlideIndex] = useState(0);

  return (
    <div className={twMerge('flex flex-col text-center gap-3', className)} {...rest}>
      <section>
        <H3>{titles[slideIndex]}</H3>
        <Body>{descriptions[slideIndex]}</Body>
      </section>
      <section className='flex text-neutral-450 text-4xl items-center'>
        <span>
          <Button
            variant='tertiary'
            onClick={() => setSlideIndex(() => {
              if (slideIndex === 0) {
                return imagePaths.length - 1;
              }
              return slideIndex - 1;
            })}
          >
            <LeftChevron aria-hidden='true' className='shrink-0'/>
          </Button>
        </span>
        <img src={imagePaths[slideIndex]} alt='' className='max-w-3xl' />
        <span>
            <Button
            variant='tertiary'
            onClick={() => setSlideIndex(() => {
              if (slideIndex === imagePaths.length - 1) {
                return 0;
              }
              return slideIndex + 1;
            })}
          >
            <RightChevron aria-hidden='true' className='shrink-0' />
          </Button>
        </span>
      </section>
    </div>
  );
};

export default Carousel;
