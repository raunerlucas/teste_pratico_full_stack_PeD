/**
 * Parses an Axios error and returns a structured error info object.
 *
 * @param {Error} err - Axios error object
 * @returns {{ status: number|null, message: string, isDuplicate: boolean, isNotFound: boolean, isConflict: boolean, isDeleteInUse: boolean, isNetwork: boolean }}
 */
export function parseApiError(err) {
  // Network / connection error (server down, CORS, timeout)
  if (!err.response) {
    return {
      status: null,
      message: err.message || 'Network error',
      isDuplicate: false,
      isNotFound: false,
      isConflict: false,
      isDeleteInUse: false,
      isNetwork: true,
    }
  }

  const { status, data } = err.response
  const serverMessage = data?.message || ''
  const msgLower = serverMessage.toLowerCase()

  const isDuplicate = msgLower.includes('already exists') || msgLower.includes('duplicate') || msgLower.includes('unique')
  const isDeleteInUse = msgLower.includes('cannot be deleted') || msgLower.includes('referential') || msgLower.includes('referenced')

  return {
    status,
    message: serverMessage,
    isDuplicate,
    isNotFound: status === 404,
    isConflict: status === 409,
    isDeleteInUse,
    isNetwork: false,
  }
}

/**
 * Maps a parsed API error to the appropriate i18n key for a given domain.
 *
 * @param {ReturnType<typeof parseApiError>} errorInfo - Parsed error info
 * @param {'rawMaterial'|'product'|'production'} domain - The domain context
 * @param {'create'|'update'|'delete'|'load'|'optimize'} action - The action being performed
 * @returns {string} The i18n key to use for the error message
 */
export function getErrorI18nKey(errorInfo, domain, action) {
  // Network errors
  if (errorInfo.isNetwork) {
    return 'common.errorNetwork'
  }

  // Duplicate code — detected by message content (works regardless of HTTP status)
  if (errorInfo.isDuplicate) {
    return `${domain}.errorDuplicateCode`
  }

  // Delete in use — referential integrity (detected by message content)
  if (errorInfo.isDeleteInUse) {
    return `${domain}.errorDeleteInUse`
  }

  // Not found (404)
  if (errorInfo.isNotFound) {
    return `${domain}.errorNotFound`
  }

  // Conflict — generic (409 not caught above)
  if (errorInfo.isConflict) {
    return 'common.errorConflict'
  }

  // Bad request (400)
  if (errorInfo.status === 400) {
    return 'common.errorBadRequest'
  }

  // Server error (500) — generic
  if (errorInfo.status >= 500) {
    return 'common.errorServer'
  }

  // Action-specific fallbacks
  if (action === 'optimize') {
    return `${domain}.errorOptimize`
  }
  if (action === 'load') {
    return `${domain}.errorLoad`
  }

  // Fallback
  return 'common.error'
}

