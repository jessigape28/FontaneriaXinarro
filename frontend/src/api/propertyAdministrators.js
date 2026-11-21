import api from './api';

export const getPropertyAdministrators = () => 
    api.get('/property-administrators').then((response) => response.data);