<template>
  <div class="page">

    <!-- En-tête -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Journal d'activité</h1>
        <p class="page-subtitle">
          {{ authStore.isAdmin ? 'Toutes les actions enregistrées' : 'Vos actions enregistrées' }}
        </p>
      </div>
    </div>

    <!-- Stats -->
    <div class="stats-bar">
      <div class="stat-item">
        <div class="stat-icon blue">📋</div>
        <div class="stat-text">
          <span class="stat-value">{{ logs.length }}</span>
          <span class="stat-label">Total actions</span>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon green">➕</div>
        <div class="stat-text">
          <span class="stat-value">{{ countByAction('CREATION') }}</span>
          <span class="stat-label">Créations</span>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon orange">✏️</div>
        <div class="stat-text">
          <span class="stat-value">{{ countByAction('MODIFICATION') }}</span>
          <span class="stat-label">Modifications</span>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon red">🗑️</div>
        <div class="stat-text">
          <span class="stat-value">{{ countByAction('SUPPRESSION') }}</span>
          <span class="stat-label">Suppressions</span>
        </div>
      </div>
    </div>

    <!-- Filtres -->
    <div class="filters-bar">
      <!-- Recherche texte -->
      <div class="search-input-wrap">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input v-model="search" type="text" placeholder="Rechercher..." class="search-input" />
        <button v-if="search" class="search-clear" @click="search = ''">✕</button>
      </div>

      <!-- Filtre action -->
      <select v-model="filterAction" class="filter-select">
        <option value="">Toutes les actions</option>
        <option value="CREATION">Créations</option>
        <option value="MODIFICATION">Modifications</option>
        <option value="SUPPRESSION">Suppressions</option>
      </select>

      <!-- Filtre entité -->
      <select v-model="filterEntite" class="filter-select">
        <option value="">Tous les types</option>
        <option v-for="type in entiteTypes" :key="type" :value="type">{{ type }}</option>
      </select>

      <!-- Filtre utilisateur (ADMIN seulement) -->
      <select v-if="authStore.isAdmin" v-model="filterUser" class="filter-select">
        <option value="">Tous les utilisateurs</option>
        <option v-for="u in userList" :key="u" :value="u">{{ u }}</option>
      </select>

      <!-- Reset -->
      <button v-if="hasFilters" class="btn btn-secondary btn-sm" @click="resetFiltres">
        Réinitialiser
      </button>

      <span class="search-count">{{ filteredLogs.length }} / {{ logs.length }} entrée(s)</span>
    </div>

    <!-- Tableau -->
    <div v-if="loading" class="spinner"></div>

    <div v-else class="card table-card">
      <table v-if="filteredLogs.length">
        <thead>
          <tr>
            <th>Date / Heure</th>
            <th v-if="authStore.isAdmin">Utilisateur</th>
            <th>Action</th>
            <th>Entité</th>
            <th>Détails</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in filteredLogs" :key="log.id">
            <td>
              <div class="date-cell">
                <span class="date-day">{{ formatDate(log.dateAction) }}</span>
                <span class="date-time">{{ formatTime(log.dateAction) }}</span>
              </div>
            </td>
            <td v-if="authStore.isAdmin">
              <div class="user-cell">
                <div class="mini-avatar" :style="{ background: avatarColor(log.username) }">
                  {{ (log.username || '?')[0].toUpperCase() }}
                </div>
                <span class="mono">{{ log.username }}</span>
              </div>
            </td>
            <td>
              <span :class="['badge-action', actionClass(log.action)]">
                {{ actionLabel(log.action) }}
              </span>
            </td>
            <td>
              <span class="badge-entite">{{ log.entiteType }}</span>
              <span v-if="log.entiteId" class="entite-id">#{{ log.entiteId }}</span>
            </td>
            <td class="details-cell">{{ log.details || '—' }}</td>
          </tr>
        </tbody>
      </table>
      <div v-else class="empty-state">
        <p class="text-muted text-center">Aucune entrée trouvée.</p>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const authStore = useAuthStore()
const toast     = useToast()

const logs    = ref([])
const loading = ref(false)

const search       = ref('')
const filterAction = ref('')
const filterEntite = ref('')
const filterUser   = ref('')

onMounted(loadLogs)

async function loadLogs() {
  loading.value = true
  try {
    const res = await api.get('/audit')
    logs.value = res.data
  } catch {
    toast.error('Impossible de charger le journal')
  } finally {
    loading.value = false
  }
}

// ── Computed ──────────────────────────────────────
const filteredLogs = computed(() => {
  return logs.value.filter(log => {
    const q = search.value.trim().toLowerCase()
    if (q && !log.details?.toLowerCase().includes(q) &&
             !log.entiteType?.toLowerCase().includes(q) &&
             !log.username?.toLowerCase().includes(q)) return false
    if (filterAction.value && log.action !== filterAction.value) return false
    if (filterEntite.value && log.entiteType !== filterEntite.value) return false
    if (filterUser.value && log.username !== filterUser.value) return false
    return true
  })
})

