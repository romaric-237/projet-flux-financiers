<template>
  <div class="page">

    <!-- En-tête -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Employés</h1>
        <p class="page-subtitle">Gestion du personnel</p>
      </div>
      <button class="btn btn-primary" @click="openForm()">+ Nouvel employé</button>
    </div>

    <!-- Stats -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-value">{{ employes.length }}</span>
        <span class="stat-label">Total employés</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value text-success">{{ nbActifs }}</span>
        <span class="stat-label">Actifs</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value text-danger">{{ nbInactifs }}</span>
        <span class="stat-label">Inactifs</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value">{{ formatMontant(totalPaiements) }}</span>
        <span class="stat-label">Total versé au personnel</span>
      </div>
    </div>

    <!-- Recherche + filtre -->
    <div class="search-bar">
      <div class="search-input-wrap">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input v-model="search" type="text" placeholder="Rechercher par nom ou prénom..." class="search-input" />
        <button v-if="search" class="search-clear" @click="search = ''">✕</button>
      </div>
      <div class="filter-group">
        <button :class="['filter-btn', filtreStatut === '' ? 'active' : '']" @click="filtreStatut = ''">Tous</button>
        <button :class="['filter-btn', filtreStatut === 'ACTIF' ? 'active' : '']" @click="filtreStatut = 'ACTIF'">Actifs</button>
        <button :class="['filter-btn', filtreStatut === 'INACTIF' ? 'active' : '']" @click="filtreStatut = 'INACTIF'">Inactifs</button>
      </div>
      <span class="search-count">{{ filteredEmployes.length }} / {{ employes.length }} employé(s)</span>
    </div>

    <!-- Tableau -->
    <div v-if="loading" class="spinner"></div>

    <div v-else class="card table-card">
      <table v-if="filteredEmployes.length">
        <thead>
          <tr>
            <th>Employé</th>
            <th>Statut</th>
            <th>Paiements</th>
            <th>Total versé</th>
            <th>Créé le</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="emp in filteredEmployes" :key="emp.id">
            <td>
              <div class="employe-cell">
                <div class="avatar" :style="{ background: avatarColor(emp.nom) }">
                  {{ initiales(emp.nom, emp.prenom) }}
                </div>
                <div>
                  <p class="employe-name">{{ emp.nom }} {{ emp.prenom }}</p>
                </div>
              </div>
            </td>
            <td>
              <span :class="emp.statut === 'ACTIF' ? 'badge badge-success' : 'badge badge-danger'">
                {{ emp.statut === 'ACTIF' ? 'Actif' : 'Inactif' }}
              </span>
            </td>
            <td>
              <span class="badge badge-secondary">{{ paiementsParEmploye(emp.id).length }}</span>
            </td>
            <td class="montant-blue font-bold">
              {{ formatMontant(totalParEmploye(emp.id)) }}
            </td>
            <td class="text-muted-sm">{{ formatDate(emp.createdAt) }}</td>
            <td>
              <div class="d-flex gap-1">
                <button class="btn btn-info btn-sm" @click="openPaiements(emp)">Voir</button>
                <button class="btn btn-secondary btn-sm" @click="openForm(emp)">Modifier</button>
                <button class="btn btn-danger btn-sm" @click="remove(emp.id)">Supprimer</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucun employé trouvé.</p>
    </div>

    <!-- Modal : Formulaire employé -->
    <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
      <div class="modal-card">
        <div class="modal-header">
          <h3>{{ editingId ? 'Modifier l\'employé' : 'Nouvel employé' }}</h3>
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
          <div class="form-group">
            <label>Statut</label>
            <select v-model="form.statut">
              <option value="ACTIF">Actif</option>
              <option value="INACTIF">Inactif</option>
            </select>
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

    <!-- Modal : Historique paiements de l'employé -->
    <div v-if="showPaiements" class="modal-overlay" @click.self="showPaiements = false">
      <div class="modal-card modal-lg">
        <div class="modal-header">
          <div class="d-flex align-center gap-2">
            <div class="avatar avatar-lg" :style="{ background: avatarColor(selectedEmploye?.nom) }">
              {{ initiales(selectedEmploye?.nom, selectedEmploye?.prenom) }}
            </div>
            <div>
              <h3>{{ selectedEmploye?.nom }} {{ selectedEmploye?.prenom }}</h3>
              <p class="modal-subtitle">{{ paiementsEmploye.length }} paiement(s)</p>
            </div>
          </div>
          <button class="modal-close" @click="showPaiements = false">✕</button>
        </div>

        <table v-if="paiementsEmploye.length">
          <thead>
            <tr>
              <th>Date</th>
              <th>Type</th>
              <th>Montant</th>
              <th>Mode</th>
              <th>Remarque</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="p in paiementsEmploye" :key="p.id">
              <td>{{ formatDate(p.datePaiement) }}</td>
              <td>
                <span :class="p.typePaiement === 'SALAIRE' ? 'badge badge-success' : 'badge badge-secondary'">
                  {{ p.typePaiement === 'SALAIRE' ? 'Salaire' : 'Prime' }}
                </span>
              </td>
              <td class="montant-blue font-bold">{{ formatMontant(p.montant) }}</td>
              <td>{{ formatMode(p.modePaiement) }}</td>
              <td class="text-muted-sm">{{ p.remarque || '-' }}</td>
            </tr>
          </tbody>
        </table>
        <p v-else class="text-muted text-center mt-2">Aucun paiement pour cet employé.</p>

        <div v-if="paiementsEmploye.length" class="paiements-total">
          <span>Total versé</span>
          <span class="montant-blue font-bold">{{ formatMontant(totalParEmploye(selectedEmploye?.id)) }}</span>
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

