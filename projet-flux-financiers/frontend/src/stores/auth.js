import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // Getters
  const isAuthenticated = computed(() => !!token.value)
  const currentUser = computed(() => user.value)

  // Actions
  async function login(credentials) {
    try {
      // TODO: Décommenter quand l'endpoint /auth/login sera créé
      // const response = await api.post('/auth/login', credentials)
      // const { token: newToken, user: userData } = response.data
      
      // Simulation temporaire (à retirer)
      const newToken = 'fake-jwt-token-for-testing'
      const userData = { 
        id: 1, 
        username: credentials.username, 
        role: 'GESTIONNAIRE' 
      }
      
      token.value = newToken
      user.value = userData
      
      localStorage.setItem('token', newToken)
      localStorage.setItem('user', JSON.stringify(userData))
      
      return { success: true }
    } catch (error) {
      console.error('Erreur de connexion:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || 'Erreur de connexion' 
      }
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return {
    // State
    token,
    user,
    // Getters
    isAuthenticated,
    currentUser,
    // Actions
    login,
    logout
  }
})
