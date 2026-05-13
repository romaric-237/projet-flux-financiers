<template>
  <div class="page">

    <!-- En-tête -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Gestion des utilisateurs</h1>
        <p class="page-subtitle">Administration des comptes  accès ADMIN</p>
      </div>
      <button class="btn btn-primary" @click="openForm()">+ Nouvel utilisateur</button>
    </div>

    <!-- Stats -->
    <div class="stats-bar">
      <div class="stat-item">
        <div class="stat-icon blue">👤</div>
        <div class="stat-text">
          <span class="stat-value">{{ users.length }}</span>
          <span class="stat-label">Total comptes</span>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon green">✅</div>
        <div class="stat-text">
          <span class="stat-value">{{ usersActifs }}</span>
          <span class="stat-label">Actifs</span>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon purple">🔑</div>
        <div class="stat-text">
          <span class="stat-value">{{ usersAdmin }}</span>
          <span class="stat-label">Administrateurs</span>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon orange">🚫</div>
        <div class="stat-text">
          <span class="stat-value">{{ usersInactifs }}</span>
          <span class="stat-label">Désactivés</span>
        </div>
      </div>
    </div>

    <!-- Recherche -->
    <div class="search-bar">
      <div class="search-input-wrap">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input v-model="search" type="text" placeholder="Rechercher par nom, prénom ou identifiant..." class="search-input" />
        <button v-if="search" class="search-clear" @click="search = ''">✕</button>
      </div>
      <span class="search-count">{{ filteredUsers.length }} / {{ users.length }} utilisateur(s)</span>
    </div>

    <!-- Tableau -->
    <div v-if="loading" class="spinner"></div>

    <div v-else class="card table-card">
      <table v-if="filteredUsers.length">
        <thead>
          <tr>
            <th>Utilisateur</th>
            <th>Identifiant</th>
            <th>Email</th>
            <th>Rôle</th>
            <th>Statut</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in filteredUsers" :key="u.id" :class="{ 'row-inactive': !u.actif }">
            <td>
              <div class="user-cell">
                <div class="avatar" :style="{ background: avatarColor(u.nom) }">
                  {{ initiales(u.nom, u.prenom) }}
                </div>
                <div>
                  <p class="user-name">{{ u.prenom }} {{ u.nom }}</p>
                  <p class="user-sub">ID #{{ u.id }}</p>
                </div>
              </div>
            </td>
            <td class="mono">{{ u.username }}</td>
            <td class="text-muted-sm">{{ u.email || '—' }}</td>
            <td>
              <span :class="['badge-role', u.role === 'ADMIN' ? 'role-admin' : 'role-gest']">
                {{ u.role === 'ADMIN' ? '🔑 Admin' : '📋 Gestionnaire' }}
              </span>
            </td>
            <td>
              <span :class="['badge-status', u.actif ? 'status-actif' : 'status-inactif']">
                {{ u.actif ? 'Actif' : 'Désactivé' }}
              </span>
            </td>
            <td>
              <div class="d-flex gap-1">
                <button class="btn btn-secondary btn-sm" @click="openForm(u)" title="Modifier">
                  ✏️
                </button>
                <button
                  v-if="u.actif"
                  class="btn btn-warning btn-sm"
                  @click="toggleActif(u)"
                  :disabled="u.id === authStore.currentUser?.id"
                  title="Désactiver"
                >
                  🚫
                </button>
                <button
                  v-else
                  class="btn btn-success btn-sm"
                  @click="toggleActif(u)"
                  title="Réactiver"
                >
                  ✅
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucun utilisateur trouvé.</p>
    </div>

    <!-- Modal : Formulaire utilisateur -->
    <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
      <div class="modal-card">
        <div class="modal-header">
          <h3>{{ editingId ? 'Modifier l\'utilisateur' : 'Nouvel utilisateur' }}</h3>
          <button class="modal-close" @click="closeForm">✕</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="save">

            <div class="form-row">
              <div class="form-group">
                <label>Nom *</label>
                <input v-model="form.nom" type="text" :class="{ error: errors.nom }" placeholder="Nom de famille" />
                <span v-if="errors.nom" class="error-message">{{ errors.nom }}</span>
              </div>
              <div class="form-group">
                <label>Prénom *</label>
                <input v-model="form.prenom" type="text" :class="{ error: errors.prenom }" placeholder="Prénom" />
                <span v-if="errors.prenom" class="error-message">{{ errors.prenom }}</span>
              </div>
            </div>

            <div class="form-group">
              <label>Email</label>
              <input v-model="form.email" type="email" placeholder="email@exemple.com" />
            </div>

            <div class="form-group">
              <label>Identifiant (username) *</label>
              <input v-model="form.username" type="text" :class="{ error: errors.username }" placeholder="Identifiant de connexion" />
              <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
            </div>

            <div class="form-group">
              <label>{{ editingId ? 'Nouveau mot de passe (laisser vide = inchangé)' : 'Mot de passe *' }}</label>
              <div class="input-password">
                <input
                  v-model="form.password"
                  :type="showPwd ? 'text' : 'password'"
                  :class="{ error: errors.password }"
                  placeholder="••••••••"
                />
                <button type="button" class="pwd-toggle" @click="showPwd = !showPwd">
                  {{ showPwd ? '🙈' : '👁️' }}
                </button>
              </div>
              <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
            </div>

            <div class="form-group">
              <label>Rôle *</label>
              <select v-model="form.role" :class="{ error: errors.role }">
                <option value="">— Sélectionner un rôle —</option>
                <option value="GESTIONNAIRE">Gestionnaire</option>
                <option value="ADMIN">Administrateur</option>
              </select>
              <span v-if="errors.role" class="error-message">{{ errors.role }}</span>
            </div>

            <div v-if="form.role === 'ADMIN'" class="alert-info">
              ⚠️ Ce compte aura un accès complet à l'application, y compris la gestion des utilisateurs.
            </div>

            <div class="modal-actions">
              <button type="submit" class="btn btn-primary" :disabled="saving">
                {{ saving ? 'Enregistrement...' : 'Enregistrer' }}
              </button>
              <button type="button" class="btn btn-secondary" @click="closeForm">Annuler</button>
            </div>
          </form>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const toast     = useToast()
