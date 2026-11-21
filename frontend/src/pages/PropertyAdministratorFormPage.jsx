import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { getPropertyAdministrator } from "../api/propertyAdministrators";
import { usePropertyAdministratorStore } from "../store/propertyAdministratorStore";
import Field from "../components/Field";
import { alertClass, buttonPrimary, buttonSecondary, cardClass, inputClass } from "../components/uiClasses";
import { toNull } from "../utils/text";

const initialForm = {
    name: '',
    email: '',
    phone: '',
};

const toForm = (propertyAdministrator) => ({
    name: propertyAdministrator.name,
    email: propertyAdministrator.email,
    phone: propertyAdministrator.phone ?? '',
});

export default function PropertyAdministratorFormPage() {
    const navigate = useNavigate();
    const { id } = useParams();
    const isEditing = Boolean(id);

    const addPropertyAdministrator = usePropertyAdministratorStore((state) => state.addPropertyAdministrator);
    const editPropertyAdministrator = usePropertyAdministratorStore((state) => state.editPropertyAdministrator);

    const [form, setForm] = useState(initialForm);
    const [error, setError] = useState(null);
    const [saving, setSaving] = useState(false);
    const [loading, setLoading] = useState(isEditing);

    useEffect(() => {
        if (!isEditing) return;

        getPropertyAdministrator(id)
            .then((propertyAdministrator) => setForm(toForm(propertyAdministrator)))
            .catch((err) => {
                console.error(err);
                setError('No se ha podido cargar el administrador de fincas');
            })
            .finally(() => setLoading(false));
    }, [id, isEditing]);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);
        setSaving(true);

        const payload = {
            name: form.name.trim(),
            email: form.email.trim(),
            phone: toNull(form.phone),
        };

        try {
            if (isEditing) {
                await editPropertyAdministrator(id, payload);
            } else {
                await addPropertyAdministrator(payload);
            }

            navigate('/property-administrators');
        } catch (err) {
            setError(err.response?.data?.message ?? 'No se ha podido guardar el administrador de fincas');
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <p className="text-slate-500">Cargando...</p>;

    return (
        <form onSubmit={handleSubmit} className="mx-auto max-w-xl space-y-6">
            <h2 className="text-2xl font-semibold">
                {isEditing ? 'Editar administrador de fincas' : 'Nuevo administrador de fincas'}
            </h2>

            <section className={cardClass}>
                <h3 className="mb-4 text-base font-semibold">Datos del administrador</h3>
                <div className="grid gap-4">
                    <Field label="Nombre / Razón social" htmlFor="name">
                        <input
                            id="name"
                            name="name"
                            value={form.name}
                            onChange={handleChange}
                            required
                            className={inputClass}
                        />
                    </Field>

                    <Field label="Email" htmlFor="email">
                        <input
                            id="email"
                            name="email"
                            type="email"
                            value={form.email}
                            onChange={handleChange}
                            required
                            className={inputClass}
                        />
                    </Field>

                    <Field label="Teléfono" htmlFor="phone">
                        <input
                            id="phone"
                            name="phone"
                            value={form.phone}
                            onChange={handleChange}
                            className={inputClass}
                        />
                    </Field>
                </div>
            </section>

            {error && <p role="alert" className={alertClass}>{error}</p>}

            <div className="flex justify-end gap-3">
                <Link to="/property-administrators" className={buttonSecondary}>Cancelar</Link>
                <button type="submit" disabled={saving} className={buttonPrimary}>
                    {saving ? 'Guardando...' : 'Guardar administrador'}
                </button>
            </div>
        </form>
    );
}
