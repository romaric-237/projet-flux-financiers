<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>🏦</h1>
        <h2>Gestion des Flux Financiers</h2>
        <p class="text-muted">ASBL Terra Sana</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">Nom d'utilisateur</label>
          <input
            id="username"
            v-model="credentials.username"
            type="text"
            placeholder="admin"
            :class="{ error: errors.username }"
            required
          />
          <p v-if="errors.username" class="error-message">{{ errors.username }}</p>
        </div>
        
        <div class="form-group">
          <label for="password">Mot de passe</label>
          <input
            id="password"
            v-model="credentials.password"
            type="password"
            placeholder="••••••••"
            :class="{ error: errors.password }"
            required
          />
          <p v-if="errors.password" class="error-message">{{ errors.password }}</p>
        </div>
        
        <button 
          type="submit" 
          class="btn btn-primary btn-lg" 
          :disabled="loading"
          style="width: 100%"
        >
          {{ loading ? 'Connexion...' : 'Se connecter' }}
        </button>
        
        <p v-if="errorMessage" class="error-message text-center mt-2">
          {{ errorMessage }}
        </p>
      </form>
      
      <div class="login-footer">
        <p class="text-muted">
          <small>Connexion sécurisée avec JWT</small>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

const credentials = ref({
  username: '',
  password: ''
})

const errors = ref({
  username: '',
  password: ''
})

const errorMessage = ref('')
const loading = ref(false)

const validateForm = () => {
  errors.value = { username: '', password: '' }
  let isValid = true
  
  if (!credentials.value.username) {
    errors.value.username = 'Le nom d\'utilisateur est obligatoire'
    isValid = false
  }
  
  if (!credentials.value.password) {
    errors.value.password = 'Le mot de passe est obligatoire'
    isValid = false
  }
  
  return isValid
}

const handleLogin = async () => {
  if (!validateForm()) return
  
  loading.value = true
  errorMessage.value = ''
  
  try {
    const result = await authStore.login(credentials.value)
    
    if (result.success) {
      toast.success('Connexion réussie !')
      router.push('/')
    } else {
      errorMessage.value = result.message || 'Identifiants incorrects'
      toast.error(result.message || 'Erreur de connexion')
    }
  } catch (error) {
    errorMessage.value = 'Une erreur est survenue'
    toast.error('Erreur de connexion')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  padding: 2rem;
}

.login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 420px;
  padding: 2.5rem;
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.login-header h1 {
  font-size: 3rem;
  margin-bottom: 0.5rem;
}

.login-header h2 {
  color: var(--color-primary);
  font-size: 1.5rem;
  margin-bottom: 0.25rem;
}

.login-form {
  margin-bottom: 1.5rem;
}

.login-footer {
  text-align: center;
  padding-top: 1rem;
  border-top: 1px solid var(--color-gray-200);
}
</style>
