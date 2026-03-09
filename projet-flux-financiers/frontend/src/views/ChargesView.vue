<template>
  <div class="page">

    <!-- En-tête -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Charges</h1>
        <p class="page-subtitle">Référentiel des charges</p>
      </div>
      <button class="btn btn-primary" @click="openForm()">+ Nouvelle charge</button>
    </div>

    <!-- Stats -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-value">{{ charges.length }}</span>
        <span class="stat-label">Total charges</span>
      </div>
      <div class="stat-divider"></div>
      <div v-for="t in TYPES" :key="t.value" class="stat-item">
        <span class="stat-value" :style="{ color: t.color }">{{ countByType(t.value) }}</span>
        <span class="stat-label">{{ t.label }}</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value">{{ formatMontant(totalPaiements) }}</span>
        <span class="stat-label">Total payé</span>
      </div>
    </div>

    <!-- Recherche + filtre -->
    <div class="search-bar">
      <div class="search-input-wrap">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input v-model="search" type="text" placeholder="Rechercher par nom..." class="search-input" />
        <button v-if="search" class="search-clear" @click="search = ''">✕</button>
      </div>
      <div class="filter-group">
        <button :class="['filter-btn', filtreType === '' ? 'active' : '']" @click="filtreType = ''">Tous</button>
        <button
          v-for="t in TYPES" :key="t.value"
          :class="['filter-btn', filtreType === t.value ? 'active' : '']"
          :style="filtreType === t.value ? { background: t.color, borderColor: t.color } : {}"
          @click="filtreType = t.value"
        >{{ t.label }}</button>
      </div>
      <span class="search-count">{{ filteredCharges.length }} / {{ charges.length }} charge(s)</span>
    </div>

    <!-- Tableau -->
    <div v-if="loading" class="spinner"></div>

    <div v-else class="card table-card">
      <table v-if="filteredCharges.length">
        <thead>
          <tr>
            <th>Nom de la charge</th>
            <th>Type</th>
            <th>Paiements</th>
            <th>Total payé</th>
            <th>Créé le</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="charge in filteredCharges" :key="charge.id">
            <td>
              <div class="charge-cell">
                <div class="type-icon" :style="{ background: typeMeta(charge.typeCharge).bg, color: typeMeta(charge.typeCharge).color }">
                  <span>{{ typeMeta(charge.typeCharge).icon }}</span>
                </div>
                <span class="charge-name">{{ charge.nomCharge }}</span>
              </div>
            </td>
            <td>
              <span class="badge-type" :style="{ background: typeMeta(charge.typeCharge).bg, color: typeMeta(charge.typeCharge).color }">
                {{ typeMeta(charge.typeCharge).label }}
              </span>
            </td>
            <td>
              <span class="badge badge-secondary">{{ paiementsParCharge(charge.id).length }}</span>
            </td>
            <td class="montant-red font-bold">
              {{ formatMontant(totalParCharge(charge.id)) }}
            </td>
            <td class="text-muted-sm">{{ formatDate(charge.createdAt) }}</td>
            <td>
              <div class="d-flex gap-1">
                <button class="btn btn-info btn-sm" @click="openPaiements(charge)">Voir</button>
                <button class="btn btn-secondary btn-sm" @click="openForm(charge)">Modifier</button>
                <button class="btn btn-danger btn-sm" @click="remove(charge.id)">Supprimer</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucune charge trouvée.</p>
    </div>

    <!-- Modal : Formulaire charge -->
    <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
      <div class="modal-card">
        <div class="modal-header">
          <h3>{{ editingId ? 'Modifier la charge' : 'Nouvelle charge' }}</h3>
          <button class="modal-close" @click="closeForm">✕</button>
        </div>
        <form @submit.prevent="save">
          <div class="form-group">
            <label>Nom de la charge *</label>
            <input v-model="form.nomCharge" type="text" :class="{ error: errors.nomCharge }" placeholder="Ex: Essence Ford" />
            <span v-if="errors.nomCharge" class="error-message">{{ errors.nomCharge }}</span>
          </div>
          <div class="form-group">
            <label>Type *</label>
            <select v-model="form.typeCharge" :class="{ error: errors.typeCharge }">
              <option value="">-- Sélectionner --</option>
              <option v-for="t in TYPES" :key="t.value" :value="t.value">{{ t.label }}</option>
            </select>
            <span v-if="errors.typeCharge" class="error-message">{{ errors.typeCharge }}</span>
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

    <!-- Modal : Historique paiements de la charge -->
    <div v-if="showPaiements" class="modal-overlay" @click.self="showPaiements = false">
      <div class="modal-card modal-lg">
        <div class="modal-header">
          <div class="d-flex align-center gap-2">
            <div class="type-icon type-icon-lg" :style="{ background: typeMeta(selectedCharge?.typeCharge).bg, color: typeMeta(selectedCharge?.typeCharge).color }">
              {{ typeMeta(selectedCharge?.typeCharge).icon }}
            </div>
            <div>
              <h3>{{ selectedCharge?.nomCharge }}</h3>
              <p class="modal-subtitle">
                <span class="badge-type" :style="{ background: typeMeta(selectedCharge?.typeCharge).bg, color: typeMeta(selectedCharge?.typeCharge).color }">
                  {{ typeMeta(selectedCharge?.typeCharge).label }}
                </span>
                &nbsp;· {{ paiementsCharge.length }} paiement(s)
              </p>
            </div>
          </div>
          <button class="modal-close" @click="showPaiements = false">✕</button>
        </div>

        <table v-if="paiementsCharge.length">
          <thead>
            <tr>
              <th>Date</th>
              <th>Montant</th>
              <th>Mode</th>
              <th>Remarque</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="p in paiementsCharge" :key="p.id">
              <td>{{ formatDate(p.datePaiement) }}</td>
              <td class="montant-red font-bold">{{ formatMontant(p.montant) }}</td>
              <td>{{ formatMode(p.modePaiement) }}</td>
              <td class="text-muted-sm">{{ p.remarque || '-' }}</td>
            </tr>
          </tbody>
        </table>
        <p v-else class="text-muted text-center mt-2">Aucun paiement pour cette charge.</p>

        <div v-if="paiementsCharge.length" class="paiements-total">
          <span>Total payé</span>
          <span class="montant-red font-bold">{{ formatMontant(totalParCharge(selectedCharge?.id)) }}</span>
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

