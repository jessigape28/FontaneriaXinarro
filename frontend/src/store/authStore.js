import { create } from 'zustand';

export const useAuthStore = create((set) => ({
            user: null,
            checking: true,
            setUser: (user) => set({ user, checking: false }),
            clearUser: () => set({ user: null, checking: false })
        }));
