import { useState } from 'react';
import api from '../api/api';
import { useAuthStore } from '../store/authStore';
import Field from '../components/Field';
import { alertClass, buttonPrimary, cardClass, inputClass } from '../components/uiClasses';

export default function LoginPage() {
    const setUser = useAuthStore((state) => state.setUser);

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);
        setLoading(true);

        try {
            const { data } = await api.post('/auth/login', { username, password });
            setUser(data);
        } catch (err) {
            setError(err.response?.status === 401 ? 'Usuario o contraseña incorrectos' : 'No se pudo conectar con el servidor');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4">
            <form onSubmit={handleSubmit} className={`${cardClass} w-full max-w-sm space-y-5`}>
            <div className="text-center">
                <h1 className="text-2xl font-semibold text-blue-700">Fontanería Xinarro</h1>
                <p className="mt-1 text-sm text-slate-500">Inicia sesión para continuar</p>
            </div>

            <Field label="Usuario" htmlFor="username">
                <input
                    id="username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    autoComplete="username"
                    autoFocus
                    required
                    className={inputClass}
                />
            </Field>

            <Field label="Contraseña" htmlFor="password">
                <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    autoComplete="current-password"
                    required
                    className={inputClass}
                />
            </Field>

            {error && <p role="alert" className={alertClass}>{error}</p>}

            <button type="submit" disabled={loading} className={`${buttonPrimary} w-full`}>
                {loading ? 'Cargando...' : 'Iniciar sesión'}
            </button>
        </form>
        </div>
        
    )
}