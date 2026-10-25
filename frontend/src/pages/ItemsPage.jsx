import { useEffect, useState } from "react";
import { useItemStore } from "../store/itemStore";
import { normalize } from "../utils/text";
import { Link } from "react-router-dom";
import { formatCurrency } from "../utils/format";
import { alertClass } from "../components/uiClasses";



export default function ItemsPage() {

    const items = useItemStore((state) => state.items);
    const loading = useItemStore((state) => state.loading);
    const error = useItemStore((state) => state.error);
    const fetchItems = useItemStore((state) => state.fetchItems);
    const removeItem = useItemStore((state) => state.removeItem);

    const [search, setSearch] = useState('');
    const [deleteError, setDeleteError] = useState(null);

    useEffect(() => {
        fetchItems();
    }, [fetchItems]);

    const searchWord = normalize(search.trim());

    const filtered = items.filter((item) => {
        const fields = [
            item.name,
            item.brand,
            item.model,
            item.price
        ];

        return fields.some((value) => normalize(value).includes(searchWord));
    });

    const handleDelete = async (item) => {
        if (!window.confirm(`¿Borrar a ${item.name}?`))
            return;

        setDeleteError(null);

        try {
            await removeItem(item.id);
        } catch (err) {
            setDeleteError(err.response?.data?.message ?? 'No se ha podido borrar el artículo.');
        }
    }

    return (
        <section>
            <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
            <h2 className="text-2xl font-semibold">Artículos</h2>
            <Link
                to="/items/new"
                className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-blue-700"
            >
                Nuevo artículo
            </Link>
        </div>

        <input
            type="search"
            placeholder="Buscar por nombre, marca, modelo o precio"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="mb-4 w-full max-w-md rounded-md border border-slate-300 bg-white px-3 py-2 text-sm shadow-sm placeholder:text-slate-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
        />

        {loading && <p className="text-slate-500">Cargando...</p>}
        {error && <p role="alert" className={alertClass}>{error}</p>}
        {deleteError && <p role="alert" className={alertClass}>{deleteError}</p>}

        {!loading && !error && filtered.length === 0 && (
            <p className="rounded-lg border border-dashed border-slate-300 bg-white px-4 py-10 text-center text-slate-500">
                {items.length === 0
                    ? 'Todavía no hay artículos.'
                    : 'Ningún artículo coincide con la búsqueda.'}
            </p>
        )}
            {filtered.length > 0 && (
                        <div className="overflow-x-auto rounded-lg border border-slate-200 bg-white shadow-sm">
                            <table className="min-w-full divide-y divide-slate-200 text-sm">
                                <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                                    <tr>
                                        <th className="px-4 py-3">Artículo</th>
                                        <th className="px-4 py-3">Marca</th>
                                        <th className="px-4 py-3">Modelo</th>
                                        <th className="px-4 py-3">Precio</th>
                                        <th className="px-4 py-3">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-100">
                                    {filtered.map((item) => (
                                        <tr key={item.id} className="hover:bg-slate-50">
                                            <td className="px-4 py-3 font-medium text-slate-900">{item.name}</td>
            
                                            <td className="px-4 py-3 text-slate-600">
                                                {item.brand ?? <span className="text-slate-400"></span>}
                                            </td>
            
                                            <td className="px-4 py-3 text-slate-600">
                                                {item.model ?? <span className="text-slate-400"></span>}
                                             </td>
            
                                            <td className="px-4 py-3 text-right text-slate-900">
                                                {formatCurrency(item.price)}
                                            </td>
            
                                            <td className="px-4 py-3">
                                                <div className="flex gap-3">
                                                    <Link
                                                        to= {`/items/${item.id}/edit`}
                                                        className="font-medium text-blue-600"
                                                    >
                                                        Editar    
                                                    </Link>
                                                    <button
                                                        type="button"
                                                        onClick={()=> handleDelete(item)}
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