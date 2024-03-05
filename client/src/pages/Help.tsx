import Body from '../components/Body';
import H1 from '../components/H1';
import H2 from '../components/H2';

const Help = () => {
  return (
    <div className='flex flex-col items-center gap-6 text-center max-w-4xl m-auto'>
      <H1>Need Help?</H1>
      <section>
        <H2 className='mb-4'>Contact Us</H2>
        <Body className='font-semibold text-lg text-neutral-500'>
          SmartNote is a work in progress, feedback and questions are welcome!
        </Body>
        <Body>Email us at smartnote.help@gmail.com</Body>
      </section>
      <img src='/contactUs.png' alt='' className='w-64' />
    </div>
  );
};

export default Help;
