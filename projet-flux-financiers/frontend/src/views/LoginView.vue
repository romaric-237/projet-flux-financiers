<template>
  <div class="login-page">

    <!-- Panneau gauche — identité visuelle -->
    <div class="login-left">
      <div class="login-brand">
        <div class="brand-icon">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="48" height="48" rx="14" fill="rgba(255,255,255,0.15)"/>
            <path d="M14 36V22l10-7 10 7v14H30v-8h-6v8H14Z" fill="white" opacity="0.95"/>
            <rect x="21" y="28" width="6" height="8" rx="1" fill="rgba(255,255,255,0.5)"/>
            <circle cx="34" cy="14" r="6" fill="rgba(255,255,255,0.2)" stroke="white" stroke-width="1.5"/>
            <path d="M34 11v6M31 14h6" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </div>
        <h1 class="brand-name">Flux Financiers</h1>
        <p class="brand-org">ASBL Terra Sana</p>
      </div>

      <div class="login-features">
        <div class="feature-item">
          <span class="feature-icon">📊</span>
          <div>
            <p class="feature-title">Tableau de bord</p>
            <p class="feature-desc">Vue complète de vos flux financiers</p>
          </div>
        </div>
        <div class="feature-item">
          <span class="feature-icon">💼</span>
          <div>
            <p class="feature-title">Gestion intégrée</p>
            <p class="feature-desc">Clients, employés, charges en un seul endroit</p>
          </div>
        </div>
        <div class="feature-item">
          <span class="feature-icon">📥</span>
          <div>
            <p class="feature-title">Export Excel & CSV</p>
            <p class="feature-desc">Exportez vos données en quelques clics</p>
          </div>
        </div>
      </div>

      <p class="login-version"></p>
    </div>

    <!-- Panneau droit — formulaire -->
    <div class="login-right">
      <div class="login-form-wrapper">
        <div class="login-form-header">
          <h2>Bienvenue</h2>
          <p class="text-muted">Connectez-vous à votre espace de gestion</p>
        </div>

        <form @submit.prevent="handleLogin" class="login-form" novalidate>

          <div class="form-group">
            <label for="username">Nom d'utilisateur</label>
            <div class="input-icon-wrap">
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              <input
                id="username"
                v-model="credentials.username"
                type="text"
                placeholder="admin"
                :class="{ error: errors.username }"
                autocomplete="username"
              />
            </div>
            <p v-if="errors.username" class="error-message">{{ errors.username }}</p>
          </div>

          <div class="form-group">
            <label for="password">Mot de passe</label>
            <div class="input-icon-wrap">
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
              <input
                id="password"
                v-model="credentials.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="••••••••"
                :class="{ error: errors.password }"
                autocomplete="current-password"
              />
              <button type="button" class="toggle-password" @click="showPassword = !showPassword" tabindex="-1">
                <svg v-if="!showPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                  <circle cx="12" cy="12" r="3"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                  <line x1="1" y1="1" x2="23" y2="23"/>
                </svg>
              </button>
            </div>
            <p v-if="errors.password" class="error-message">{{ errors.password }}</p>
          </div>

          <div v-if="errorMessage" class="alert-error">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            {{ errorMessage }}
          </div>

          <button
            type="submit"
            class="btn btn-primary btn-login"
            :disabled="loading"
          >
            <svg v-if="!loading" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
              <polyline points="10 17 15 12 10 7"/>
              <line x1="15" y1="12" x2="3" y2="12"/>
            </svg>
            <span class="btn-spinner" v-if="loading"></span>
            {{ loading ? 'Connexion...' : 'Se connecter' }}
          </button>

        </form>

        <p class="secure-note">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="11" width="18" height="11" rx="2"/>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
          </svg>
          Connexion sécurisée avec JWT
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

const router      = useRouter()
const authStore   = useAuthStore()
const toast       = useToast()

const credentials   = ref({ username: '', password: '' })
const errors        = ref({ username: '', password: '' })
const errorMessage  = ref('')
const loading       = ref(false)
const showPassword  = ref(false)

