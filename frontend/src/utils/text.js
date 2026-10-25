export const normalize = (value) => 
    (value ?? '')
        .toString()
        .toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '');

export const toNull = (value) => (value.trim() === '' ? null : value.trim());