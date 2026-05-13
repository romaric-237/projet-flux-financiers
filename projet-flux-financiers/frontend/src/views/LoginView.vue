<template>
  <div class="login-page">

    <!-- ══════════════════════════════════════
         PANNEAU GAUCHE — branding Terra Sana
    ══════════════════════════════════════ -->
    <div class="login-left">

      <!-- Cercles décoratifs -->
      <div class="deco deco-1"></div>
      <div class="deco deco-2"></div>
      <div class="deco deco-3"></div>

      <div class="left-inner">

        <!-- Logo + identité -->
        <div class="brand">
          <div class="brand-logo">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="48" height="48" rx="14" fill="rgba(255,255,255,0.15)"/>
              <path d="M12 36V20l12-8 12 8v16H30v-9h-6v9H12Z" fill="white" opacity="0.95"/>
              <rect x="22" y="27" width="4" height="9" rx="1" fill="rgba(255,255,255,0.45)"/>
              <circle cx="35" cy="13" r="5" fill="rgba(255,255,255,0.18)" stroke="white" stroke-width="1.5"/>
              <path d="M35 10.5v5M32.5 13h5" stroke="white" stroke-width="1.5" stroke-linecap="round"/>
            </svg>
          </div>
          <div>
            <h1 class="brand-name">Flux Financiers</h1>
            <p class="brand-org">ASBL Terra Sana</p>
          </div>
        </div>

        <!-- Tagline -->
        <div class="brand-tagline">
          <p>Gérez vos finances associatives<br/>simplement et en toute sécurité.</p>
        </div>

        <!-- Features -->
        <ul class="features">
          <li class="feature-item">
            <div class="feature-icon-wrap">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="2" y="3" width="20" height="14" rx="2"/>
                <path d="M8 21h8M12 17v4"/>
              </svg>
            </div>
            <div>
              <p class="feature-title">Tableau de bord</p>
              <p class="feature-desc">KPIs, graphiques et alertes budgétaires en temps réel</p>
            </div>
          </li>
          <li class="feature-item">
            <div class="feature-icon-wrap">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
              </svg>
            </div>
            <div>
              <p class="feature-title">Gestion intégrée</p>
              <p class="feature-desc">Clients, employés et charges en un seul endroit</p>
            </div>
          </li>
          <li class="feature-item">
            <div class="feature-icon-wrap">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="7 10 12 15 17 10"/>
                <line x1="12" y1="15" x2="12" y2="3"/>
              </svg>
            </div>
            <div>
              <p class="feature-title">Export Excel & CSV</p>
              <p class="feature-desc">Exportez vos données en quelques clics</p>
            </div>
          </li>
        </ul>

        <!-- Copyright -->
        <p class="left-footer">© 2025 Terra Sana · Tous droits réservés</p>

      </div>
    </div>

    <!-- ══════════════════════════════════════
         PANNEAU DROIT — formulaire
    ══════════════════════════════════════ -->
    <div class="login-right">
      <div class="form-wrapper">

        <!-- En-tête formulaire -->
        <div class="form-header">
          <div class="form-logo-sm">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
          </div>
          <h2>Connexion</h2>
          <p class="form-subtitle">Accédez à votre espace de gestion</p>
        </div>

        <!-- Formulaire -->
        <form @submit.prevent="handleLogin" novalidate>

          <!-- Username -->
          <div class="form-group">
            <label for="username">Nom d'utilisateur</label>
            <div class="input-wrap">
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              <input
                id="username"
                v-model="credentials.username"
                type="text"
                placeholder="Entrez votre identifiant"
                :class="{ error: errors.username }"
                autocomplete="username"
              />
            </div>
            <p v-if="errors.username" class="error-message">{{ errors.username }}</p>
          </div>

          <!-- Password -->
          <div class="form-group">
            <label for="password">Mot de passe</label>
            <div class="input-wrap">
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2"/>
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
              <button type="button" class="toggle-eye" @click="showPassword = !showPassword" tabindex="-1">
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

          <!-- Alerte erreur globale -->
          <div v-if="errorMessage" class="alert-error">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            {{ errorMessage }}
          </div>

          <!-- Bouton cyan -->
          <button type="submit" class="btn-login" :disabled="loading">
            <span v-if="loading" class="btn-spinner"></span>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
              <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
              <polyline points="10 17 15 12 10 7"/>
              <line x1="15" y1="12" x2="3" y2="12"/>
            </svg>
            {{ loading ? 'Connexion en cours…' : 'Se connecter' }}
          </button>

        </form>

        <!-- Badge sécurité JWT -->
        <div class="jwt-badge">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
          <span>Connexion chiffrée · Authentification JWT</span>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const router     = useRouter()
