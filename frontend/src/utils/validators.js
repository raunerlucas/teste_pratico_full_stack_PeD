/**
 * Validate that a value is not empty.
 */
export function required(value) {
  if (value === null || value === undefined || value === '') return false
  if (typeof value === 'string' && value.trim() === '') return false
  return true
}

/**
 * Validate that a numeric value is >= 0.
 */
export function nonNegative(value) {
  return value != null && Number(value) >= 0
}

/**
 * Validate that a numeric value is > 0.
 */
export function positive(value) {
  return value != null && Number(value) > 0
}

/**
 * Validate raw material form data. Returns an object with field errors.
 */
export function validateRawMaterial(data) {
  const errors = {}
  if (!required(data.code)) errors.code = 'required'
  if (!required(data.name)) errors.name = 'required'
  if (!nonNegative(data.stockQuantity)) errors.stockQuantity = 'invalidValue'
  return errors
}

/**
 * Validate product form data. Returns an object with field errors.
 */
export function validateProduct(data) {
  const errors = {}
  if (!required(data.code)) errors.code = 'required'
  if (!required(data.name)) errors.name = 'required'
  if (!positive(data.price)) errors.price = 'invalidValue'
  return errors
}

/**
 * Check if an errors object has any errors.
 */
export function hasErrors(errors) {
  return Object.keys(errors).length > 0
}

