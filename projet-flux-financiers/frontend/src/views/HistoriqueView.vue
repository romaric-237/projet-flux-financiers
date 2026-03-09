<template>
  <div class="page">

    <!-- En-tête -->
    <div class="page-header">
      <div>
        <h1 class="page-title">Historique</h1>
        <p class="page-subtitle">Toutes les transactions enregistrées</p>
      </div>
    </div>

    <!-- Stats globales -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-value text-success">{{ formatMontant(totalVersements) }}</span>
        <span class="stat-label">Total versements</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value text-danger">{{ formatMontant(totalCharges + totalPersonnel) }}</span>
        <span class="stat-label">Total dépenses</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value" :class="solde >= 0 ? 'text-success' : 'text-danger'">
          {{ formatMontant(solde) }}
        </span>
        <span class="stat-label">Solde</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value">{{ versements.length + paiementsCharges.length + paiementsEmployes.length }}</span>
        <span class="stat-label">Transactions</span>
      </div>
    </div>

    <!-- Onglets -->
    <div class="tabs-wrapper">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-btn', activeTab === tab.key ? 'tab-active' : '']"
        @click="activeTab = tab.key"
      >
        <span class="tab-icon">{{ tab.icon }}</span>
        {{ tab.label }}
        <span class="tab-count">{{ tabCount(tab.key) }}</span>
      </button>
    </div>

    <!-- Filtre + recherche -->
    <div class="search-bar">
      <div class="search-input-wrap">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input v-model="search" type="text" :placeholder="searchPlaceholder" class="search-input" />
        <button v-if="search" class="search-clear" @click="search = ''">✕</button>
      </div>
      <div class="filter-group">
        <select v-model="filtreMode" class="filter-select">
          <option value="">Tous les modes</option>
          <option value="ESPECES">Espèces</option>
          <option value="VIREMENT">Virement</option>
          <option value="CHEQUE">Chèque</option>
          <option value="CARTE_BANCAIRE">Carte bancaire</option>
        </select>
        <input v-model="filtreDateDebut" type="date" class="filter-date" title="Date début" />
        <input v-model="filtreDateFin" type="date" class="filter-date" title="Date fin" />
        <button v-if="filtreMode || filtreDateDebut || filtreDateFin" class="btn btn-secondary btn-sm" @click="resetFiltres">Réinitialiser</button>
      </div>
      <span class="search-count">{{ filteredData.length }} résultat(s)</span>
    </div>

    <!-- Tableau -->
    <div v-if="loading" class="spinner"></div>

    <div v-else class="card table-card">

      <!-- Versements -->
      <table v-if="activeTab === 'versements' && filteredData.length">
        <thead>
          <tr>
            <th>Client</th>
            <th>Montant TTC</th>
            <th>Date</th>
            <th>Mode</th>
            <th>Remarque</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="v in filteredData" :key="v.id">
            <td class="font-semibold">{{ v.clientNom }}</td>
            <td class="montant-pos font-bold">{{ formatMontant(v.montantTTC) }}</td>
            <td>{{ formatDate(v.dateVersement) }}</td>
            <td><span class="mode-badge">{{ formatMode(v.modePaiement) }}</span></td>
            <td class="text-muted-sm">{{ v.remarque || '-' }}</td>
          </tr>
        </tbody>
        <tfoot>
          <tr class="total-row">
            <td><strong>Total</strong></td>
            <td class="montant-pos font-bold">{{ formatMontant(filteredData.reduce((s,v)=>s+(v.montantTTC||0),0)) }}</td>
            <td colspan="3"></td>
          </tr>
        </tfoot>
      </table>

      <!-- Paiements Charges -->
      <table v-else-if="activeTab === 'charges' && filteredData.length">
        <thead>
          <tr>
            <th>Charge</th>
            <th>Montant</th>
            <th>Date</th>
            <th>Mode</th>
            <th>Remarque</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in filteredData" :key="p.id">
            <td class="font-semibold">{{ p.chargeNom }}</td>
            <td class="montant-neg font-bold">{{ formatMontant(p.montant) }}</td>
            <td>{{ formatDate(p.datePaiement) }}</td>
            <td><span class="mode-badge">{{ formatMode(p.modePaiement) }}</span></td>
            <td class="text-muted-sm">{{ p.remarque || '-' }}</td>
          </tr>
        </tbody>
        <tfoot>
          <tr class="total-row">
            <td><strong>Total</strong></td>
            <td class="montant-neg font-bold">{{ formatMontant(filteredData.reduce((s,p)=>s+(p.montant||0),0)) }}</td>
            <td colspan="3"></td>
          </tr>
        </tfoot>
      </table>

      <!-- Paiements Employés -->
      <table v-else-if="activeTab === 'employes' && filteredData.length">
        <thead>
          <tr>
            <th>Employé</th>
            <th>Type</th>
            <th>Montant</th>
            <th>Date</th>
            <th>Mode</th>
            <th>Remarque</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in filteredData" :key="p.id">
            <td class="font-semibold">{{ p.employeNom }}</td>
            <td>
              <span :class="p.typePaiement === 'SALAIRE' ? 'badge badge-success' : 'badge badge-secondary'">
                {{ p.typePaiement === 'SALAIRE' ? 'Salaire' : 'Prime' }}
              </span>
            </td>
            <td class="montant-neg font-bold">{{ formatMontant(p.montant) }}</td>
            <td>{{ formatDate(p.datePaiement) }}</td>
            <td><span class="mode-badge">{{ formatMode(p.modePaiement) }}</span></td>
            <td class="text-muted-sm">{{ p.remarque || '-' }}</td>
          </tr>
        </tbody>
        <tfoot>
          <tr class="total-row">
            <td colspan="2"><strong>Total</strong></td>
            <td class="montant-neg font-bold">{{ formatMontant(filteredData.reduce((s,p)=>s+(p.montant||0),0)) }}</td>
            <td colspan="3"></td>
          </tr>
        </tfoot>
      </table>

      <p v-if="!filteredData.length" class="text-muted text-center mt-2">Aucune donnée pour cette sélection.</p>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

