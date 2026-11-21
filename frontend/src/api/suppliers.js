import api from "./api";

export const getSuppliers = () => api.get('/suppliers').then((response) => response.data);

export const createSupplier = (supplier) =>
    api.post('/suppliers', supplier).then((response) => response.data);

export const getSupplier = (id) => 
    api.get(`/suppliers/${id}`).then((response) => response.data);

export const updateSupplier = (id, supplier) =>
    api.put(`/suppliers/${id}`, supplier).then((response) => response.data);

export const deleteSupplier = (id) => 
    api.delete(`/suppliers/${id}`);