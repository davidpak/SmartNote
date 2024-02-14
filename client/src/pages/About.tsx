import Body from '../components/Body';
import H1 from '../components/H1';
import H2 from '../components/H2';

const About = () => {
  return (
    <div className='flex flex-col items-center gap-4 text-center max-w-4xl m-auto'>
      <H1 className='mb-2'>What is SmartNote?</H1>
      <H2>Intelligent note-taking with AI, integrated with Notion.</H2>
      <Body>
        Streamlining the process of transforming educational content to easily
        parsable notes, one file at a time.
      </Body>
      <div className='flex items-center gap-8'>
        <img src='/about.svg' alt='' className='w-56' />
        <div className='mt-4 font-bold text-3xl flex flex-col gap-8'>
          <div className='flex items-center gap-4' aria-hidden>
            <img src='/notion.svg' alt='' className='w-12' />
            <p>Notion API</p>
          </div>
          <div className='flex items-center gap-4' aria-hidden>
            <span>🦜🔗</span>
            <p>LangChain</p>
          </div>
        </div>
      </div>
      <H2>Overview</H2>
      <Body>
        SmartNote is an automated note-taking organization application that aims
        to revolution of students engage with educational content. SmartNote
        automates the process of creating detailed notes from PDFs, slideshows,
        and audio transcripts. It also provides a simple way to transfer the
        generated notes to the user's Notion workspace.
      </Body>
      <H2>How does it work?</H2>
      <Body>
        Once your files are uploaded, first customize the structure and desired
        sections of your generated notes!
      </Body>
      <img src='/about-customization.png' alt='' className='max-w-lg' />
      <Body>
        Then, adjust and review the output by leveraging our breakdown tree:
      </Body>
      <img src='/about-breakdown-tree.png' alt='' className='max-w-2xl' />
    </div>
  );
};

export default About;