const TYPES = [
  { value: 'VEHICULES',         label: 'Véhicules',          color: '#1e88e5', bg: '#e3f2fd', icon: '🚗' },
  { value: 'INFRASTRUCTURE',    label: 'Infrastructure',     color: '#00897b', bg: '#e0f2f1', icon: '🏢' },
  { value: 'FISCALES_SOCIALES', label: 'Fiscales & Sociales',color: '#8e24aa', bg: '#f3e5f5', icon: '📋' },
  { value: 'SERVICES_EXTERNES', label: 'Services Externes',  color: '#f59e0b', bg: '#fffde7', icon: '🔧' },
]

const charges  = ref([])
const paiements = ref([])
const search   = ref('')
const filtreType = ref('')
const loading  = ref(false)
const showForm = ref(false)
const saving   = ref(false)
const editingId = ref(null)
const form     = ref({ nomCharge: '', typeCharge: '' })
const errors   = ref({})

const showPaiements  = ref(false)
const selectedCharge = ref(null)

onMounted(loadAll)

async function loadAll() {
  loading.value = true
  try {
    const [c, p] = await Promise.all([api.get('/charges'), api.get('/paiement-charges')])
    charges.value  = c.data
    paiements.value = p.data
  } catch {
    toast.error('Impossible de charger les données')
  } finally {
    loading.value = false
  }
}

// ── Computed ──────────────────────────────────────
const filteredCharges = computed(() => {
  let list = charges.value
  if (filtreType.value) list = list.filter(c => c.typeCharge === filtreType.value)
  const q = search.value.trim().toLowerCase()
  if (q) list = list.filter(c => c.nomCharge.toLowerCase().includes(q))
  return list
})

const totalPaiements = computed(() => paiements.value.reduce((s, p) => s + (p.montant || 0), 0))

function countByType(type) {
  return charges.value.filter(c => c.typeCharge === type).length
}
function paiementsParCharge(chargeId) {
  return paiements.value.filter(p => p.chargeId === chargeId)
}
function totalParCharge(chargeId) {
  return paiementsParCharge(chargeId).reduce((s, p) => s + (p.montant || 0), 0)
}

const paiementsCharge = computed(() =>
  selectedCharge.value ? paiementsParCharge(selectedCharge.value.id) : []
)

function typeMeta(type) {
  return TYPES.find(t => t.value === type) || { label: type, color: '#6c757d', bg: '#f0f0f0', icon: '📁' }
}

// ── Actions ───────────────────────────────────────
function openPaiements(charge) {
  selectedCharge.value = charge
  showPaiements.value = true
}

function openForm(charge = null) {
  errors.value = {}
  if (charge) {
    editingId.value = charge.id
    form.value = { nomCharge: charge.nomCharge, typeCharge: charge.typeCharge }
  } else {
    editingId.value = null
    form.value = { nomCharge: '', typeCharge: '' }
  }
  showForm.value = true
}

function closeForm() { showForm.value = false }

function validate() {
  const e = {}
  if (!form.value.nomCharge.trim()) e.nomCharge = 'Le nom est obligatoire'
  if (!form.value.typeCharge)       e.typeCharge = 'Le type est obligatoire'
  errors.value = e
  return Object.keys(e).length === 0
}