function validateForm() {
  errors.value = { username: '', password: '' }
  let valid = true
  if (!credentials.value.username) { errors.value.username = 'Le nom d\'utilisateur est obligatoire'; valid = false }
  if (!credentials.value.password) { errors.value.password = 'Le mot de passe est obligatoire'; valid = false }
  return valid
}

async function handleLogin() {
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
    }
  } catch {
    errorMessage.value = 'Une erreur est survenue. Réessayez.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ── Layout split-screen ── */
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 1fr;
}

/* ── Panneau gauche ── */
.login-left {
  background: linear-gradient(150deg, var(--color-primary) 0%, var(--color-primary-hover) 100%);
  padding: 3rem 3.5rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  top: -80px;
  right: -80px;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
}

.login-left::after {
  content: '';
  position: absolute;
  bottom: -60px;
  left: -60px;
  width: 220px;
  height: 220px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.04);
}

/* Brand */
.login-brand { position: relative; z-index: 1; }

.brand-icon {
  width: 64px;
  height: 64px;
  margin-bottom: 1.25rem;
}

.brand-icon svg { width: 100%; height: 100%; }

.brand-name {
  font-size: 2rem;
  font-weight: 800;
  color: white;
  margin-bottom: 0.3rem;
  letter-spacing: -0.02em;
}

.brand-org {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.65);
  font-weight: 400;
}

/* Features */
.login-features {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  position: relative;
  z-index: 1;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}

.feature-icon {
  font-size: 1.4rem;
  flex-shrink: 0;
  margin-top: 0.1rem;
}

.feature-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: white;
  margin-bottom: 0.15rem;
}

.feature-desc {
  font-size: 0.82rem;
  color: rgba(255, 255, 255, 0.6);
}

.login-version {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.4);
  position: relative;
  z-index: 1;
}

/* ── Panneau droit ── */
.login-right {
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem 2rem;
}

.login-form-wrapper {
  width: 100%;
  max-width: 380px;
}

.login-form-header {
  margin-bottom: 2rem;
}

.login-form-header h2 {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-gray-900);
  margin-bottom: 0.4rem;
}

.login-form-header p {
  font-size: 0.9rem;
  color: var(--color-gray-500);
}

/* Input avec icône */
.input-icon-wrap {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 0.85rem;
  top: 50%;
  transform: translateY(-50%);
  width: 17px;
  height: 17px;
  color: var(--color-gray-400);
  pointer-events: none;
}

.input-icon-wrap input {
  padding-left: 2.5rem;
  padding-right: 2.75rem;
}

.toggle-password {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px;
  color: var(--color-gray-400);
  display: flex;
  align-items: center;
  border-radius: 4px;
  transition: color 0.15s;
}

.toggle-password:hover { color: var(--color-gray-700); }

.toggle-password svg { width: 16px; height: 16px; }

/* Alerte d'erreur */
.alert-error {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  background: var(--color-danger-light);
  color: #991b1b;
  border: 1px solid #fca5a5;
  border-radius: var(--border-radius);
  padding: 0.65rem 0.9rem;
  font-size: 0.85rem;
  font-weight: 500;
  margin-bottom: 1rem;
}

.alert-error svg { width: 16px; height: 16px; flex-shrink: 0; }

/* Bouton de connexion */
.btn-login {
  width: 100%;
  padding: 0.75rem;
  font-size: 0.95rem;
  margin-top: 0.5rem;
  gap: 0.5rem;
}

.btn-login svg { width: 18px; height: 18px; }

.btn-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  flex-shrink: 0;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* Note sécurité */
.secure-note {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  margin-top: 1.5rem;
  font-size: 0.78rem;
  color: var(--color-gray-400);
}

.secure-note svg { width: 13px; height: 13px; }

/* ── Responsive ── */
@media (max-width: 768px) {
  .login-page { grid-template-columns: 1fr; }

  .login-left {
    padding: 2.5rem 2rem;
    min-height: auto;
  }

  .login-features { display: none; }
  .brand-name { font-size: 1.5rem; }

  .login-right { padding: 2rem 1.5rem; }
}
</style>
