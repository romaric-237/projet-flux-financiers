<template>
  <div class="page">

    <!-- En-tête -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Clients</h1>
        <p class="page-subtitle">Gestion de la clientèle</p>
      </div>
      <button class="btn btn-primary" @click="openForm()">+ Nouveau client</button>
    </div>

    <!-- Stats -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-value">{{ clients.length }}</span>
        <span class="stat-label">Total clients</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value">{{ clientsCeMois }}</span>
        <span class="stat-label">Nouveaux ce mois</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value">{{ formatMontant(totalVersements) }}</span>
        <span class="stat-label">Total versements</span>
      </div>
    </div>

    <!-- Recherche -->
    <div class="search-bar">
      <div class="search-input-wrap">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input v-model="search" type="text" placeholder="Rechercher par nom ou prénom..." class="search-input" />
        <button v-if="search" class="search-clear" @click="search = ''">✕</button>
      </div>
      <span class="search-count">{{ filteredClients.length }} / {{ clients.length }} client(s)</span>
    </div>

    <!-- Tableau -->
    <div v-if="loading" class="spinner"></div>

    <div v-else class="card table-card">
      <table v-if="filteredClients.length">
        <thead>
          <tr>
            <th>Client</th>
            <th>Versements</th>
            <th>Total versé</th>
            <th>Créé le</th>
            <th>Créé par</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="client in filteredClients" :key="client.id">
            <td>
              <div class="client-cell">
                <div class="avatar" :style="{ background: avatarColor(client.nom) }">
                  {{ initiales(client.nom, client.prenom) }}
                </div>
                <div>
                  <p class="client-name">{{ client.nom }} {{ client.prenom }}</p>
                </div>
              </div>
            </td>
            <td>
              <span class="badge badge-secondary">{{ versementsParClient(client.id).length }}</span>
            </td>
            <td class="montant-pos font-bold">
              {{ formatMontant(totalParClient(client.id)) }}
            </td>
            <td class="text-muted-sm">{{ formatDate(client.createdAt) }}</td>
            <td class="text-muted-sm">{{ client.createdByUsername }}</td>
            <td>
              <div class="d-flex gap-1">
                <button class="btn btn-info btn-sm" @click="openVersements(client)">Voir</button>
                <button class="btn btn-secondary btn-sm" @click="openForm(client)">Modifier</button>
                <button class="btn btn-danger btn-sm" @click="remove(client.id)">Supprimer</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucun client trouvé.</p>
    </div>

    <!-- Modal : Formulaire client -->
    <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
      <div class="modal-card">
        <div class="modal-header">
          <h3>{{ editingId ? 'Modifier le client' : 'Nouveau client' }}</h3>
          <button class="modal-close" @click="closeForm">✕</button>
        </div>
        <form @submit.prevent="save">
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
          <div class="modal-actions">
            <button type="submit" class="btn btn-primary" :disabled="saving">
              {{ saving ? 'Enregistrement...' : 'Enregistrer' }}
            </button>
            <button type="button" class="btn btn-secondary" @click="closeForm">Annuler</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal : Historique versements du client -->
    <div v-if="showVersements" class="modal-overlay" @click.self="showVersements = false">
      <div class="modal-card modal-lg">
        <div class="modal-header">
          <div>
            <h3>Versements — {{ selectedClient?.nom }} {{ selectedClient?.prenom }}</h3>
            <p class="modal-subtitle">{{ versementsClient.length }} versement(s)</p>
          </div>
          <button class="modal-close" @click="showVersements = false">✕</button>
        </div>

        <table v-if="versementsClient.length">
          <thead>
            <tr>
              <th>Date</th>
              <th>Montant TTC</th>
              <th>Mode</th>
              <th>Remarque</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="v in versementsClient" :key="v.id">
              <td>{{ formatDate(v.dateVersement) }}</td>
              <td class="montant-pos font-bold">{{ formatMontant(v.montantTTC) }}</td>
              <td>{{ formatMode(v.modePaiement) }}</td>
              <td class="text-muted-sm">{{ v.remarque || '-' }}</td>
            </tr>
          </tbody>
        </table>
        <p v-else class="text-muted text-center mt-2">Aucun versement pour ce client.</p>

        <div v-if="versementsClient.length" class="versements-total">
          <span>Total versé</span>
          <span class="montant-pos font-bold">{{ formatMontant(totalParClient(selectedClient?.id)) }}</span>
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

const toast = useToast()
const authStore = useAuthStore()

const clients = ref([])
const versements = ref([])
const search = ref('')
const loading = ref(false)
const showForm = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = ref({ nom: '', prenom: '' })
const errors = ref({})

// Modal versements
const showVersements = ref(false)
const selectedClient = ref(null)

onMounted(loadAll)

async function loadAll() {
  loading.value = true
  try {
    const [c, v] = await Promise.all([api.get('/clients'), api.get('/versements')])
    clients.value = c.data
    versements.value = v.data
  } catch {
    toast.error('Impossible de charger les données')
  } finally {
    loading.value = false
  }
}

// ── Computed ──────────────────────────────────────
const filteredClients = computed(() => {
  const q = search.value.trim().toLowerCase()
  if (!q) return clients.value
  return clients.value.filter(c =>
    c.nom.toLowerCase().includes(q) || c.prenom.toLowerCase().includes(q)
  )
})

const clientsCeMois = computed(() => {
  const now = new Date()
  return clients.value.filter(c => {
    if (!c.createdAt) return false
    const d = new Date(c.createdAt)
    return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear()
  }).length
})

const totalVersements = computed(() =>
  versements.value.reduce((s, v) => s + (v.montantTTC || 0), 0)
)

function versementsParClient(clientId) {
  return versements.value.filter(v => v.clientId === clientId)
}

