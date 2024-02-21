import { render, screen, cleanup, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';

import Carousel from '../components/Carousel';
import componentSmokeTest from './componentSmokeTest';

afterEach(cleanup);

componentSmokeTest({
  Component: Carousel,
  props: {
    imagePaths: [
      '/general-overview-ex.png',
      '/key-concepts-ex.png',
      '/conclusion-ex.png',
    ],
    titles: [
      'General Overview:',
      'Key Concepts:',
      'Conclusion:',
    ],
    descriptions: [
      'A top-level breakdown that captures a succinct summary of the uploaded file contents.',
      'Highlight the top concepts present with a brief summary.',
      'Finish off your notes with a comprehensive, succinct conclusion.',
    ],
  },
});

const carousel =
  <Carousel
    imagePaths={[
      '/general-overview-ex.png',
      '/key-concepts-ex.png',
      '/conclusion-ex.png',
    ]}
    titles={[
      'General Overview:',
      'Key Concepts:',
      'Conclusion:',
    ]}
    descriptions={[
      'A top-level breakdown that captures a succinct summary of the uploaded file contents.',
      'Highlight the top concepts present with a brief summary.',
      'Finish off your notes with a comprehensive, succinct conclusion.',
    ]}
  />;

function testImage(path: string, button?: number) {
  render(carousel);
  if (button != null) {
    const buttons = screen.getAllByRole('button');
    fireEvent.click(buttons[button]);
  }
  expect(screen.getByRole('img')).toHaveAttribute('src', path);
}

function testHeading(text: string, button?: number) {
  render(carousel);
  if (button != null) {
    const buttons = screen.getAllByRole('button');
    fireEvent.click(buttons[button]);
  }
  expect(screen.getByRole('heading')).toHaveTextContent(text);
}

function testDescription(text: string, button?: number) {
  render(carousel);
  if (button != null) {
    const buttons = screen.getAllByRole('button');
    fireEvent.click(buttons[button]);
  }
  const elts = screen.getAllByRole('generic');
  expect(elts[1]).toHaveTextContent(text);
}

it('should render with slide containing imagePaths[0]', () => {
  testImage('/general-overview-ex.png');
});

it('should render with slide containing titles[0]', () => {
  testHeading('General Overview:');
});

it('should render with slide containing descriptions[0]', () => {
  testDescription('A top-level breakdown that captures a succinct summary of the uploaded file contents.');
});

it('should update image correctly when click left', () => {
  testImage('/conclusion-ex.png', 0);
});

it('should update heading correctly when click left', () => {
  testHeading('Conclusion:', 0);
});

it('should update description correctly when click left', () => {
  testDescription('Finish off your notes with a comprehensive, succinct conclusion.', 0);
});

it('should update image correctly when click right', () => {
  testImage('/key-concepts-ex.png', 1);
});

it('should update heading correctly when click right', () => {
  testHeading('Key Concepts:', 1);
});

it('should update description correctly when click right', () => {
  testDescription('Highlight the top concepts present with a brief summary.', 1);
});
