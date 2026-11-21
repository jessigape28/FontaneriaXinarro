import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { usePropertyAdministratorStore } from "../store/propertyAdministratorStore";
import { normalize } from "../utils/text";
import { alertClass } from "../components/uiClasses";

export default function PropertyAdministratorsPage() {

    const propertyAdministrators = usePropertyAdministratorStore((state) => state.propertyAdministrators);
    const loading = usePropertyAdministratorStore((state) => state.loading);
    const error = usePropertyAdministratorStore((state) => state.error);
    const fetchPropertyAdministrators = usePropertyAdministratorStore((state) => state.fetchPropertyAdministrators);
    const removePropertyAdministrator = usePropertyAdministratorStore((state) => state.removePropertyAdministrator);

    const [search, setSearch] = useState('');
    const [deleteError, setDeleteError] = useState(null);

    useEffect(() => {
        fetchPropertyAdministrators();
    }, [fetchPropertyAdministrators]);

    const searchWord = normalize(search.trim());

    const filtered = propertyAdministrators.filter((propertyAdministrator) => {
        const fields = [
            propertyAdministrator.name,
            propertyAdministrator.email,
            propertyAdministrator.phone,
        ];

        return fields.some((value) => normalize(value).includes(searchWord));
    });

    const handleDelete = async (propertyAdministrator) => {
        if (!window.confirm(`¿Borrar a ${propertyAdministrator.name}?`))
            return;

        setDeleteError(null);

        try {
            await removePropertyAdministrator(propertyAdministrator.id);
        } catch (err) {
            setDeleteError(err.response?.data?.message ?? 'No se ha podido borrar el administrador de fincas');
        }
    };

    return (
        <section>
            <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
                <h2 className="text-2xl font-semibold">Administradores de fincas</h2>
                <Link
                    to="/property-administrators/new"
                    className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-blue-700"
                >
                    Nuevo administrador
                </Link>
            </div>

            <input
                type="search"
                placeholder="Buscar por nombre, email o teléfono"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="mb-4 w-full max-w-md rounded-md border border-slate-300 bg-white px-3 py-2 text-sm shadow-sm placeholder:text-slate-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
            />

            {loading && <p className="text-slate-500">Cargando...</p>}
            {error && <p role="alert" className={alertClass}>{error}</p>}
            {deleteError && <p role="alert" className={`${alertClass} mb-4`}>{deleteError}</p>}

            {!loading && !error && filtered.length === 0 && (
                <p className="rounded-lg border border-dashed border-slate-300 bg-white px-4 py-10 text-center text-slate-500">
                    {propertyAdministrators.length === 0
                        ? 'Todavía no hay administradores de fincas.'
                        : 'Ningún administrador coincide con la búsqueda.'}
                </p>
            )}

            {filtered.length > 0 && (
                <div className="overflow-x-auto rounded-lg border border-slate-200 bg-white shadow-sm">
                    <table className="min-w-full divide-y divide-slate-200 text-sm">
                        <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                            <tr>
                                <th className="px-4 py-3">Administrador</th>
                                <th className="px-4 py-3">Contacto</th>
                                <th className="px-4 py-3">Acciones</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {filtered.map((propertyAdministrator) => (
                                <tr key={propertyAdministrator.id} className="hover:bg-slate-50">
                                    <td className="px-4 py-3 align-top font-medium text-slate-900">
                                        {propertyAdministrator.name}
                                    </td>
                                    <td className="px-4 py-3 align-top text-slate-600">
                                        <div className="break-all">{propertyAdministrator.email}</div>
                                        <div className="text-xs text-slate-500">
                                            {propertyAdministrator.phone ?? <span className="text-slate-400">—</span>}
                                        </div>
                                    </td>
                                    <td className="px-4 py-3 align-top">
                                        <div className="flex gap-3">
                                            <Link
                                                to={`/property-administrators/${propertyAdministrator.id}/edit`}
                                                className="font-medium text-blue-600 hover:underline"
                                            >
                                                Editar
                                            </Link>
                                            <button
                                                type="button"
                                                onClick={() => handleDelete(propertyAdministrator)}
                                                className="font-medium text-red-600 hover:underline"
                                            >
                                                Borrar
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </section>
    );
}