function totalParClient(clientId) {
  return versementsParClient(clientId).reduce((s, v) => s + (v.montantTTC || 0), 0)
}

const versementsClient = computed(() =>
  selectedClient.value ? versementsParClient(selectedClient.value.id) : []
)

// ── Actions ───────────────────────────────────────
function openVersements(client) {
  selectedClient.value = client
  showVersements.value = true
}

function openForm(client = null) {
  errors.value = {}
  if (client) {
    editingId.value = client.id
    form.value = { nom: client.nom, prenom: client.prenom }
  } else {
    editingId.value = null
    form.value = { nom: '', prenom: '' }
  }
  showForm.value = true
}

function closeForm() { showForm.value = false }

function validate() {
  const e = {}
  if (!form.value.nom.trim())    e.nom    = 'Le nom est obligatoire'
  if (!form.value.prenom.trim()) e.prenom = 'Le prénom est obligatoire'
  errors.value = e
  return Object.keys(e).length === 0
}

async function save() {
  if (!validate()) return
  saving.value = true
  const payload = { nom: form.value.nom, prenom: form.value.prenom, createdById: authStore.currentUser?.id }
  try {
    if (editingId.value) {
      await api.put(`/clients/${editingId.value}`, payload)
      toast.success('Client modifié')
    } else {
      await api.post('/clients', payload)
      toast.success('Client créé')
    }
    closeForm()
    await loadAll()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Erreur lors de l\'enregistrement')
  } finally {
    saving.value = false
  }
}

async function remove(id) {
  if (!confirm('Supprimer ce client ?')) return
  try {
    await api.delete(`/clients/${id}`)
    toast.success('Client supprimé')
    await loadAll()
  } catch {
    toast.error('Impossible de supprimer ce client')
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

function formatMontant(m) {
  return new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' }).format(m || 0)
}

function formatDate(dt) {
  if (!dt) return '-'
  return new Date(dt).toLocaleDateString('fr-BE', { day: '2-digit', month: 'short', year: 'numeric' })
}

function formatMode(mode) {
  const labels = { ESPECES: 'Espèces', VIREMENT: 'Virement', CHEQUE: 'Chèque', CARTE_BANCAIRE: 'Carte bancaire' }
  return labels[mode] || mode || '-'
}
</script>

<style scoped>
.page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem 2rem;
}

/* ── En-tête ── */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.25rem;
}
.page-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-gray-900);
  margin-bottom: 0.1rem;
}
.page-subtitle {
  font-size: 0.875rem;
  color: var(--color-gray-500);
}

/* ── Stats bar ── */
.stats-bar {
  display: flex;
  align-items: center;
  gap: 2rem;
  background: white;
  border-radius: 12px;
  padding: 1rem 1.5rem;
  box-shadow: var(--shadow-sm);
  margin-bottom: 1.25rem;
}
.stat-item { display: flex; flex-direction: column; }
.stat-value { font-size: 1.4rem; font-weight: 700; color: var(--color-gray-900); }
.stat-label { font-size: 0.78rem; color: var(--color-gray-500); text-transform: uppercase; letter-spacing: 0.04em; }
.stat-divider { width: 1px; height: 40px; background: var(--color-gray-200); }

/* ── Recherche ── */
.search-bar {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}
.search-input-wrap {
  position: relative;
  flex: 1;
  max-width: 400px;
}
.search-icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: var(--color-gray-500);
  pointer-events: none;
}
.search-input {
  width: 100%;
  padding: 0.5rem 2.5rem 0.5rem 2.25rem;
  border: 1px solid var(--color-gray-300);
  border-radius: 8px;
  font-size: 0.95rem;
  background: white;
  transition: border-color 0.2s;
}
.search-input:focus { outline: none; border-color: var(--color-primary); }
.search-clear {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-gray-500);
  font-size: 0.85rem;
  padding: 0;
}
.search-count { font-size: 0.875rem; color: var(--color-gray-500); white-space: nowrap; }

/* ── Table ── */
.table-card { padding: 0; overflow: hidden; }
.table-card table { margin: 0; border-radius: 0; }

.client-cell { display: flex; align-items: center; gap: 0.75rem; }
.avatar {
  width: 38px; height: 38px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: white;
  font-size: 0.85rem;
  font-weight: 700;
  flex-shrink: 0;
}
.client-name { font-weight: 600; color: var(--color-gray-800); font-size: 0.95rem; }

.text-muted-sm { font-size: 0.875rem; color: var(--color-gray-500); }
.montant-pos { color: var(--color-success); }
.font-bold { font-weight: 600; }

/* ── Bouton info ── */
.btn-info {
  background-color: var(--color-info);
  color: white;
}
.btn-info:hover { background-color: #138496; }

/* ── Modals ── */
.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex; align-items: center; justify-content: center;
  z-index: 2000;
  padding: 1rem;
}
.modal-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  width: 100%;
  max-width: 480px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
}
.modal-lg { max-width: 680px; }

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.25rem;
}
.modal-header h3 { margin-bottom: 0.2rem; }
.modal-subtitle { font-size: 0.85rem; color: var(--color-gray-500); }
.modal-close {
  background: none; border: none; cursor: pointer;
  font-size: 1.1rem; color: var(--color-gray-500);
  padding: 0.1rem 0.3rem;
  border-radius: 4px;
  line-height: 1;
}
.modal-close:hover { background: var(--color-gray-100); }

.modal-actions { display: flex; gap: 0.75rem; margin-top: 1.25rem; }

.versements-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background: var(--color-gray-100);
  border-radius: 8px;
  margin-top: 1rem;
  font-size: 0.95rem;
  color: var(--color-gray-700);
}
</style>
