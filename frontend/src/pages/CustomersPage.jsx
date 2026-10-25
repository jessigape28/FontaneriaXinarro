import { useEffect, useState } from 'react';
import { useCustomerStore } from '../store/customerStore';
import { Link } from 'react-router-dom';
import { formatProvince } from '../constants/enums';
import { normalize } from '../utils/text';

const TYPE_LABELS = {
    INDIVIDUAL: 'Particular',
    COMPANY: 'Empresa',
    COMMUNITY: 'Comunidad',
}

const TYPE_STYLES = {
    INDIVIDUAL: 'bg-slate-100 text-slate-700',
    COMPANY: 'bg-blue-50 text-blue-700',
    COMMUNITY: 'bg-amber-50 text-amber-700',
};

const alertClass =
    'mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700';

export default function CustomersPage() {
    
    const customers = useCustomerStore((state) => state.customers);
    const loading = useCustomerStore((state) => state.loading);
    const error = useCustomerStore((state) => state.error);
    const fetchCustomers = useCustomerStore((state) => state.fetchCustomers);
    
    const [search, setSearch] = useState('');

    const removeCustomer = useCustomerStore((state) => state.removeCustomer);
    const [deleteError, setDeteleError] = useState(null);

    useEffect(() => {
        fetchCustomers();
    }, [fetchCustomers]);

    const searchWord = normalize(search.trim());

    const filtered = customers.filter((customer) => {
        const fields = [
            customer.name, 
            customer.surname, 
            customer.dniCif,
            ...customer.addresses.flatMap((address) => [
                address.alias,
                address.street,
                address.town,
                address.postalCode,
                formatProvince(address.province),
            ])
        ];

        return fields.some((value) => normalize(value).includes(searchWord));
    });

    const handleDelete = async (customer) => {
        if (!window.confirm(`¿Borrar a ${customer.name}?`))
        return;

        setDeteleError(null);
        try {
            await removeCustomer(customer.id);
        } catch (err) {
            setDeteleError(err.response?.data?.message ?? 'No se pudo borrar el cliente')
        }
    }

    return (
    <section>
        <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
            <h2 className="text-2xl font-semibold">Clientes</h2>
            <Link
                to="/customers/new"
                className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-blue-700"
            >
                Nuevo cliente
            </Link>
        </div>

        <input
            type="search"
            placeholder="Buscar por nombre, DNI/CIF o dirección"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="mb-4 w-full max-w-md rounded-md border border-slate-300 bg-white px-3 py-2 text-sm shadow-sm placeholder:text-slate-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
        />

        {loading && <p className="text-slate-500">Cargando...</p>}
        {error && <p role="alert" className={alertClass}>{error}</p>}
        {deleteError && <p role="alert" className={alertClass}>{deleteError}</p>}

        {!loading && !error && filtered.length === 0 && (
            <p className="rounded-lg border border-dashed border-slate-300 bg-white px-4 py-10 text-center text-slate-500">
                {customers.length === 0
                    ? 'Todavía no hay clientes.'
                    : 'Ningún cliente coincide con la búsqueda.'}
            </p>
        )}

        {filtered.length > 0 && (
            <div className="overflow-x-auto rounded-lg border border-slate-200 bg-white shadow-sm">
                <table className="min-w-full divide-y divide-slate-200 text-sm">
                    <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                        <tr>
                            <th className="px-4 py-3">Cliente</th>
                            <th className="px-4 py-3">Tipo</th>
                            <th className="px-4 py-3">Contacto</th>
                            <th className="px-4 py-3">Direcciones</th>
                            <th className="px-4 py-3">Administrador de fincas</th>
                            <th className="px-4 py-3">Creado por</th>
                            <th className="px-4 py-3">Acciones</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                        {filtered.map((customer) => (
                            <tr key={customer.id} className="hover:bg-slate-50">
                                <td className="px-4 py-3 align-top">
                                    <div className="font-medium text-slate-900">
                                        {[customer.name, customer.surname].filter(Boolean).join(' ')}
                                    </div>
                                    <div className="text-xs text-slate-500">{customer.dniCif}</div>
                                </td>

                                <td className="px-4 py-3 align-top">
                                    <span
                                        className={`inline-block rounded-full px-2.5 py-0.5 text-xs font-medium ${TYPE_STYLES[customer.type]}`}
                                    >
                                        {TYPE_LABELS[customer.type]}
                                    </span>
                                </td>

                                <td className="px-4 py-3 align-top text-slate-600">
                                    <div>{customer.phone}</div>
                                    <div className="break-all text-xs text-slate-500">{customer.email}</div>
                                </td>

                                <td className="px-4 py-3 align-top text-slate-600">
                                    {customer.addresses.length === 0 ? (
                                        <span className="text-slate-400">—</span>
                                    ) : (
                                        <ul className="space-y-1">
                                            {customer.addresses.map((address) => (
                                                <li key={address.id}>
                                                    {address.street}, {address.town},{' '}
                                                    {formatProvince(address.province)}
                                                </li>
                                            ))}
                                        </ul>
                                    )}
                                </td>

                                <td className="px-4 py-3 align-top text-slate-600">
                                    {customer.propertyAdministratorName ?? (
                                        <span className="text-slate-400">—</span>
                                    )}
                                </td>

                                <td className="px-4 py-3 align-top text-slate-500">
                                    {customer.createdByUsername}
                                </td>

                                <td className="px-4 py-3 align-top">
                                    <div className="flex gap-3">
                                        <Link
                                            to={`/customers/${customer.id}/edit`}
                                            className="font-medium text-blue-600 hover:underline"
                                        >
                                            Editar
                                        </Link>
                                        <button
                                            type="button"
                                            onClick={() => handleDelete(customer)}
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
)
}