const toast = useToast()

const tabs = [
  { key: 'versements', label: 'Versements clients',  icon: '💰' },
  { key: 'charges',    label: 'Paiements charges',   icon: '🏢' },
  { key: 'employes',   label: 'Paiements employés',  icon: '👤' }
]

const activeTab         = ref('versements')
const loading           = ref(false)
const search            = ref('')
const filtreMode        = ref('')
const filtreDateDebut   = ref('')
const filtreDateFin     = ref('')

const versements       = ref([])
const paiementsCharges = ref([])
const paiementsEmployes = ref([])

onMounted(loadAll)

async function loadAll() {
  loading.value = true
  try {
    const [v, pc, pe] = await Promise.all([
      api.get('/versements'),
      api.get('/paiement-charges'),
      api.get('/paiement-employes')
    ])
    versements.value        = v.data.sort((a,b) => b.dateVersement?.localeCompare(a.dateVersement))
    paiementsCharges.value  = pc.data.sort((a,b) => b.datePaiement?.localeCompare(a.datePaiement))
    paiementsEmployes.value = pe.data.sort((a,b) => b.datePaiement?.localeCompare(a.datePaiement))
  } catch {
    toast.error('Impossible de charger l\'historique')
  } finally {
    loading.value = false
  }
}

// ── Computed ──────────────────────────────────────
const totalVersements = computed(() => versements.value.reduce((s,v) => s + (v.montantTTC || 0), 0))
const totalCharges    = computed(() => paiementsCharges.value.reduce((s,p) => s + (p.montant || 0), 0))
const totalPersonnel  = computed(() => paiementsEmployes.value.reduce((s,p) => s + (p.montant || 0), 0))
const solde           = computed(() => totalVersements.value - totalCharges.value - totalPersonnel.value)

function tabCount(key) {
  if (key === 'versements') return versements.value.length
  if (key === 'charges')    return paiementsCharges.value.length
  return paiementsEmployes.value.length
}

const searchPlaceholder = computed(() => {
  if (activeTab.value === 'versements') return 'Rechercher par client...'
  if (activeTab.value === 'charges')    return 'Rechercher par charge...'
  return 'Rechercher par employé...'
})

const filteredData = computed(() => {
  let list = []
  if (activeTab.value === 'versements')  list = [...versements.value]
  if (activeTab.value === 'charges')     list = [...paiementsCharges.value]
  if (activeTab.value === 'employes')    list = [...paiementsEmployes.value]

  const q = search.value.trim().toLowerCase()
  if (q) {
    list = list.filter(item => {
      const name = item.clientNom || item.chargeNom || item.employeNom || ''
      return name.toLowerCase().includes(q)
    })
  }

  if (filtreMode.value) {
    list = list.filter(item => item.modePaiement === filtreMode.value)
  }

  if (filtreDateDebut.value) {
    list = list.filter(item => {
      const d = item.dateVersement || item.datePaiement || ''
      return d >= filtreDateDebut.value
    })
  }
  if (filtreDateFin.value) {
    list = list.filter(item => {
      const d = item.dateVersement || item.datePaiement || ''
      return d <= filtreDateFin.value
    })
  }

  return list
})

function resetFiltres() {
  filtreMode.value = ''
  filtreDateDebut.value = ''
  filtreDateFin.value = ''
}

