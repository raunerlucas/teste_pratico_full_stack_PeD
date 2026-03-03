import {describe, expect, it} from 'vitest'
import {getErrorI18nKey, parseApiError} from '@/utils/errorHandler'

describe('parseApiError', () => {
  it('detects network error when no response', () => {
    const err = { message: 'Network Error' }
    const result = parseApiError(err)

    expect(result.isNetwork).toBe(true)
    expect(result.status).toBeNull()
    expect(result.isDuplicate).toBe(false)
  })

  it('detects duplicate code from 409 with "already exists" message', () => {
    const err = {
      response: {
        status: 409,
        data: { message: "Raw material with code 'MP001' already exists." },
      },
    }
    const result = parseApiError(err)

    expect(result.isDuplicate).toBe(true)
    expect(result.isConflict).toBe(true)
    expect(result.status).toBe(409)
  })

  it('detects duplicate from 500 when message contains "already exists"', () => {
    const err = {
      response: {
        status: 500,
        data: { message: "Record already exists in database" },
      },
    }
    const result = parseApiError(err)

    expect(result.isDuplicate).toBe(true)
    expect(result.status).toBe(500)
  })

  it('detects delete in use from message containing "cannot be deleted"', () => {
    const err = {
      response: {
        status: 409,
        data: { message: 'This raw material cannot be deleted because it is used in product compositions.' },
      },
    }
    const result = parseApiError(err)

    expect(result.isDeleteInUse).toBe(true)
    expect(result.isConflict).toBe(true)
  })

  it('detects 404 not found', () => {
    const err = {
      response: {
        status: 404,
        data: { message: 'Raw Material not found with id: 99' },
      },
    }
    const result = parseApiError(err)

    expect(result.isNotFound).toBe(true)
    expect(result.status).toBe(404)
  })

  it('handles empty response data gracefully', () => {
    const err = {
      response: { status: 500, data: {} },
    }
    const result = parseApiError(err)

    expect(result.isDuplicate).toBe(false)
    expect(result.isDeleteInUse).toBe(false)
    expect(result.message).toBe('')
  })
})

describe('getErrorI18nKey', () => {
  it('returns network error key for network errors', () => {
    const errorInfo = { isNetwork: true, isDuplicate: false, isDeleteInUse: false, isNotFound: false, isConflict: false, status: null }
    expect(getErrorI18nKey(errorInfo, 'rawMaterial', 'create')).toBe('common.errorNetwork')
  })

  it('returns domain-specific duplicate key', () => {
    const errorInfo = { isNetwork: false, isDuplicate: true, isDeleteInUse: false, isNotFound: false, isConflict: true, status: 409 }
    expect(getErrorI18nKey(errorInfo, 'rawMaterial', 'create')).toBe('rawMaterial.errorDuplicateCode')
    expect(getErrorI18nKey(errorInfo, 'product', 'create')).toBe('product.errorDuplicateCode')
  })

  it('returns domain-specific delete in use key', () => {
    const errorInfo = { isNetwork: false, isDuplicate: false, isDeleteInUse: true, isNotFound: false, isConflict: true, status: 409 }
    expect(getErrorI18nKey(errorInfo, 'rawMaterial', 'delete')).toBe('rawMaterial.errorDeleteInUse')
  })

  it('returns domain-specific not found key', () => {
    const errorInfo = { isNetwork: false, isDuplicate: false, isDeleteInUse: false, isNotFound: true, isConflict: false, status: 404 }
    expect(getErrorI18nKey(errorInfo, 'product', 'load')).toBe('product.errorNotFound')
  })

  it('returns server error key for 500 errors', () => {
    const errorInfo = { isNetwork: false, isDuplicate: false, isDeleteInUse: false, isNotFound: false, isConflict: false, status: 500 }
    expect(getErrorI18nKey(errorInfo, 'rawMaterial', 'create')).toBe('common.errorServer')
  })

  it('returns bad request key for 400 errors', () => {
    const errorInfo = { isNetwork: false, isDuplicate: false, isDeleteInUse: false, isNotFound: false, isConflict: false, status: 400 }
    expect(getErrorI18nKey(errorInfo, 'rawMaterial', 'create')).toBe('common.errorBadRequest')
  })

  it('returns optimize fallback for production optimize action', () => {
    const errorInfo = { isNetwork: false, isDuplicate: false, isDeleteInUse: false, isNotFound: false, isConflict: false, status: null }
    expect(getErrorI18nKey(errorInfo, 'production', 'optimize')).toBe('production.errorOptimize')
  })

  it('returns load fallback for load action', () => {
    const errorInfo = { isNetwork: false, isDuplicate: false, isDeleteInUse: false, isNotFound: false, isConflict: false, status: null }
    expect(getErrorI18nKey(errorInfo, 'rawMaterial', 'load')).toBe('rawMaterial.errorLoad')
  })

  it('returns generic fallback for unknown errors', () => {
    const errorInfo = { isNetwork: false, isDuplicate: false, isDeleteInUse: false, isNotFound: false, isConflict: false, status: null }
    expect(getErrorI18nKey(errorInfo, 'rawMaterial', 'create')).toBe('common.error')
  })
})

