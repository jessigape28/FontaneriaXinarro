import { useEffect, useState } from "react";
import { useSupplierStore } from "../store/supplierStore";
import { normalize } from "../utils/text";
import { Link } from "react-router-dom";
import { alertClass } from "../components/uiClasses";
import { formatProvince, PAYMENT_OPTION_LABELS } from "../constants/enums";

export default function SuppliersPage() {

    const suppliers = useSupplierStore((state) => state.suppliers);
    const loading = useSupplierStore((state) => state.loading);
    const error = useSupplierStore((state) => state.error);
    const fetchSuppliers = useSupplierStore((state) => state.fetchSuppliers);
    const removeSupplier = useSupplierStore((state) => state.removeSupplier);

    const [search, setSearch] = useState('');
    const [deleteError, setDeleteError] = useState(null);

    useEffect(() => {
        fetchSuppliers();
    }, [fetchSuppliers]);

    const searchWord = normalize(search.trim());

    const filtered = suppliers.filter((supplier) => {
        const fields = [
            supplier.name, 
            supplier.cif
        ];

        return fields.some((value) => normalize(value).includes(searchWord));
    });

    const handleDelete = async (supplier) => {
        if (!window.confirm(`¿Borrar a ${supplier.name}?`))
            return;

        setDeleteError(null);

        try {
            await removeSupplier(supplier.id);
        } catch (err) {
            setDeleteError(err.response?.data?.message ?? 'No se ha podido borrar el proveedor');
        }
    }

    return (
        <section>
            <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
                <h2 className="text-2xl font-semibold">Proveedores</h2>
                <Link
                    to="/suppliers/new"
                    className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-blue-700"
                >
                    Nuevo proveedor
                </Link>
            </div>

            <input
                type="search"
                placeholder="Buscar por nombre o CIF"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="mb-4 w-full max-w-md rounded-md border border-slate-300 bg-white px-3 py-2 text-sm shadow-sm placeholder:text-slate-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
            />

            {loading && <p className="text-slate-500">Cargando...</p>}
            {error && <p role="alert" className={alertClass}>{error}</p>}
            {deleteError && <p role="alert" className={alertClass}>{deleteError}</p>}

            {!loading && !error && filtered.length === 0 && (
                <p className="rounded-lg border border-dashed border-slate-300 bg-white px-4 py-10 text-center text-slate-500">
                    {suppliers.length === 0
                        ? 'Todavía no hay proveedores.'
                        : 'Ningún proveedor coincide con la búsqueda.'}
                </p>
            )}

            {filtered.length > 0 && (
                <div className="overflow-x-auto rounded-lg border border-slate-200 bg-white shadow-sm">
                    <table className="min-w-full divide-y divide-slate-200 text-sm">
                        <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                            <tr>
                                <th className="px-4 py-3">Proveedor</th>
                                <th className="px-4 py-3">Dirección</th>
                                <th className="px-4 py-3">Forma de pago</th>
                                <th className="px-4 py-3">Creado por</th>
                                <th className="px-4 py-3">Acciones</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {filtered.map((supplier) => (
                                <tr key={supplier.id} className="hover:bg-slate-50">
                                    <td className="px-4 py-3 align-top">
                                        <div className="font-medium text-slate-900">
                                            {supplier.name}
                                        </div>
                                        <div className="text-xs text-slate-500">{supplier.cif}</div>
                                    </td>    
                                    <td className="px-4 py-3 align-top text-slate-600">
                                        <div>
                                            {supplier.address ?? <span className="text-slate-400"></span>}
                                        </div>
                                        <div className="text-xs text-slate-500">
                                            {[supplier.postalCode, supplier.province && formatProvince(supplier.province)]
                                                .filter(Boolean)
                                                .join(' ')}
                                        </div> 
                                    </td>
                                    
                                    
                                    <td className="px-4 py-3 align-top text-slate-600">
                                        {PAYMENT_OPTION_LABELS[supplier.paymentOption]}
                                    </td>
                                    <td className="px-4 py-3 align-top text-slate-500">
                                        {supplier.createdByUsername}
                                    </td>
                                    <td className="px-4 py-3 align-top">
                                    <div className="flex gap-3">
                                        <Link
                                            to={`/suppliers/${supplier.id}/edit`}
                                            className="font-medium text-blue-600 hover:underline"
                                        >
                                            Editar
                                        </Link>
                                        <button
                                            type="button"
                                            onClick={() => handleDelete(supplier)}
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