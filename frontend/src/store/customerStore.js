import { create } from 'zustand';
import { createCustomer, deleteCustomer, getCustomers, updateCustomer } from '../api/customers';

export const useCustomerStore = create((set) => ({
    customers: [],
    loading: false,
    error: null,

    fetchCustomers: async () => {
        set({ loading: true, error: null});

        try {
            const customers = await getCustomers();
            set({ customers, loading: false });
        } catch (err){
            console.error('Error fetching customers', err);
            set({ error: 'No se pudieron cargar los clientes', loading: false});
        }
    },

    addCustomer: async (customer) => {
        const created = await createCustomer(customer);
        set((state) => ({ customers: [...state.customers, created ]}));

        return created;
    },

    editCustomer: async (id, customer) => {
        const updated = await updateCustomer(id, customer);
        set((state) => ({
            customers: state.customers.map((c) => (
                c.id === updated.id ? updated : c
            )),
        }))

        return updated;
    },

    removeCustomer: async (id) => {
        await deleteCustomer(id);
        set((state) => ({ customers: state.customers.filter((c) =>
        c.id !== id )}))
    }
}));