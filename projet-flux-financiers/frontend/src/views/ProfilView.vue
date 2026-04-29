<template>
  <div class="page">

    <div class="page-header">
      <div>
        <h1 class="page-title">Mon profil</h1>
        <p class="page-subtitle">Informations personnelles et sécurité du compte</p>
      </div>
    </div>

    <div class="profil-grid">

      <!-- Carte identité -->
      <div class="card profil-card">
        <div class="card-header">
          <h2>Identité</h2>
        </div>
        <div class="profil-avatar-section">
          <div class="profil-avatar" :style="{ background: avatarColor(authStore.currentUser?.nom) }">
            {{ initiales }}
          </div>
          <div>
            <p class="profil-name">{{ authStore.currentUser?.prenom }} {{ authStore.currentUser?.nom }}</p>
            <span :class="['badge-role', authStore.isAdmin ? 'role-admin' : 'role-gest']">
              {{ authStore.isAdmin ? '🔑 Administrateur' : '📋 Gestionnaire' }}
            </span>
          </div>
        </div>

        <div class="profil-fields">
          <div class="profil-field">
            <span class="field-label">Identifiant</span>
            <span class="field-value mono">{{ authStore.currentUser?.username }}</span>
          </div>
          <div class="profil-field" v-if="authStore.currentUser?.email">
            <span class="field-label">Email</span>
            <span class="field-value">{{ authStore.currentUser?.email }}</span>
          </div>
          <div class="profil-field">
            <span class="field-label">Rôle</span>
            <span class="field-value">{{ authStore.isAdmin ? 'Administrateur' : 'Gestionnaire' }}</span>
          </div>
        </div>
      </div>

      <!-- Carte changement de mot de passe -->
      <div class="card password-card">
        <div class="card-header">
          <h2>Changer le mot de passe</h2>
        </div>
        <div class="password-body">

          <div v-if="success" class="alert-success">
            ✅ Mot de passe modifié. Vous allez être déconnecté dans {{ countdown }}s...
          </div>

          <form v-else @submit.prevent="changerMotDePasse">

            <div class="form-group">
              <label>Mot de passe actuel *</label>
              <div class="input-password">
                <input
                  v-model="form.ancienMotDePasse"
                  :type="show.ancien ? 'text' : 'password'"
                  :class="{ error: errors.ancienMotDePasse }"
                  placeholder="••••••••"
                  autocomplete="current-password"
                />
                <button type="button" class="pwd-toggle" @click="show.ancien = !show.ancien">
                  {{ show.ancien ? '🙈' : '👁️' }}
                </button>
              </div>
              <span v-if="errors.ancienMotDePasse" class="error-message">{{ errors.ancienMotDePasse }}</span>
            </div>

            <div class="form-group">
              <label>Nouveau mot de passe *</label>
              <div class="input-password">
                <input
                  v-model="form.nouveauMotDePasse"
                  :type="show.nouveau ? 'text' : 'password'"
                  :class="{ error: errors.nouveauMotDePasse }"
                  placeholder="••••••••"
                  autocomplete="new-password"
                />
                <button type="button" class="pwd-toggle" @click="show.nouveau = !show.nouveau">
                  {{ show.nouveau ? '🙈' : '👁️' }}
                </button>
              </div>
              <span v-if="errors.nouveauMotDePasse" class="error-message">{{ errors.nouveauMotDePasse }}</span>

              <!-- Indicateur de force -->
              <div v-if="form.nouveauMotDePasse" class="strength-bar">
                <div class="strength-track">
                  <div class="strength-fill" :class="strengthClass" :style="{ width: strengthWidth }"></div>
                </div>
                <span class="strength-label" :class="strengthClass">{{ strengthLabel }}</span>
              </div>
            </div>

            <div class="form-group">
              <label>Confirmer le nouveau mot de passe *</label>
              <div class="input-password">
                <input
                  v-model="form.confirmation"
                  :type="show.confirm ? 'text' : 'password'"
                  :class="{ error: errors.confirmation }"
                  placeholder="••••••••"
                  autocomplete="new-password"
                />
                <button type="button" class="pwd-toggle" @click="show.confirm = !show.confirm">
                  {{ show.confirm ? '🙈' : '👁️' }}
                </button>
              </div>
              <span v-if="errors.confirmation" class="error-message">{{ errors.confirmation }}</span>

              <!-- Indicateur concordance -->
              <div v-if="form.confirmation && form.nouveauMotDePasse" class="match-indicator">
                <span v-if="form.confirmation === form.nouveauMotDePasse" class="match-ok">✅ Les mots de passe correspondent</span>
                <span v-else class="match-ko">❌ Les mots de passe ne correspondent pas</span>
              </div>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn-primary btn-full" :disabled="saving">
                {{ saving ? 'Modification en cours...' : '🔒 Changer le mot de passe' }}
              </button>
            </div>

          </form>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'