const authStore = useAuthStore()

const users    = ref([])
const search   = ref('')
const loading  = ref(false)
const saving   = ref(false)
const showForm = ref(false)
const showPwd  = ref(false)
const editingId = ref(null)

const form = ref({ nom: '', prenom: '', email: '', username: '', password: '', role: '' })
const errors = ref({})

onMounted(loadUsers)

async function loadUsers() {
  loading.value = true
  try {
    const res = await api.get('/users')
    users.value = res.data
  } catch {
    toast.error('Impossible de charger les utilisateurs')
  } finally {
    loading.value = false
  }
}

// ── Computed ──────────────────────────────────────
const filteredUsers = computed(() => {
  const q = search.value.trim().toLowerCase()
  if (!q) return users.value
  return users.value.filter(u =>
    u.nom?.toLowerCase().includes(q) ||
    u.prenom?.toLowerCase().includes(q) ||
    u.username?.toLowerCase().includes(q)
  )
})

const usersActifs   = computed(() => users.value.filter(u => u.actif).length)
const usersInactifs = computed(() => users.value.filter(u => !u.actif).length)
const usersAdmin    = computed(() => users.value.filter(u => u.role === 'ADMIN').length)

// ── Actions ───────────────────────────────────────
function openForm(user = null) {
  errors.value = {}
  showPwd.value = false
  if (user) {
    editingId.value = user.id
    form.value = {
      nom: user.nom || '',
      prenom: user.prenom || '',
      email: user.email || '',
      username: user.username || '',
      password: '',
      role: user.role || ''
    }
  } else {
    editingId.value = null
    form.value = { nom: '', prenom: '', email: '', username: '', password: '', role: '' }
  }
  showForm.value = true
}

function closeForm() {
  showForm.value = false
  editingId.value = null
}

