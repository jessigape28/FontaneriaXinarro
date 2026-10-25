import api from './api';

export const getPropertyAdministrators = () => 
    api.get('/propertyAdministrators').then((response) => response.data);