const authStore  = useAuthStore()
const toast      = useToast()

const credentials  = ref({ username: '', password: '' })
const errors       = ref({ username: '', password: '' })
const errorMessage = ref('')
const loading      = ref(false)
const showPassword = ref(false)

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
/* ══════════════════════════════════════
   LAYOUT
══════════════════════════════════════ */
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 1fr;
}

/* ══════════════════════════════════════
   PANNEAU GAUCHE
══════════════════════════════════════ */
.login-left {
  position: relative;
  overflow: hidden;
  background: linear-gradient(145deg, #059669 0%, #065f46 60%, #022c22 100%);
  display: flex;
  align-items: stretch;
}

/* Cercles décoratifs */
.deco {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
}
.deco-1 { width: 420px; height: 420px; top: -120px; right: -120px; }
.deco-2 { width: 240px; height: 240px; bottom: 40px; left: -80px; background: rgba(255,255,255,0.04); }
.deco-3 { width: 120px; height: 120px; top: 50%; left: 55%; background: rgba(255,255,255,0.05); }

.left-inner {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 3rem 3.5rem;
  width: 100%;
}

/* Brand */
.brand {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.brand-logo {
  width: 56px;
  height: 56px;
  flex-shrink: 0;
}
.brand-logo svg { width: 100%; height: 100%; }
.brand-name {
  font-size: 1.6rem;
  font-weight: 800;
  color: white;
  letter-spacing: -0.02em;
  margin-bottom: 0.1rem;
}
.brand-org {
  font-size: 0.82rem;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 400;
}

/* Tagline */
.brand-tagline {
  margin: auto 0;
}
.brand-tagline p {
  font-size: 1.35rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.55;
  letter-spacing: -0.01em;
}

/* Features */
.features {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}
.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}
.feature-icon-wrap {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: rgba(255, 255, 255, 0.9);
}
.feature-icon-wrap svg { width: 18px; height: 18px; }
.feature-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: white;
  margin-bottom: 0.15rem;
}
.feature-desc {
  font-size: 0.79rem;
  color: rgba(255, 255, 255, 0.55);
  line-height: 1.4;
}

/* Footer gauche */
.left-footer {
  font-size: 0.72rem;
  color: rgba(255, 255, 255, 0.3);
  margin-top: 1rem;
}

/* ══════════════════════════════════════
   PANNEAU DROIT
══════════════════════════════════════ */
.login-right {
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem 2rem;
}

.form-wrapper {
  width: 100%;
  max-width: 400px;
}

/* En-tête formulaire */
.form-header {
  text-align: center;
  margin-bottom: 2rem;
}
.form-logo-sm {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, #059669, #047857);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
  box-shadow: 0 4px 14px rgba(5, 150, 105, 0.35);
}
.form-logo-sm svg { width: 22px; height: 22px; }

.form-header h2 {
  font-size: 1.6rem;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 0.35rem;
}
.form-subtitle {
  font-size: 0.875rem;
  color: #64748b;
}

/* Inputs */
.form-group { margin-bottom: 1.1rem; }

