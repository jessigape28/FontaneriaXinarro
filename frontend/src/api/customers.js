import api from './api';

export const getCustomers = () => api.get('/customers').then((response) => response.data);

export const createCustomer = (customer) => 
    api.post('/customers', customer).then((response) => response.data);

export const getCustomer = (id) =>
    api.get(`/customers/${id}`).then((response) =>
    response.data);

export const updateCustomer = (id, customer) =>
    api.put(`/customers/${id}`, customer).then((response) =>
    response.data);

export const deleteCustomer = (id) =>
    api.delete(`/customers/${id}`);