import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/clients',
      name: 'clients',
      component: () => import('@/views/ClientsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/employes',
      name: 'employes',
      component: () => import('@/views/EmployesView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/charges',
      name: 'charges',
      component: () => import('@/views/ChargesView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/versements',
      name: 'versements',
      component: () => import('@/views/VersementsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/paiements-employe',
      name: 'paiements-employe',
      component: () => import('@/views/PaiementsEmployeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/paiements-charge',
      name: 'paiements-charge',
      component: () => import('@/views/PaiementsChargeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/historique',
      name: 'historique',
      component: () => import('@/views/HistoriqueView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/export',
      name: 'export',
      component: () => import('@/views/ExportView.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// Navigation guard pour protéger les routes
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login' })
  } else if (to.name === 'login' && authStore.isAuthenticated) {
    next({ name: 'dashboard' })
  } else {
    next()
  }
})

export default router