const entiteTypes = computed(() => [...new Set(logs.value.map(l => l.entiteType))].sort())
const userList    = computed(() => [...new Set(logs.value.map(l => l.username))].sort())

const hasFilters = computed(() =>
  search.value || filterAction.value || filterEntite.value || filterUser.value
)

function countByAction(action) {
  return logs.value.filter(l => l.action === action).length
}

function resetFiltres() {
  search.value = ''
  filterAction.value = ''
  filterEntite.value = ''
  filterUser.value   = ''
}

// ── Formatage ─────────────────────────────────────
function formatDate(dt) {
  if (!dt) return '—'
  return new Date(dt).toLocaleDateString('fr-BE', { day: '2-digit', month: 'short', year: 'numeric' })
}

function formatTime(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleTimeString('fr-BE', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

function actionLabel(action) {
  const labels = { CREATION: '+ Création', MODIFICATION: '✏ Modification', SUPPRESSION: '✕ Suppression' }
  return labels[action] || action
}

function actionClass(action) {
  return { CREATION: 'action-create', MODIFICATION: 'action-update', SUPPRESSION: 'action-delete' }[action] || ''
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
  background: white; border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm); border: 1px solid var(--color-gray-200);
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
.stat-icon.orange { background: #fff7ed; }
.stat-icon.red    { background: var(--color-danger-light); }
.stat-text { display: flex; flex-direction: column; }
.stat-value { font-size: 1.4rem; font-weight: 700; color: var(--color-gray-900); line-height: 1.2; }
.stat-label { font-size: 0.72rem; color: var(--color-gray-500); text-transform: uppercase; letter-spacing: 0.05em; margin-top: 0.1rem; }

/* ── Filtres ── */
.filters-bar {
  display: flex; align-items: center; flex-wrap: wrap; gap: 0.75rem;
  margin-bottom: 1rem;
}
.search-input-wrap { position: relative; flex: 1; min-width: 200px; max-width: 300px; }
.search-icon {
  position: absolute; left: 0.8rem; top: 50%; transform: translateY(-50%);
  width: 17px; height: 17px; color: var(--color-gray-400); pointer-events: none;
}
.search-input {
  width: 100%; padding: 0.55rem 2.2rem 0.55rem 2.3rem;
  border: 1px solid var(--color-gray-300); border-radius: var(--border-radius);
  font-size: 0.9rem; background: white;
}
.search-input:focus { outline: none; border-color: var(--color-primary); box-shadow: var(--focus-ring); }
.search-clear {
  position: absolute; right: 0.75rem; top: 50%; transform: translateY(-50%);
  background: none; border: none; cursor: pointer; color: var(--color-gray-400); font-size: 0.8rem;
}
.filter-select {
  padding: 0.55rem 0.85rem; border: 1px solid var(--color-gray-300);
  border-radius: var(--border-radius); font-size: 0.85rem; background: white;
  color: var(--color-gray-700); cursor: pointer;
}
.filter-select:focus { outline: none; border-color: var(--color-primary); }
.search-count { font-size: 0.82rem; color: var(--color-gray-500); white-space: nowrap; margin-left: auto; }

/* ── Table ── */
.table-card { padding: 0; overflow: hidden; }
.table-card table { margin: 0; border-radius: 0; }

.date-cell { display: flex; flex-direction: column; gap: 0.1rem; }
.date-day  { font-size: 0.85rem; font-weight: 500; color: var(--color-gray-800); }
.date-time { font-size: 0.75rem; color: var(--color-gray-400); font-family: monospace; }

.user-cell { display: flex; align-items: center; gap: 0.5rem; }
.mini-avatar {
  width: 26px; height: 26px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: white; font-size: 0.7rem; font-weight: 700; flex-shrink: 0;
}
.mono { font-family: monospace; font-size: 0.85rem; }

/* Badges action */
.badge-action {
  display: inline-block; padding: 0.22rem 0.65rem;
  border-radius: 20px; font-size: 0.75rem; font-weight: 600; white-space: nowrap;
}
.action-create { background: var(--color-success-light); color: #065f46; }
.action-update { background: #fff7ed; color: #92400e; }
.action-delete { background: var(--color-danger-light); color: #991b1b; }

/* Badge entité */
.badge-entite {
  display: inline-block; padding: 0.18rem 0.55rem;
  background: var(--color-gray-100); color: var(--color-gray-700);
  border-radius: 6px; font-size: 0.78rem; font-weight: 600;
}
.entite-id { font-size: 0.75rem; color: var(--color-gray-400); margin-left: 0.3rem; font-family: monospace; }

.details-cell { font-size: 0.85rem; color: var(--color-gray-600); max-width: 300px; }

.empty-state { padding: 2rem; }

@media (max-width: 768px) {
  .stats-bar { flex-direction: column; }
  .stat-item { border-right: none; border-bottom: 1px solid var(--color-gray-200); width: 100%; }
  .stat-item:last-child { border-bottom: none; }
  .filters-bar { flex-direction: column; align-items: stretch; }
  .search-input-wrap { max-width: 100%; }
}
</style>