label {
  display: block;
  margin-bottom: 0.35rem;
  font-size: 0.82rem;
  font-weight: 600;
  color: #334155;
  letter-spacing: 0.01em;
}

.input-wrap { position: relative; }

.input-icon {
  position: absolute;
  left: 0.9rem;
  top: 50%;
  transform: translateY(-50%);
  width: 17px;
  height: 17px;
  color: #94a3b8;
  pointer-events: none;
}

.input-wrap input {
  width: 100%;
  padding: 0.65rem 2.75rem 0.65rem 2.55rem;
  font-size: 0.9rem;
  color: #1e293b;
  background: white;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  transition: border-color 0.15s, box-shadow 0.15s;
  line-height: 1.5;
}
.input-wrap input:focus {
  outline: none;
  border-color: #06b6d4;
  box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.15);
}
.input-wrap input.error {
  border-color: #ef4444;
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}
.input-wrap input::placeholder { color: #cbd5e1; }

.toggle-eye {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px;
  color: #94a3b8;
  display: flex;
  align-items: center;
  border-radius: 4px;
  transition: color 0.15s;
}
.toggle-eye:hover { color: #475569; }
.toggle-eye svg { width: 16px; height: 16px; }

/* Erreur champ */
.error-message {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  color: #ef4444;
  font-size: 0.79rem;
  margin-top: 0.3rem;
  font-weight: 500;
}
.error-message::before { content: '⚠'; font-size: 0.72rem; }

/* Alerte erreur globale */
.alert-error {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  background: #fef2f2;
  color: #991b1b;
  border: 1px solid #fca5a5;
  border-radius: 10px;
  padding: 0.65rem 0.9rem;
  font-size: 0.84rem;
  font-weight: 500;
  margin-bottom: 1rem;
}
.alert-error svg { width: 16px; height: 16px; flex-shrink: 0; }

/* ── Bouton cyan ── */
.btn-login {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.55rem;
  padding: 0.75rem 1.5rem;
  margin-top: 0.5rem;
  font-size: 0.95rem;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #06b6d4, #0891b2);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.18s ease;
  box-shadow: 0 3px 12px rgba(6, 182, 212, 0.35);
  letter-spacing: 0.01em;
}
.btn-login:hover:not(:disabled) {
  background: linear-gradient(135deg, #0891b2, #0e7490);
  box-shadow: 0 5px 18px rgba(6, 182, 212, 0.45);
  transform: translateY(-1px);
}
.btn-login:active:not(:disabled) { transform: scale(0.98); }
.btn-login:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-login svg { width: 18px; height: 18px; }

/* Spinner dans le bouton */
.btn-spinner {
  width: 17px;
  height: 17px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  flex-shrink: 0;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── Badge JWT ── */
.jwt-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
  margin-top: 1.75rem;
  padding: 0.55rem 1.1rem;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
  color: #64748b;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  width: fit-content;
  margin-left: auto;
  margin-right: auto;
}
.jwt-badge svg { width: 13px; height: 13px; color: #059669; stroke: #059669; }

/* ══════════════════════════════════════
   RESPONSIVE
══════════════════════════════════════ */
@media (max-width: 900px) {
  .login-page { grid-template-columns: 1fr; }

  .login-left {
    min-height: 220px;
    padding: 2rem 2rem 1.75rem;
  }
  .left-inner { justify-content: flex-start; gap: 1.25rem; }
  .brand-tagline { display: none; }
  .features { flex-direction: row; flex-wrap: wrap; gap: 0.75rem; }
  .feature-desc { display: none; }
  .left-footer { display: none; }
  .deco-1 { width: 260px; height: 260px; top: -80px; right: -60px; }

  .login-right { padding: 2.5rem 1.5rem; background: white; }
}

@media (max-width: 480px) {
  .login-left { display: none; }
  .login-right { padding: 2rem 1.25rem; }
}
</style>