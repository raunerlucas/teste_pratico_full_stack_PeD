import api from './api'

export default {
  getAll() {
    return api.get('/raw-materials')
  },
  getById(id) {
    return api.get(`/raw-materials/${id}`)
  },
  create(data) {
    return api.post('/raw-materials', data)
  },
  update(id, data) {
    return api.put(`/raw-materials/${id}`, data)
  },
  delete(id) {
    return api.delete(`/raw-materials/${id}`)
  },
}