async function save() {
  if (!validate()) return
  saving.value = true
  const payload = { nomCharge: form.value.nomCharge, typeCharge: form.value.typeCharge, createdById: authStore.currentUser?.id }
  try {
    if (editingId.value) {
      await api.put(`/charges/${editingId.value}`, payload)
      toast.success('Charge modifiée')
    } else {
      await api.post('/charges', payload)
      toast.success('Charge créée')
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
  if (!confirm('Supprimer cette charge ?')) return
  try {
    await api.delete(`/charges/${id}`)
    toast.success('Charge supprimée')
    await loadAll()
  } catch {
    toast.error('Impossible de supprimer cette charge')
  }
}

// ── Utilitaires ───────────────────────────────────
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
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;
}
.page-title { font-size: 1.75rem; font-weight: 700; color: var(--color-gray-900); margin-bottom: 0.1rem; }
.page-subtitle { font-size: 0.875rem; color: var(--color-gray-500); }

/* ── Stats bar ── */
.stats-bar {
  display: flex; align-items: center; gap: 1.5rem;
  background: white; border-radius: 12px; padding: 1rem 1.5rem;
  box-shadow: var(--shadow-sm); margin-bottom: 1.25rem; flex-wrap: wrap;
}
.stat-item { display: flex; flex-direction: column; }
.stat-value { font-size: 1.4rem; font-weight: 700; color: var(--color-gray-900); }
.stat-label { font-size: 0.75rem; color: var(--color-gray-500); text-transform: uppercase; letter-spacing: 0.04em; }
.stat-divider { width: 1px; height: 40px; background: var(--color-gray-200); }

/* ── Recherche ── */
.search-bar {
  display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem; flex-wrap: wrap;
}
.search-input-wrap { position: relative; flex: 1; max-width: 320px; }
.search-icon {
  position: absolute; left: 0.75rem; top: 50%; transform: translateY(-50%);
  width: 18px; height: 18px; color: var(--color-gray-500); pointer-events: none;
}
.search-input {
  width: 100%; padding: 0.5rem 2.5rem 0.5rem 2.25rem;
  border: 1px solid var(--color-gray-300); border-radius: 8px;
  font-size: 0.95rem; background: white; transition: border-color 0.2s;
}
.search-input:focus { outline: none; border-color: var(--color-primary); }
.search-clear {
  position: absolute; right: 0.75rem; top: 50%; transform: translateY(-50%);
  background: none; border: none; cursor: pointer; color: var(--color-gray-500); font-size: 0.85rem; padding: 0;
}
.filter-group { display: flex; gap: 0.4rem; flex-wrap: wrap; }
.filter-btn {
  padding: 0.35rem 0.85rem; border: 1px solid var(--color-gray-300);
  border-radius: 20px; background: white; font-size: 0.82rem;
  cursor: pointer; transition: all 0.15s; color: var(--color-gray-700);
}
.filter-btn:hover { border-color: var(--color-primary); color: var(--color-primary); }
.filter-btn.active { background: var(--color-primary); color: white; border-color: var(--color-primary); }
.search-count { font-size: 0.875rem; color: var(--color-gray-500); white-space: nowrap; }

/* ── Table ── */
.table-card { padding: 0; overflow: hidden; }
.table-card table { margin: 0; border-radius: 0; }

.charge-cell { display: flex; align-items: center; gap: 0.75rem; }
.type-icon {
  width: 36px; height: 36px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  font-size: 1rem; flex-shrink: 0;
}
.type-icon-lg { width: 44px; height: 44px; font-size: 1.2rem; border-radius: 10px; }
.charge-name { font-weight: 600; color: var(--color-gray-800); font-size: 0.95rem; }

.badge-type {
  display: inline-block; padding: 0.2rem 0.65rem;
  border-radius: 20px; font-size: 0.78rem; font-weight: 500;
}

.text-muted-sm { font-size: 0.875rem; color: var(--color-gray-500); }
.montant-red { color: var(--color-danger); }
.font-bold { font-weight: 600; }

/* ── Bouton info ── */
.btn-info { background-color: var(--color-info); color: white; }
.btn-info:hover { background-color: #138496; }

/* ── Modals ── */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.45);
  display: flex; align-items: center; justify-content: center;
  z-index: 2000; padding: 1rem;
}
.modal-card {
  background: white; border-radius: 12px; padding: 1.5rem;
  width: 100%; max-width: 480px; max-height: 90vh; overflow-y: auto;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
}
.modal-lg { max-width: 680px; }
.modal-header {
  display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.25rem;
}
.modal-header h3 { margin-bottom: 0.2rem; }
.modal-subtitle { font-size: 0.85rem; color: var(--color-gray-500); margin-top: 0.3rem; }
.modal-close {
  background: none; border: none; cursor: pointer;
  font-size: 1.1rem; color: var(--color-gray-500);
  padding: 0.1rem 0.3rem; border-radius: 4px; line-height: 1;
}
.modal-close:hover { background: var(--color-gray-100); }
.modal-actions { display: flex; gap: 0.75rem; margin-top: 1.25rem; }

.paiements-total {
  display: flex; justify-content: space-between; align-items: center;
  padding: 0.75rem 1rem; background: var(--color-gray-100);
  border-radius: 8px; margin-top: 1rem;
  font-size: 0.95rem; color: var(--color-gray-700);
}
</style>