const employes = ref([])
const paiements = ref([])
const search = ref('')
const filtreStatut = ref('')
const loading = ref(false)
const showForm = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = ref({ nom: '', prenom: '', statut: 'ACTIF' })
const errors = ref({})

// Modal paiements
const showPaiements = ref(false)
const selectedEmploye = ref(null)

onMounted(loadAll)

async function loadAll() {
  loading.value = true
  try {
    const [e, p] = await Promise.all([api.get('/employes'), api.get('/paiement-employes')])
    employes.value = e.data
    paiements.value = p.data
  } catch {
    toast.error('Impossible de charger les données')
  } finally {
    loading.value = false
  }
}

// ── Computed ──────────────────────────────────────
const filteredEmployes = computed(() => {
  let list = employes.value
  if (filtreStatut.value) list = list.filter(e => e.statut === filtreStatut.value)
  const q = search.value.trim().toLowerCase()
  if (q) list = list.filter(e => e.nom.toLowerCase().includes(q) || e.prenom.toLowerCase().includes(q))
  return list
})

const nbActifs   = computed(() => employes.value.filter(e => e.statut === 'ACTIF').length)
const nbInactifs = computed(() => employes.value.filter(e => e.statut === 'INACTIF').length)
const totalPaiements = computed(() => paiements.value.reduce((s, p) => s + (p.montant || 0), 0))

function paiementsParEmploye(employeId) {
  return paiements.value.filter(p => p.employeId === employeId)
}
function totalParEmploye(employeId) {
  return paiementsParEmploye(employeId).reduce((s, p) => s + (p.montant || 0), 0)
}

const paiementsEmploye = computed(() =>
  selectedEmploye.value ? paiementsParEmploye(selectedEmploye.value.id) : []
)

// ── Actions ───────────────────────────────────────
function openPaiements(emp) {
  selectedEmploye.value = emp
  showPaiements.value = true
}

