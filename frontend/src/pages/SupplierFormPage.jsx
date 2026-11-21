import { Link, useNavigate, useParams } from "react-router-dom"
import { useSupplierStore } from "../store/supplierStore";
import { useEffect, useState } from "react";
import { getSupplier } from "../api/suppliers";
import Field from "../components/Field";
import { alertClass, buttonPrimary, buttonSecondary, cardClass, inputClass } from "../components/uiClasses";
import { formatProvince, PAYMENT_OPTIONS, PROVINCES } from "../constants/enums";
import { toNull } from "../utils/text";

const initialForm = {
    name: '',
    cif: '',
    address: '',
    postalCode: '',
    province: '',
    paymentOption: 'TRANSFER'
}

const toForm = (supplier) => ({
    name: supplier.name,
    cif: supplier.cif,
    address: supplier.address ?? '',
    postalCode: supplier.postalCode ?? '',
    province: supplier.province ?? '',
    paymentOption: supplier.paymentOption
})

export default function SupplierFormPage() {
    const navigate = useNavigate();
    const { id } = useParams();
    const isEditing = Boolean(id);

    const addSupplier = useSupplierStore((state) => state.addSupplier);
    const editSupplier = useSupplierStore((state) => state.editSupplier);

    const [form, setForm] = useState(initialForm);
    const [error, setError] = useState(null);
    const [saving, setSaving] = useState(false);
    const [loading, setLoading] = useState(isEditing);

    useEffect(() => {
        if (!isEditing) return;

        getSupplier(id)
            .then((supplier) => setForm(toForm(supplier)))
            .catch((err) => {
                console.error(err);
                setError('No se ha podido cargar el proveedor');
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
            cif: form.cif.trim().toUpperCase(),
            address: toNull(form.address),
            postalCode: toNull(form.postalCode),
            province: form.province || null,
            paymentOption: form.paymentOption

        }

        try {
            if (isEditing) {
                await editSupplier(id, payload);
            } else {
                await addSupplier(payload);
            }

            navigate('/suppliers');
        } catch (err) {
            setError(err.response?.data?.message ?? 'No se ha podido guardar el proveedor');
        } finally {
            setSaving(false);
        }
    }

    if (loading) return <p className="text-slate-500">Cargando...</p>;

    return (
        <form onSubmit={handleSubmit} className="mx-auto max-w-3xl space-y-6">
            <h2 className="text-2xl font-semibold">
                {isEditing ? 'Editar proveedor' : 'Nuevo proveedor'}
            </h2>

            <section className={cardClass}>
                <h3 className="mb-4 text-base font-semibold">Datos del proveedor</h3>
                <div className="grid gap-4 sm:grid-cols-2">
                    <Field label="Nombre / Razón social" htmlFor="name">
                        <input id="name" name="name" value={form.name} onChange={handleChange} required className={inputClass}></input>
                    </Field>
                    <Field label="CIF" htmlFor="cif">
                        <input id="cif" name="cif" value={form.cif} onChange={handleChange} required className={inputClass}></input>
                    </Field>
                    <Field label="Dirección" htmlFor="address">
                        <input id="address" name="address" value={form.address} onChange={handleChange} required className={inputClass}></input>
                    </Field>
                    <Field label="Código postal" htmlFor="postalCode">
                        <input id="postalCode" name="postalCode" value={form.postalCode} onChange={handleChange} required className={inputClass} pattern="\d{5}"></input>
                    </Field>
                    <Field label="Provincia" htmlFor="province">
                        <select
                            id="province"
                            name="province"
                            value={form.province}
                            onChange={handleChange}
                            required
                            className={inputClass}
                        >
                            <option value="">-Provincia-</option>
                            {PROVINCES.map((province) => (
                                <option key={province} value={province}>{formatProvince(province)}</option>
                            ))}
                        </select>
                    </Field>
                    <Field
                        label="Forma de pago"
                        htmlFor="paymentOption"
                        >
                            <select 
                                id="paymentOption" 
                                name="paymentOption" 
                                value={form.paymentOption}
                                onChange={handleChange}
                                className={inputClass}
                                >
                                    {PAYMENT_OPTIONS.map((option) => (
                                        <option key={option.value} value={option.value}>
                                            {option.label}
                                        </option>
                                    ))}
                            </select>
                    </Field>

                </div>
            </section>

            {error && <p role="alert" className={alertClass}>{error}</p>}

            <div className="flex justify-end fap-3">
                <Link to="/suppliers" className={buttonSecondary}>Cancelar</Link>
                <button type="submit" disabled={saving} className={buttonPrimary}>
                    {saving ? 'Guardando...' : 'Guardar proveedor'}
                </button>
            </div>
        </form>
    )
}