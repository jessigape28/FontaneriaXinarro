import api from './api';

export const getPropertyAdministrators = () =>
    api.get('/property-administrators').then((response) => response.data);

export const getPropertyAdministrator = (id) =>
    api.get(`/property-administrators/${id}`).then((response) => response.data);

export const createPropertyAdministrator = (propertyAdministrator) =>
    api.post('/property-administrators', propertyAdministrator).then((response) => response.data);

export const updatePropertyAdministrator = (id, propertyAdministrator) =>
    api.put(`/property-administrators/${id}`, propertyAdministrator).then((response) => response.data);

export const deletePropertyAdministrator = (id) =>
    api.delete(`/property-administrators/${id}`);
