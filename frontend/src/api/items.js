import api from './api';

export const getItems = () => api.get('/items').then((response) => response.data);

export const createItem = (item) => 
    api.post('/items', item).then((response) => response.data);

export const getItem = (id) => 
    api.get(`/items/${id}`).then((response) => response.data);

export const updateItem = (id, item) =>
    api.put(`/items/${id}`, item).then((response) => response.data);

export const deleteItem = (id) =>
    api.delete(`/items/${id}`);