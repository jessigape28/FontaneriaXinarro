import { useEffect, useState } from "react";
import { useItemStore } from "../store/itemStore";
import { toNull } from "../utils/text";
import { alertClass, buttonPrimary, buttonSecondary, cardClass, inputClass } from "../components/uiClasses";
import Field from "../components/Field";
import { Link, useNavigate, useParams } from "react-router-dom";
import { getItem } from "../api/items";

const initialForm = {
    name: '',
    brand: '',
    model: '',
    price: ''
}

const toForm = (item) => ({
    name: item.name,
    brand: item.brand,
    model: item.model,
    price: item.price
})

export default function ItemFormPage() {
    const navigate = useNavigate();
    const { id } = useParams();
    const isEditing = Boolean(id);

    const addItem = useItemStore((state) => state.addItem);
    const editItem = useItemStore((state) => state.editItem);

    const [form, setForm] = useState(initialForm);
    const [error, setError] = useState(null);
    const [saving, setSaving] = useState(false);
    const [loading, setLoading] = useState(isEditing);

    useEffect(() => {
        if (!isEditing) return;

        getItem(id)
            .then((item) => setForm(toForm(item)))
            .catch((error) => {
                console.error(error);
                setError('No se ha podido cargar el artículo');
            })
            .finally(() => setLoading(false));
    }, [id, isEditing]);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    }

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);
        setSaving(true);

        const payload = {
            name: form.name.trim(),
            brand: toNull(form.brand),
            model: toNull(form.model),
            price: Number(form.price)
        }

        try {
            if (isEditing) {
                await editItem(id, payload);
            } else {
                await addItem(payload);
            }

            navigate('/items');
        } catch (err) {
            setError(err.response?.data?.message ?? 'No se ha podido guardar el artículo');
        } finally {
            setSaving(false);
        }
    }

    if (loading) return <p className="text-slate-500">Cargando...</p>;

return (
    <form 
        onSubmit={handleSubmit}
        className="mx-auto max-w-3xl space-y-6"
    >
        <h2 className="text-2xl font-semibold">
            {isEditing ? 'Editar artículo' : 'Nuevo artículo'}
        </h2>

        <section className={cardClass}>
            <h3 className="mb-4 text-base font-semibold">Datos del artículo</h3>
            <div className="grid gap-4 sm:grid-cols-2">
                <Field label="Nombre" htmlFor="name">
                    <input 
                        id="name" 
                        name="name" 
                        value={form.name} 
                        onChange={handleChange}
                        required className={inputClass}
                        />
                </Field>
                <Field label="Marca" htmlFor="brand">
                    <input 
                        id="brand" 
                        name="brand" 
                        value={form.brand} 
                        onChange={handleChange}
                        className={inputClass}
                        />
                </Field>
                <Field label="Modelo" htmlFor="model">
                    <input 
                        id="model" 
                        name="model" 
                        value={form.model} 
                        onChange={handleChange}
                        className={inputClass}
                        />
                </Field>
                <Field label="Precio" htmlFor="price">
                    <input 
                        type="number"
                        id="price" 
                        name="price" 
                        step="0.01"
                        min="0"
                        value={form.price} 
                        onChange={handleChange}
                        required className={inputClass}
                        />
                </Field>

            </div>
        </section>

        {error && <p role="alert" className={alertClass}>{error}</p>}

        <div className="flex justify-end gap-3">
            <Link to="/items" className={buttonSecondary}>Cancelar</Link>
            <button type="submit" disabled={saving} className={buttonPrimary}>
                {saving ? 'Guardando...' : 'Guardar artículo'}
            </button>
        </div>

    </form>
)

}