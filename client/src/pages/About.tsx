import Body from '../components/Body';
import H1 from '../components/H1';
import H2 from '../components/H2';
import Carousel from '../components/Carousel';

const About = () => {
  return (
    <div className='flex flex-col gap-8 text-center max-w-4xl m-auto'>
      <H1 className='mb-2'>What is SmartNote?</H1>
      <section className='flex flex-col items-center gap-5'>
        <H2>Intelligent note-taking with AI, integrated with Notion.</H2>
        <Body>
          Streamlining the process of transforming educational content to easily
          parsable notes, one file at a time.
        </Body>
        <div className='flex items-center gap-8'>
          <img src='/about.png' alt='' className='w-56' />
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
      </section>
      <section className='flex flex-col items-center gap-5'>
        <H2>Overview</H2>
        <Body>
          SmartNote is an automated note-taking organization application that aims
          to revolution of students engage with educational content. SmartNote
          automates the process of creating detailed notes from PDFs, slideshows,
          and audio transcripts. It also provides a simple way to transfer the
          generated notes to the user's Notion workspace.
        </Body>
      </section>
      <section className='flex flex-col items-center gap-3'>
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
      </section>
      <section className='flex flex-col items-center gap-5'>
        <H2>Need Examples?</H2>
        <Carousel
          imagePaths={[
            '/general-overview-ex.png',
            '/key-concepts-ex.png',
            '/section-breakdown-ex.png',
            '/additional-info-ex.png',
            '/vocab-ex.png',
            '/5th-grader-ex.png',
            '/conclusion-ex.png',
          ]}
          titles={[
            'General Overview:',
            'Key Concepts:',
            'Sections Breakdown:',
            'Additional Information:',
            'Vocab words:',
            '5th Grader Explanation:',
            'Conclusion:',
          ]}
          descriptions={[
            'A top-level breakdown that captures a succinct summary of the uploaded file contents.',
            'Highlight the top concepts present with a brief summary.',
            'The core content breakdown, the bread and butter of your notes.',
            'Extra information that might be of interest to you.',
            'Capture vocabulary words and definitions in a convenient and concise manner.',
            'An intuitive and easy-to-comprehend summary.',
            'Finish off your notes with a comprehensive, succinct conclusion.',
          ]}
        />
      </section>
      <section className='flex flex-col items-center gap-5'>
        <H2>
          Lastly, Connect your <span className='text-accent'>Notion Workspace!</span>
        </H2>
        <Body>
          Connect your Notion workspace to export your notes.
          Make sure to select one or more Notion pages.
        </Body>
      </section>
    </div>
  );
};

export default About;
