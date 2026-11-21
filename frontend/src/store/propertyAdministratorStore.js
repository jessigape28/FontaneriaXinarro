import { create } from 'zustand';
import {
    createPropertyAdministrator,
    deletePropertyAdministrator,
    getPropertyAdministrators,
    updatePropertyAdministrator,
} from '../api/propertyAdministrators';

export const usePropertyAdministratorStore = create((set) => ({
    propertyAdministrators: [],
    loading: false,
    error: null,

    fetchPropertyAdministrators: async () => {
        set({ loading: true, error: null });

        try {
            const propertyAdministrators = await getPropertyAdministrators();
            set({ propertyAdministrators, loading: false });
        } catch (err) {
            console.error('Error fetching property administrators', err);
            set({ error: 'No se han podido cargar los administradores de fincas', loading: false });
        }
    },

    addPropertyAdministrator: async (propertyAdministrator) => {
        const created = await createPropertyAdministrator(propertyAdministrator);
        set((state) => ({ propertyAdministrators: [...state.propertyAdministrators, created] }));

        return created;
    },

    editPropertyAdministrator: async (id, propertyAdministrator) => {
        const updated = await updatePropertyAdministrator(id, propertyAdministrator);
        set((state) => ({
            propertyAdministrators: state.propertyAdministrators.map((p) =>
                p.id === updated.id ? updated : p
            ),
        }));

        return updated;
    },

    removePropertyAdministrator: async (id) => {
        await deletePropertyAdministrator(id);
        set((state) => ({
            propertyAdministrators: state.propertyAdministrators.filter((p) => p.id !== id),
        }));
    },
}));