import api from '@/services/api'

const router    = useRouter()
const authStore = useAuthStore()
const toast     = useToast()

const saving  = ref(false)
const success = ref(false)
const countdown = ref(3)

const form = ref({
  ancienMotDePasse: '',
  nouveauMotDePasse: '',
  confirmation: ''
})

const show = ref({ ancien: false, nouveau: false, confirm: false })
const errors = ref({})

// ── Computed ──────────────────────────────────────
const initiales = computed(() => {
  const u = authStore.currentUser
  if (!u) return ''
  return ((u.prenom?.[0] || '') + (u.nom?.[0] || '')).toUpperCase()
})

const strengthScore = computed(() => {
  const pwd = form.value.nouveauMotDePasse
  if (!pwd) return 0
  let score = 0
  if (pwd.length >= 6)  score++
  if (pwd.length >= 10) score++
  if (/[A-Z]/.test(pwd)) score++
  if (/[0-9]/.test(pwd)) score++
  if (/[^A-Za-z0-9]/.test(pwd)) score++
  return score
})

const strengthClass = computed(() => {
  if (strengthScore.value <= 1) return 'weak'
  if (strengthScore.value <= 3) return 'medium'
  return 'strong'
})

const strengthWidth = computed(() => `${(strengthScore.value / 5) * 100}%`)

const strengthLabel = computed(() => {
  if (strengthScore.value <= 1) return 'Faible'
  if (strengthScore.value <= 3) return 'Moyen'
  return 'Fort'
})

// ── Actions ───────────────────────────────────────
function validate() {
  const e = {}
  if (!form.value.ancienMotDePasse)   e.ancienMotDePasse   = 'L\'ancien mot de passe est obligatoire'
  if (!form.value.nouveauMotDePasse)  e.nouveauMotDePasse  = 'Le nouveau mot de passe est obligatoire'
  else if (form.value.nouveauMotDePasse.length < 6)
                                      e.nouveauMotDePasse  = 'Minimum 6 caractères'
  if (!form.value.confirmation)       e.confirmation       = 'La confirmation est obligatoire'
  else if (form.value.confirmation !== form.value.nouveauMotDePasse)
                                      e.confirmation       = 'Les mots de passe ne correspondent pas'
  errors.value = e
  return Object.keys(e).length === 0
}

async function changerMotDePasse() {
  if (!validate()) return
  saving.value = true
  try {
    await api.post('/users/changer-mot-de-passe', {
      ancienMotDePasse:  form.value.ancienMotDePasse,
      nouveauMotDePasse: form.value.nouveauMotDePasse,
      confirmation:      form.value.confirmation
    })
    success.value = true
    // Déconnexion automatique après 3 secondes
    const interval = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(interval)
        authStore.logout()
        router.push('/login')
      }
    }, 1000)
  } catch (err) {
    toast.error(err.response?.data?.message || 'Erreur lors du changement de mot de passe')
  } finally {
    saving.value = false
  }
}

