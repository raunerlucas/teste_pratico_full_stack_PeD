import {createRouter, createWebHistory} from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
  },
  {
    path: '/raw-materials',
    name: 'raw-materials',
    component: () => import('@/views/RawMaterialListView.vue'),
  },
  {
    path: '/raw-materials/new',
    name: 'raw-material-new',
    component: () => import('@/views/RawMaterialFormView.vue'),
  },
  {
    path: '/raw-materials/:id/edit',
    name: 'raw-material-edit',
    component: () => import('@/views/RawMaterialFormView.vue'),
  },
  {
    path: '/products',
    name: 'products',
    component: () => import('@/views/ProductListView.vue'),
  },
  {
    path: '/products/new',
    name: 'product-new',
    component: () => import('@/views/ProductFormView.vue'),
  },
  {
    path: '/products/:id/edit',
    name: 'product-edit',
    component: () => import('@/views/ProductFormView.vue'),
  },
  {
    path: '/production/optimize',
    name: 'production-optimize',
    component: () => import('@/views/ProductionOptimizerView.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  linkActiveClass: 'active',
})

export default router

