const currencyFormatter = new Intl.NumberFormat('es-ES', {
    style: 'currency',
    currency: 'EUR',
});

export const formatCurrency = (value) => currencyFormatter.format(value);