const AVATAR_COLORS = ['#2E7D5E','#1e88e5','#e53935','#f59e0b','#8e24aa','#00897b','#d81b60','#3949ab']
function avatarColor(nom) {
  let hash = 0
  for (const c of (nom || '')) hash = c.charCodeAt(0) + hash * 31
  return AVATAR_COLORS[Math.abs(hash) % AVATAR_COLORS.length]
}
</script>

<style scoped>
.page { max-width: 900px; margin: 0 auto; padding: 0 1rem 2rem; }

.profil-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 1.5rem;
  align-items: start;
}

/* ── Carte identité ── */
.profil-card, .password-card { padding: 0; overflow: hidden; }

.card-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid var(--color-gray-200);
  background: var(--color-gray-50);
}
.card-header h2 { font-size: 0.95rem; font-weight: 600; color: var(--color-gray-800); margin: 0; }

.profil-avatar-section {
  display: flex; align-items: center; gap: 1rem;
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-gray-100);
}
.profil-avatar {
  width: 64px; height: 64px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: white; font-size: 1.4rem; font-weight: 700; flex-shrink: 0;
  box-shadow: 0 0 0 3px white, 0 0 0 5px rgba(0,0,0,0.08);
}
.profil-name { font-size: 1.05rem; font-weight: 700; color: var(--color-gray-900); margin-bottom: 0.3rem; }

.badge-role {
  display: inline-flex; align-items: center; gap: 0.3rem;
  padding: 0.2rem 0.65rem; border-radius: 20px;
  font-size: 0.75rem; font-weight: 600;
}
.role-admin { background: #ede9fe; color: #6d28d9; }
.role-gest  { background: var(--color-primary-light); color: var(--color-primary-dark); }

.profil-fields { padding: 1rem 1.5rem; display: flex; flex-direction: column; gap: 0.85rem; }
.profil-field { display: flex; flex-direction: column; gap: 0.2rem; }
.field-label { font-size: 0.72rem; text-transform: uppercase; letter-spacing: 0.05em; color: var(--color-gray-400); font-weight: 600; }
.field-value { font-size: 0.9rem; color: var(--color-gray-800); }
.mono { font-family: monospace; }

/* ── Carte mot de passe ── */
.password-body { padding: 1.5rem; }

.input-password { position: relative; }
.input-password input { padding-right: 2.5rem; width: 100%; }
.pwd-toggle {
  position: absolute; right: 0.6rem; top: 50%; transform: translateY(-50%);
  background: none; border: none; cursor: pointer; font-size: 1rem; padding: 0;
}

/* Indicateur de force */
.strength-bar { display: flex; align-items: center; gap: 0.6rem; margin-top: 0.4rem; }
.strength-track {
  flex: 1; height: 4px; background: var(--color-gray-200);
  border-radius: 2px; overflow: hidden;
}
.strength-fill { height: 100%; border-radius: 2px; transition: width 0.3s, background 0.3s; }
.strength-fill.weak   { background: var(--color-danger); }
.strength-fill.medium { background: var(--color-warning, #f59e0b); }
.strength-fill.strong { background: var(--color-success); }
.strength-label { font-size: 0.75rem; font-weight: 600; white-space: nowrap; }
.strength-label.weak   { color: var(--color-danger); }
.strength-label.medium { color: #d97706; }
.strength-label.strong { color: var(--color-success); }

/* Indicateur de concordance */
.match-indicator { margin-top: 0.4rem; font-size: 0.8rem; }
.match-ok { color: var(--color-success); }
.match-ko { color: var(--color-danger); }

/* Alerte succès */
.alert-success {
  padding: 1.25rem; background: var(--color-success-light);
  border: 1px solid #a7f3d0; border-radius: var(--border-radius);
  color: #065f46; font-weight: 500; text-align: center;
  font-size: 0.95rem; line-height: 1.6;
}

.form-actions { margin-top: 1.5rem; }
.btn-full { width: 100%; justify-content: center; }

@media (max-width: 768px) {
  .profil-grid { grid-template-columns: 1fr; }
}
</style>