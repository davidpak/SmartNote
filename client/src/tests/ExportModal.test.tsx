import { render, screen, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom';

import ExportModal from '../components/ExportModal';
import componentSmokeTest from './componentSmokeTest';

afterEach(cleanup);

componentSmokeTest({
  Component: ExportModal,
  props: {
    exportUrl: '../components/Toggle',
    exportFilename: 'smartnotes.txt',
  },
});

const modal =
  <ExportModal
    exportUrl='../components/Toggle'
    exportFilename={'smartnotes.txt'}
    onExport={() => {}}
    data-testid='modal-test'
  />;

it('should render button with correct label', () => {
  render(modal);
  expect(screen.getByRole('button')).toHaveTextContent('Export');
});