function validate() {
  const e = {}
  if (!form.value.nom.trim())      e.nom      = 'Le nom est obligatoire'
  if (!form.value.prenom.trim())   e.prenom   = 'Le prénom est obligatoire'
  if (!form.value.username.trim()) e.username = 'L\'identifiant est obligatoire'
  if (!form.value.role)            e.role     = 'Le rôle est obligatoire'
  if (!editingId.value && !form.value.password)
                                   e.password = 'Le mot de passe est obligatoire'
  if (form.value.password && form.value.password.length < 6)
                                   e.password = 'Minimum 6 caractères'
  errors.value = e
  return Object.keys(e).length === 0
}

async function save() {
  if (!validate()) return
  saving.value = true
  const payload = {
    nom: form.value.nom,
    prenom: form.value.prenom,
    email: form.value.email || null,
    username: form.value.username,
    role: form.value.role,
    ...(form.value.password ? { password: form.value.password } : {})
  }
  try {
    if (editingId.value) {
      await api.put(`/users/${editingId.value}`, payload)
      toast.success('Utilisateur modifié')
    } else {
      await api.post('/users', payload)
      toast.success('Utilisateur créé')
    }
    closeForm()
    await loadUsers()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Erreur lors de l\'enregistrement')
  } finally {
    saving.value = false
  }
}

async function toggleActif(u) {
  const action = u.actif ? 'désactiver' : 'réactiver'
  if (!confirm(`Voulez-vous ${action} le compte de ${u.prenom} ${u.nom} ?`)) return
  try {
    if (u.actif) {
      await api.patch(`/users/${u.id}/desactiver`)
      toast.success('Compte désactivé')
    } else {
      await api.patch(`/users/${u.id}/reactiver`)
      toast.success('Compte réactivé')
    }
    await loadUsers()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Erreur')
  }
}

// ── Utilitaires ───────────────────────────────────
function initiales(nom, prenom) {
  return ((nom?.[0] || '') + (prenom?.[0] || '')).toUpperCase()
}

const AVATAR_COLORS = ['#2E7D5E','#1e88e5','#e53935','#f59e0b','#8e24aa','#00897b','#d81b60','#3949ab']
function avatarColor(nom) {
  let hash = 0
  for (const c of (nom || '')) hash = c.charCodeAt(0) + hash * 31
  return AVATAR_COLORS[Math.abs(hash) % AVATAR_COLORS.length]
}
</script>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; padding: 0 1rem 2rem; }