function openForm(emp = null) {
  errors.value = {}
  if (emp) {
    editingId.value = emp.id
    form.value = { nom: emp.nom, prenom: emp.prenom, statut: emp.statut || 'ACTIF' }
  } else {
    editingId.value = null
    form.value = { nom: '', prenom: '', statut: 'ACTIF' }
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
  const payload = { nom: form.value.nom, prenom: form.value.prenom, statut: form.value.statut, createdById: authStore.currentUser?.id }
  try {
    if (editingId.value) {
      await api.put(`/employes/${editingId.value}`, payload)
      toast.success('Employé modifié')
    } else {
      await api.post('/employes', payload)
      toast.success('Employé créé')
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
  if (!confirm('Supprimer cet employé ?')) return
  try {
    await api.delete(`/employes/${id}`)
    toast.success('Employé supprimé')
    await loadAll()
  } catch {
    toast.error('Impossible de supprimer cet employé')
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
.page-title { font-size: 1.75rem; font-weight: 700; color: var(--color-gray-900); margin-bottom: 0.1rem; }
.page-subtitle { font-size: 0.875rem; color: var(--color-gray-500); }

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
.text-success { color: var(--color-success); }
.text-danger  { color: var(--color-danger); }

/* ── Recherche + filtre ── */
.search-bar {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}
.search-input-wrap {
  position: relative;
  flex: 1;
  max-width: 360px;
}
.search-icon {
  position: absolute; left: 0.75rem; top: 50%; transform: translateY(-50%);
  width: 18px; height: 18px; color: var(--color-gray-500); pointer-events: none;
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
  position: absolute; right: 0.75rem; top: 50%; transform: translateY(-50%);
  background: none; border: none; cursor: pointer; color: var(--color-gray-500); font-size: 0.85rem; padding: 0;
}
.filter-group { display: flex; gap: 0.4rem; }
.filter-btn {
  padding: 0.35rem 0.9rem;
  border: 1px solid var(--color-gray-300);
  border-radius: 20px;
  background: white;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.15s;
  color: var(--color-gray-700);
}
.filter-btn:hover { border-color: var(--color-primary); color: var(--color-primary); }
.filter-btn.active { background: var(--color-primary); color: white; border-color: var(--color-primary); }

.search-count { font-size: 0.875rem; color: var(--color-gray-500); white-space: nowrap; }

/* ── Table ── */
.table-card { padding: 0; overflow: hidden; }
.table-card table { margin: 0; border-radius: 0; }

.employe-cell { display: flex; align-items: center; gap: 0.75rem; }
.avatar {
  width: 38px; height: 38px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: white; font-size: 0.85rem; font-weight: 700; flex-shrink: 0;
}
.avatar-lg { width: 46px; height: 46px; font-size: 1rem; }
.employe-name { font-weight: 600; color: var(--color-gray-800); font-size: 0.95rem; }

.text-muted-sm { font-size: 0.875rem; color: var(--color-gray-500); }
.montant-blue { color: var(--color-info); }
.font-bold { font-weight: 600; }

/* ── Bouton info ── */
.btn-info { background-color: var(--color-info); color: white; }
.btn-info:hover { background-color: #138496; }

/* ── Modals ── */
.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex; align-items: center; justify-content: center;
  z-index: 2000; padding: 1rem;
}
.modal-card {
  background: white; border-radius: 12px; padding: 1.5rem;
  width: 100%; max-width: 480px; max-height: 90vh; overflow-y: auto;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
}
.modal-lg { max-width: 700px; }
.modal-header {
  display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.25rem;
}
.modal-header h3 { margin-bottom: 0.2rem; }
.modal-subtitle { font-size: 0.85rem; color: var(--color-gray-500); }
.modal-close {
  background: none; border: none; cursor: pointer;
  font-size: 1.1rem; color: var(--color-gray-500);
  padding: 0.1rem 0.3rem; border-radius: 4px; line-height: 1;
}
.modal-close:hover { background: var(--color-gray-100); }
.modal-actions { display: flex; gap: 0.75rem; margin-top: 1.25rem; }

.paiements-total {
  display: flex; justify-content: space-between; align-items: center;
  padding: 0.75rem 1rem;
  background: var(--color-gray-100);
  border-radius: 8px; margin-top: 1rem;
  font-size: 0.95rem; color: var(--color-gray-700);
}
</style>
