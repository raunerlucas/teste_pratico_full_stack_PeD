import api from './api'

export default {
  optimize() {
    return api.get('/production/optimize')
  },
}

