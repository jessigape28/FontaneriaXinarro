import { create } from "zustand";
import { createSupplier, deleteSupplier, getSuppliers, updateSupplier } from "../api/suppliers";

export const useSupplierStore = create((set) => ({
    suppliers: [],
    loading: false,
    error: null,

    fetchSuppliers: async () => {
        set({ loading: true, error: null });

        try {
            const suppliers = await getSuppliers();
            set({ suppliers, loading: false });
        } catch (err) {
            console.error('Error fetching suppliers', err);
            set({ error: 'No se han podido cargar los proveedores', loading: false });
        }
    },

    addSupplier: async (supplier) => {
        const created = await createSupplier(supplier);
        set((state) => ({ suppliers: [...state.suppliers, created ]}));

        return created;
    },

    editSupplier: async (id, supplier) => {
        const updated = await updateSupplier(id, supplier);
        set((state) => ({
            suppliers: state.suppliers.map((s) => (
                s.id === updated.id ? updated : s
            )),
        }))

        return updated;
    },

    removeSupplier: async (id) => {
        await deleteSupplier(id);
        set((state) => ({ suppliers: state.suppliers.filter((s) =>
        s.id !== id )}))
    }
}));