// ── Utilitaires ───────────────────────────────────
function formatMontant(m) {
  return new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' }).format(m || 0)
}
function formatDate(d) {
  if (!d) return '-'
  return new Date(d).toLocaleDateString('fr-BE', { day: '2-digit', month: 'short', year: 'numeric' })
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
.page-header { margin-bottom: 1.25rem; }
.page-title { font-size: 1.75rem; font-weight: 700; color: var(--color-gray-900); margin-bottom: 0.1rem; }
.page-subtitle { font-size: 0.875rem; color: var(--color-gray-500); }

/* ── Stats bar ── */
.stats-bar {
  display: flex; align-items: center; gap: 2rem;
  background: white; border-radius: 12px; padding: 1rem 1.5rem;
  box-shadow: var(--shadow-sm); margin-bottom: 1.25rem; flex-wrap: wrap;
}
.stat-item { display: flex; flex-direction: column; }
.stat-value { font-size: 1.3rem; font-weight: 700; color: var(--color-gray-900); }
.stat-label { font-size: 0.75rem; color: var(--color-gray-500); text-transform: uppercase; letter-spacing: 0.04em; }
.stat-divider { width: 1px; height: 40px; background: var(--color-gray-200); }
.text-success { color: var(--color-success); }
.text-danger  { color: var(--color-danger); }

/* ── Onglets ── */
.tabs-wrapper {
  display: flex; gap: 0.25rem;
  background: var(--color-gray-200);
  border-radius: 10px; padding: 0.25rem;
  margin-bottom: 1rem; width: fit-content;
}
.tab-btn {
  display: flex; align-items: center; gap: 0.4rem;
  padding: 0.5rem 1.1rem; border: none; border-radius: 8px;
  background: transparent; cursor: pointer;
  font-size: 0.9rem; color: var(--color-gray-600);
  transition: all 0.15s; font-weight: 500;
}
.tab-btn:hover { color: var(--color-gray-900); }
.tab-active { background: white; color: var(--color-primary); box-shadow: var(--shadow-sm); }
.tab-icon { font-size: 1rem; }
.tab-count {
  background: var(--color-gray-200); color: var(--color-gray-600);
  border-radius: 10px; padding: 0.05rem 0.45rem; font-size: 0.75rem; font-weight: 600;
}
.tab-active .tab-count { background: var(--color-primary-light); color: var(--color-primary); }

/* ── Filtre ── */
.search-bar {
  display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem; flex-wrap: wrap;
}
.search-input-wrap { position: relative; flex: 1; max-width: 300px; }
.search-icon {
  position: absolute; left: 0.75rem; top: 50%; transform: translateY(-50%);
  width: 18px; height: 18px; color: var(--color-gray-500); pointer-events: none;
}
.search-input {
  width: 100%; padding: 0.5rem 2rem 0.5rem 2.25rem;
  border: 1px solid var(--color-gray-300); border-radius: 8px;
  font-size: 0.9rem; background: white; transition: border-color 0.2s;
}
.search-input:focus { outline: none; border-color: var(--color-primary); }
.search-clear {
  position: absolute; right: 0.6rem; top: 50%; transform: translateY(-50%);
  background: none; border: none; cursor: pointer; color: var(--color-gray-400); font-size: 0.8rem; padding: 0;
}
.filter-group { display: flex; align-items: center; gap: 0.5rem; flex-wrap: wrap; }
.filter-select, .filter-date {
  padding: 0.45rem 0.75rem; border: 1px solid var(--color-gray-300);
  border-radius: 8px; font-size: 0.875rem; background: white; color: var(--color-gray-700);
  cursor: pointer;
}
.filter-select:focus, .filter-date:focus { outline: none; border-color: var(--color-primary); }
.search-count { font-size: 0.875rem; color: var(--color-gray-500); white-space: nowrap; }

/* ── Table ── */
.table-card { padding: 0; overflow: hidden; }
.table-card table { margin: 0; border-radius: 0; }

.font-semibold { font-weight: 600; }
.font-bold     { font-weight: 700; }
.montant-pos   { color: var(--color-success); }
.montant-neg   { color: var(--color-danger); }
.text-muted-sm { font-size: 0.875rem; color: var(--color-gray-500); }

.mode-badge {
  display: inline-block; padding: 0.15rem 0.6rem;
  background: var(--color-gray-100); border-radius: 20px;
  font-size: 0.8rem; color: var(--color-gray-600); font-weight: 500;
}

tfoot .total-row td {
  background: var(--color-gray-100);
  padding: 0.75rem 1rem;
  border-top: 2px solid var(--color-gray-300);
  font-size: 0.95rem;
}
</style>
