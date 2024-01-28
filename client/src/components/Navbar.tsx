import { NavLink } from 'react-router-dom';

const Navbar = () => {
  return (
    <nav className='navbar bg-base-100 sticky z-10 w-full px-12 py-5 border-b border-b-neutral-300'>
      <div className='flex w-full items-center justify-between'>
        <NavLink to='/' className='w-40 shrink-0' aria-label='Home'>
          <img src='/smartnote-logo.svg' alt='' />
        </NavLink>

        <ul className='flex items-center gap-10'>
          <li className='w-14'>
            <NavLink
              to='/'
              className={({ isActive }) =>
                `hover:text-accent transition duration-200 ${
                  isActive ? 'font-bold' : ''
                }`
              }
            >
              Home
            </NavLink>
          </li>
          <li className='w-14'>
            <NavLink
              to='/about'
              className={({ isActive }) =>
                `hover:text-accent transition duration-200 ${
                  isActive ? 'font-bold' : ''
                }`
              }
            >
              About
            </NavLink>
          </li>
          <li className='w-14'>
            <NavLink
              to='/help'
              className={({ isActive }) =>
                `hover:text-accent transition duration-200 ${
                  isActive ? 'font-bold' : ''
                }`
              }
            >
              Help
            </NavLink>
          </li>
        </ul>
      </div>
    </nav>
  );
};

export default Navbar;
