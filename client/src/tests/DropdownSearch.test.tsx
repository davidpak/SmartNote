import DropdownSearch from '../components/DropdownSearch';
import componentSmokeTest from './componentSmokeTest';

componentSmokeTest({
  Component: DropdownSearch,
  props: {
    label: 'Select a Notion page',
    options: [
      { name: 'Untitled' },
      { name: 'List of Movies to Watch', icon: '🎬' },
      { name: 'Some Random Blogs', icon: '📷' },
      { name: 'A B C D E F G H I J K Long M N O Page', icon: '🐞' },
      { name: 'Roadmap' },
      { name: 'University Hub', icon: '📚' },
      { name: 'Weekly Project Meeting', icon: '📝' },
    ],
    selectPage: () => {},
  },
});
