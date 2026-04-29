import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user  = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)
  const currentUser     = computed(() => user.value)
  const isAdmin         = computed(() => user.value?.role === 'ADMIN')

  async function login(credentials) {
    try {
      const response = await api.post('/auth/login', credentials)
      const { id, token: newToken, username, nom, prenom, role } = response.data

      const userData = { id, username, nom, prenom, role }

      token.value = newToken
      user.value  = userData

      localStorage.setItem('token', newToken)
      localStorage.setItem('user', JSON.stringify(userData))

      return { success: true }
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Identifiants incorrects'
      }
    }
  }

  function logout() {
    token.value = null
    user.value  = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, isAuthenticated, currentUser, isAdmin, login, logout }
})