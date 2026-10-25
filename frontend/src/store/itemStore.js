import { create } from 'zustand';
import { createItem, deleteItem, getItems, updateItem } from '../api/items';

export const useItemStore = create((set) => ({
    items: [],
    loading: false,
    error: null,

    fetchItems: async () => {
        set({ loading: true, error: null});

        try {
            const items = await getItems();
            set({ items, loading: false });
        } catch (err) {
            console.error('Error fetching items', err);
            set({ error: 'No se pudieron cargar los artículos', loading: false });
        }
    },

    addItem: async (item) => {
        const created = await createItem(item);
        set((state) => ({ items: [...state.items, created ]}));

        return created;
    },

    editItem: async (id, item) => {
        const updated = await updateItem(id, item);
        set((state) => ({
            items: state.items.map((i) => (
                i.id === updated.id ? updated : i
            )),
        }))

        return updated;
    },

    removeItem: async (id) => {
        await deleteItem(id);
        set((state) => ({ items: state.items.filter((i) => 
            i.id !== id )}))
    }
}))