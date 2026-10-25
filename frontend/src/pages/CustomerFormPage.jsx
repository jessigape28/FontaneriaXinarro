import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getPropertyAdministrators } from '../api/propertyAdministrators';
import { useCustomerStore } from '../store/customerStore';
import { CUSTOMER_TYPES, IVA_TYPES, PROVINCES, formatProvince } from '../constants/enums';
import { getCustomer } from '../api/customers';
import Field from '../components/Field';
import { alertClass, buttonPrimary, buttonSecondary, cardClass, inputClass } from '../components/uiClasses';
import { toNull } from '../utils/text';

const emptyAddress = { alias: '', street: '', town: '', postalCode: '', province: '' };

const initialForm = {
    name: '',
    surname: '',
    dniCif: '',
    email: '',
    phone: '',
    type: 'INDIVIDUAL',
    ivaType: 'GENERAL_21',
    notes: '',
    bankAccount: '',
    propertyAdministratorName: '',
    addresses: [{ ...emptyAddress }],
}

const toForm = (customer) => ({
    name: customer.name,
    surname: customer.surname ?? '',
    dniCif: customer.dniCif,
    email: customer.email ?? '',
    phone: customer.phone ?? '',
    type: customer.type,
    ivaType: customer.ivaType,
    notes: customer.notes ?? '',
    bankAccount: customer.bankAccount ?? '',
    propertyAdministratorName: customer.propertyAdministratorName ? String(customer.propertyAdministratorName) : '',
    addresses: customer.addresses.map((address) => ({
        id: address.id,
        alias: address.aslias ?? '',
        street: address.street,
        town: address.town,
        postalCode: address.postalCode,
        province: address.province
    })),
})

export default function CustomerFormPage() {
    const navigate = useNavigate();
    const { id } = useParams();
    const isEditing = Boolean(id);

    const addCustomer = useCustomerStore((state) => state.addCustomer);
    const editCustomer = useCustomerStore((state) => state.editCustomer);

    const [form, setForm] = useState(initialForm);
    const [administrators, setAdministrators] = useState([]);
    const [error, setError] = useState(null);
    const [saving, setSaving] = useState(false);
    const [loading, setLoading] = useState(isEditing);

    

    useEffect(() => {
        getPropertyAdministrators().then(setAdministrators)
            .catch((err) => console.error(err))
    }, []);

    useEffect(() => {
        if (!isEditing) return;

        getCustomer(id)
            .then((customer) => setForm(toForm(customer)))
            .catch((error) => {
                console.error(error);
                setError('No se ha podido cargar el cliente');
            })
            .finally(() => setLoading(false));
    }, [id, isEditing]);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((prev) => ({ ...prev, [name]: value}));
    }

    const handleAddressChange = (index, event) => {
        const { name, value } = event.target;
        setForm((prev) => ({
            ...prev,
            addresses: prev.addresses.map((address, i) =>
            i === index ? { ...address, [name]: value} : address ),
        }))
    }

    const addAddress = () => 
        setForm((prev) => ({ ...prev, addresses: [...prev.addresses, { ...emptyAddress }]}));

    const removeAddress = (index) =>
        setForm((prev) => ({ ...prev, addresses: prev.addresses.filter((_, i) =>
            i !== index) }));
    
    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);
        setSaving(true);

        const payload = {
            name: form.name.trim(),
            surname: toNull(form.surname),
            dniCif: form.dniCif.trim(),
            email: toNull(form.email),
            phone: toNull(form.phone),
            type: form.type,
            ivaType: form.ivaType,
            notes: toNull(form.notes),
            bankAccount: toNull(form.bankAccount),
            propertyAdministratorId: form.propertyAdministratorId ? Number(form.propertyAdministratorId) : null,
            addresses: form.addresses.map((address) => ({
                id: address.id,
                alias: toNull(address.alias),
                street: address.street.trim(),
                town: address.town.trim(),
                postalCode: address.postalCode.trim(),
                province: address.province
            })),
        }

        try {
            if (isEditing) {
                await editCustomer(id, payload);
            } else {
                await addCustomer(payload);
            }
            
            navigate('/customers');
        } catch (err) {
            setError(err.response?.data?.message ?? 'No se ha podido guardar el cliente');
        } finally {
            setSaving(false);
        }
    }

    if (loading) return <p className="text-slate-500">Cargando...</p>;

