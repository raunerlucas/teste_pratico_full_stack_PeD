/**
 * Format a number as Brazilian Real currency (BRL).
 */
export function formatCurrency(value) {
  if (value == null) return 'R$ 0,00'
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value)
}

/**
 * Format a number with decimal places.
 */
export function formatNumber(value, decimals = 2) {
  if (value == null) return '0'
  return new Intl.NumberFormat('pt-BR', {
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals,
  }).format(value)
}

/**
 * Format a date string to local date format.
 */
export function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Intl.DateTimeFormat('pt-BR').format(new Date(dateStr))
}

