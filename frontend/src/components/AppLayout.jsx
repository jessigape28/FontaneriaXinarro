import { NavLink, Outlet } from 'react-router-dom';
import api from '../api/api';
import { useAuthStore } from '../store/authStore';

const navLinkClass = ({ isActive }) => 
    `rounded-md px-3 py-2 text-sm font-medium transition-colors 
        ${ isActive ? 'bg-blue-50 text-blue-700' : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900' }`

export default function AppLayout() {
    const user = useAuthStore((state) => state.user);
    const clearUser = useAuthStore((state) => state.clearUser);

    const handleLogout = () => {
    api.post('/auth/logout').finally(() => clearUser());
    }

    return (
        <div className="min-h-screen bg-slate-50 text-slate-900">
            <header className="border-b border-slate-200 bg-white">
                <div className="mx-auto flex max-w-7xl flex-wrap items-center justify-between gap-4 px-4 py-3">
                    <div className="flex items-center gap-6">
                        <span className="text-lg font-semibold text-blue-700">
                            Fontanería Xinarro
                        </span>
                        <nav className="flex gap-1">
                            <NavLink to="/" end className={navLinkClass}>Inicio</NavLink>
                            <NavLink to="/customers" className={navLinkClass}>Clientes</NavLink>
                            <NavLink to="/items" className={navLinkClass}>Artículos</NavLink>
                        </nav>
                    </div>

                    <div className="flex items-center gap-3 text-sm">
                        <span className="text-slate-600">
                            {user.fullName}{' '}
                            <span className="text-slate-400">({user.username})</span>
                        </span>
                        <button
                            type="button"
                            onClick={handleLogout}
                            className="rounded-md border border-slate-300 px-3 py-1.5 font-medium text-slate-700 hover:bg-slate-100"
                        >
                            Cerrar sesión
                        </button>
                    </div>
                </div>
            </header>

            <main className="mx-auto max-w-7xl px-4 py-6">
                <Outlet />
            </main>
        </div>
    )
}