return (
    <form onSubmit={handleSubmit} className="mx-auto max-w-3xl space-y-6">
        <h2 className="text-2xl font-semibold">
            {isEditing ? 'Editar cliente' : 'Nuevo cliente'}
        </h2>

        <section className={cardClass}>
            <h3 className="mb-4 text-base font-semibold">Datos del cliente</h3>

            <div className="grid gap-4 sm:grid-cols-2">
                <Field label="Nombre / Razón social" htmlFor="name">
                    <input id="name" name="name" value={form.name} onChange={handleChange} required className={inputClass} />
                </Field>

                <Field label="Apellidos" htmlFor="surname">
                    <input id="surname" name="surname" value={form.surname} onChange={handleChange} className={inputClass} />
                </Field>

                <Field label="DNI / CIF" htmlFor="dniCif">
                    <input id="dniCif" name="dniCif" value={form.dniCif} onChange={handleChange} required className={inputClass} />
                </Field>

                <Field label="Tipo de cliente" htmlFor="type">
                    <select id="type" name="type" value={form.type} onChange={handleChange} className={inputClass}>
                        {CUSTOMER_TYPES.map((option) => (
                            <option key={option.value} value={option.value}>{option.label}</option>
                        ))}
                    </select>
                </Field>

                <Field label="Email" htmlFor="email">
                    <input id="email" name="email" type="email" value={form.email} onChange={handleChange} className={inputClass} />
                </Field>

                <Field label="Teléfono" htmlFor="phone">
                    <input id="phone" name="phone" value={form.phone} onChange={handleChange} className={inputClass} />
                </Field>

                <Field label="IVA" htmlFor="ivaType">
                    <select id="ivaType" name="ivaType" value={form.ivaType} onChange={handleChange} className={inputClass}>
                        {IVA_TYPES.map((option) => (
                            <option key={option.value} value={option.value}>{option.label}</option>
                        ))}
                    </select>
                </Field>

                <Field label="Cuenta bancaria" htmlFor="bankAccount">
                    <input id="bankAccount" name="bankAccount" value={form.bankAccount} onChange={handleChange} className={inputClass} />
                </Field>

                <Field label="Administrador de fincas" htmlFor="propertyAdministratorId" className="sm:col-span-2">
                    <select
                        id="propertyAdministratorId"
                        name="propertyAdministratorId"
                        value={form.propertyAdministratorId}
                        onChange={handleChange}
                        className={inputClass}
                    >
                        <option value="">— Ninguno —</option>
                        {administrators.map((administrator) => (
                            <option key={administrator.id} value={administrator.id}>
                                {administrator.name}
                            </option>
                        ))}
                    </select>
                </Field>

                <Field label="Observaciones" htmlFor="notes" className="sm:col-span-2">
                    <textarea id="notes" name="notes" rows={3} value={form.notes} onChange={handleChange} className={inputClass} />
                </Field>
            </div>
        </section>

        <section className={cardClass}>
            <div className="mb-4 flex items-center justify-between">
                <h3 className="text-base font-semibold">Direcciones</h3>
                <button type="button" onClick={addAddress} className={buttonSecondary}>
                    + Añadir dirección
                </button>
            </div>

            {form.addresses.length === 0 && (
                <p className="text-sm text-slate-500">Este cliente no tiene direcciones.</p>
            )}

            <div className="space-y-4">
                {form.addresses.map((address, index) => (
                    <div key={index} className="rounded-md border border-slate-200 bg-slate-50 p-4">
                        <div className="grid gap-3 sm:grid-cols-6">
                            <input
                                name="alias"
                                aria-label="Alias"
                                placeholder="Alias (ej. Vivienda habitual)"
                                value={address.alias}
                                onChange={(e) => handleAddressChange(index, e)}
                                className={`${inputClass} sm:col-span-6`}
                            />
                            <input
                                name="street"
                                aria-label="Calle y número"
                                placeholder="Calle y número"
                                value={address.street}
                                onChange={(e) => handleAddressChange(index, e)}
                                required
                                className={`${inputClass} sm:col-span-4`}
                            />
                            <input
                                name="town"
                                aria-label="Municipio"
                                placeholder="Municipio"
                                value={address.town}
                                onChange={(e) => handleAddressChange(index, e)}
                                required
                                className={`${inputClass} sm:col-span-2`}
                            />
                            <input
                                name="postalCode"
                                aria-label="Código postal"
                                placeholder="C.P."
                                value={address.postalCode}
                                onChange={(e) => handleAddressChange(index, e)}
                                pattern="\d{5}"
                                required
                                className={`${inputClass} sm:col-span-2`}
                            />
                            <select
                                name="province"
                                aria-label="Provincia"
                                value={address.province}
                                onChange={(e) => handleAddressChange(index, e)}
                                required
                                className={`${inputClass} sm:col-span-4`}
                            >
                                <option value="">— Provincia —</option>
                                {PROVINCES.map((province) => (
                                    <option key={province} value={province}>{formatProvince(province)}</option>
                                ))}
                            </select>
                        </div>

                        <div className="mt-3 text-right">
                            <button
                                type="button"
                                onClick={() => removeAddress(index)}
                                className="text-sm font-medium text-red-600 hover:underline"
                            >
                                Quitar dirección
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </section>

        {error && <p role="alert" className={alertClass}>{error}</p>}

        <div className="flex justify-end gap-3">
            <Link to="/customers" className={buttonSecondary}>Cancelar</Link>
            <button type="submit" disabled={saving} className={buttonPrimary}>
                {saving ? 'Guardando...' : 'Guardar cliente'}
            </button>
        </div>
    </form>
)
}