/* ── Stats bar ── */
.stats-bar {
  display: flex; align-items: center;
  background: var(--surface-1); border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm); border: 1px solid var(--border);
  margin-bottom: 1.25rem; overflow: hidden;
}
.stat-item {
  display: flex; align-items: center; gap: 1rem;
  flex: 1; padding: 1.1rem 1.5rem;
  border-right: 1px solid var(--color-gray-200);
}
.stat-item:last-child { border-right: none; }
.stat-icon {
  width: 42px; height: 42px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.1rem; flex-shrink: 0;
}
.stat-icon.blue   { background: var(--color-info-light); }
.stat-icon.green  { background: var(--color-success-light); }
.stat-icon.purple { background: #ede9fe; }
.stat-icon.orange { background: #fff7ed; }
.stat-text { display: flex; flex-direction: column; }
.stat-value { font-size: 1.4rem; font-weight: 700; color: var(--color-gray-900); line-height: 1.2; }
.stat-label { font-size: 0.72rem; color: var(--color-gray-500); text-transform: uppercase; letter-spacing: 0.05em; margin-top: 0.1rem; }

/* ── Recherche ── */
.search-bar { display: flex; align-items: center; gap: 1rem; margin-bottom: 1rem; }
.search-input-wrap { position: relative; flex: 1; max-width: 420px; }
.search-icon {
  position: absolute; left: 0.8rem; top: 50%; transform: translateY(-50%);
  width: 17px; height: 17px; color: var(--color-gray-400); pointer-events: none;
}
.search-input {
  width: 100%; padding: 0.55rem 2.5rem 0.55rem 2.3rem;
  border: 1px solid var(--border); border-radius: var(--border-radius);
  font-size: 0.9rem; background: var(--surface-1); color: var(--text-primary);
  transition: border-color 0.15s;
}
.search-input:focus { outline: none; border-color: var(--color-primary); box-shadow: var(--focus-ring); }
.search-clear {
  position: absolute; right: 0.75rem; top: 50%; transform: translateY(-50%);
  background: none; border: none; cursor: pointer; color: var(--color-gray-400);
  font-size: 0.8rem; width: 18px; height: 18px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 50%;
}
.search-clear:hover { background: var(--color-gray-200); }
.search-count { font-size: 0.82rem; color: var(--color-gray-500); white-space: nowrap; }

/* ── Table ── */
.table-card { padding: 0; overflow: hidden; }
.table-card table { margin: 0; border-radius: 0; }

.user-cell { display: flex; align-items: center; gap: 0.85rem; }
.avatar {
  width: 38px; height: 38px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: white; font-size: 0.82rem; font-weight: 700; flex-shrink: 0;
  box-shadow: 0 0 0 2px var(--surface-1), 0 0 0 3px var(--border);
}
.user-name { font-weight: 600; color: var(--color-gray-800); font-size: 0.9rem; }
.user-sub  { font-size: 0.75rem; color: var(--color-gray-400); margin-top: 0.1rem; }
.mono      { font-family: monospace; font-size: 0.85rem; color: var(--color-gray-700); }
.text-muted-sm { font-size: 0.82rem; color: var(--color-gray-500); }

.row-inactive td { opacity: 0.55; }

/* ── Badges ── */
.badge-role {
  display: inline-flex; align-items: center; gap: 0.3rem;
  padding: 0.2rem 0.65rem; border-radius: 20px;
  font-size: 0.75rem; font-weight: 600; white-space: nowrap;
}
.role-admin { background: #ede9fe; color: #6d28d9; }
.role-gest  { background: var(--color-primary-light); color: var(--color-primary-dark); }

.badge-status {
  display: inline-block; padding: 0.2rem 0.65rem; border-radius: 20px;
  font-size: 0.75rem; font-weight: 600;
}
.status-actif   { background: var(--color-success-light); color: var(--color-success); }
.status-inactif { background: rgba(255,255,255,0.05); color: var(--text-tertiary); }

/* ── Modal ── */
.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(15, 23, 42, 0.5); backdrop-filter: blur(2px);
  display: flex; align-items: center; justify-content: center;
  z-index: 2000; padding: 1rem;
  animation: fadeIn 0.15s ease;
}
.modal-card {
  background: var(--surface-2); border: 1px solid var(--border-bright);
  border-radius: var(--border-radius-xl);
  width: 100%; max-width: 500px; max-height: 90vh; overflow-y: auto;
  box-shadow: var(--shadow-xl); animation: slideUp 0.2s ease;
}
.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 1.1rem 1.5rem;
  background: var(--color-gray-50); border-bottom: 1px solid var(--color-gray-200);
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
}
.modal-header h3 { font-size: 1rem; font-weight: 600; margin-bottom: 0; }
.modal-close {
  width: 28px; height: 28px; border-radius: 6px;
  background: none; border: none; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  color: var(--color-gray-500); font-size: 0.9rem; transition: all 0.15s;
}
.modal-close:hover { background: var(--color-gray-200); }
.modal-body { padding: 1.5rem; }

.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 0.75rem; }

.input-password { position: relative; }
.input-password input { padding-right: 2.5rem; width: 100%; }
.pwd-toggle {
  position: absolute; right: 0.5rem; top: 50%; transform: translateY(-50%);
  background: none; border: none; cursor: pointer; font-size: 1rem; padding: 0;
}

.alert-info {
  padding: 0.75rem 1rem; background: var(--color-info-light); border: 1px solid rgba(96,165,250,0.2);
  border-radius: var(--border-radius); font-size: 0.82rem; color: var(--color-info);
  margin-bottom: 0.75rem;
}

.modal-actions { display: flex; gap: 0.75rem; margin-top: 1.25rem; }

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes slideUp {
  from { opacity: 0; transform: translateY(10px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

@media (max-width: 768px) {
  .stats-bar { flex-direction: column; }
  .stat-item { border-right: none; border-bottom: 1px solid var(--color-gray-200); width: 100%; }
  .stat-item:last-child { border-bottom: none; }
  .form-row { grid-template-columns: 1fr; }
}
</style>