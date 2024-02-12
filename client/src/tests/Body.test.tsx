import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';

import Body from '../components/Body';
import componentSmokeTest from './componentSmokeTest';

componentSmokeTest({ Component: Body, children: 'test' });

it('should render paragraph with text', () => {
  render(<Body>test</Body>);
  expect(screen.getByText('test')).toBeInTheDocument();
});
