export const CUSTOMER_TYPES = [
    { value: 'INDIVIDUAL', label: 'Particular' },
    { value: 'COMPANY', label: 'Empresa' },
    { value: 'COMMUNITY', label: 'Comunidad' }
];

export const IVA_TYPES = [
    { value: 'EXEMPT', label: 'Exento (0%)' },
    { value: 'REDUCED_10', label: 'Reducido (10%)' },
    { value: 'GENERAL_21', label: 'General (21%)' }
];

export const PROVINCES = [
  'ALAVA', 'ALBACETE', 'ALICANTE', 'ALMERIA', 'ASTURIAS', 'AVILA', 'BADAJOZ',
  'BALEARES', 'BARCELONA', 'BURGOS', 'CACERES', 'CADIZ', 'CANTABRIA', 'CASTELLON',
  'CIUDAD_REAL', 'CORDOBA', 'CORUNA', 'CUENCA', 'GIRONA', 'GRANADA', 'GUADALAJARA',
  'GUIPUZCOA', 'HUELVA', 'HUESCA', 'JAEN', 'LEON', 'LERIDA', 'LUGO', 'MADRID', 'MALAGA',
  'MURCIA', 'NAVARRA', 'OURENSE', 'PALENCIA', 'PONTEVEDRA', 'LA_RIOJA',
  'SALAMANCA', 'SEGOVIA', 'SEVILLA', 'SORIA', 'TARRAGONA', 'TENERIFE',
  'LAS_PALMAS', 'TERUEL', 'TOLEDO', 'VALENCIA', 'VALLADOLID', 'VIZCAYA',
  'ZAMORA', 'ZARAGOZA', 'CEUTA', 'MELILLA',
];

export const formatProvince = (code) => 
    code
        .split('_')
        .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
        .join(' ');

