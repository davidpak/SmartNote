import { render, screen, cleanup, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';

import DropdownMenu from '../components/DropdownMenu';
import componentSmokeTest from './componentSmokeTest';

afterEach(cleanup);

componentSmokeTest({
  Component: DropdownMenu,
  props: {
    label: 'Export Format',
    options: ['RTF', 'TXT', 'PDF', 'HTML', 'Markdown & CSV'],
    selectOption: () => {},
  },
});

const dropdown =
  <DropdownMenu
    label='Export Format'
    options={['RTF', 'TXT', 'PDF', 'HTML', 'Markdown & CSV']}
    selectOption={() => {}}
    data-testid='dropdown-test'
  />;

it('should render with correct label and default value', () => {
  render(dropdown);
  expect(screen.getByLabelText('Export Format')).toHaveTextContent('RTF');
  expect(screen.getByRole('button')).toHaveAttribute('aria-expanded', 'false');
});

it('should update value when selecting a new value', () => {
  render(dropdown);
  fireEvent.click(screen.getByRole('button'));
  const options = screen.getAllByRole('option');
  fireEvent.click(options[2]);
  expect(options[0]).toHaveAttribute('aria-selected', 'false');
  expect(options[2]).toHaveAttribute('aria-selected', 'true');
  expect(screen.getByLabelText('Export Format')).toHaveTextContent('PDF');
});
