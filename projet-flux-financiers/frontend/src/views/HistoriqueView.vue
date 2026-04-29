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
        <span class="stat-value text-success">{{ formatMontant(totalRecettes) }}</span>
        <span class="stat-label">Total recettes</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value text-danger">{{ formatMontant(totalDepenses) }}</span>
        <span class="stat-label">Total dépenses</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-value" :class="solde >= 0 ? 'text-success' : 'text-danger'">
          {{ solde >= 0 ? '+' : '' }}{{ formatMontant(solde) }}
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
      <button v-for="tab in tabs" :key="tab.key"
        :class="['tab-btn', activeTab === tab.key ? 'tab-active' : '']"
        @click="changeTab(tab.key)">
        <span class="tab-icon">{{ tab.icon }}</span>
        {{ tab.label }}
        <span class="tab-count">{{ tabCount(tab.key) }}</span>
      </button>
    </div>

    <!-- ══ FILTRES ══ -->
    <div class="search-bar">
      <!-- Recherche texte -->
      <div class="search-input-wrap">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input v-model="search" type="text" :placeholder="searchPlaceholder" class="search-input" />
        <button v-if="search" class="search-clear" @click="search = ''">✕</button>
      </div>

      <div class="filter-group">
        <!-- Filtre mode paiement -->
        <select v-model="filtreMode" class="filter-select">
          <option value="">Tous les modes</option>
          <option value="ESPECES">Espèces</option>
          <option value="VIREMENT">Virement</option>
          <option value="CHEQUE">Chèque</option>
          <option value="CARTE_BANCAIRE">Carte bancaire</option>
        </select>

        <!-- Filtre type dépense (onglet dépenses uniquement) -->
        <select v-if="activeTab === 'depenses'" v-model="filtreTypeDepense" class="filter-select">
          <option value="">Toutes les dépenses</option>
          <option value="charges">Charges uniquement</option>
          <option value="employes">Personnel uniquement</option>
        </select>

        <!-- Filtre montant min/max (onglet recettes) -->
        <template v-if="activeTab === 'recettes'">
          <input v-model.number="filtreMontantMin" type="number" min="0" step="0.01"
            placeholder="Montant min (€)" class="filter-input-num" />
          <input v-model.number="filtreMontantMax" type="number" min="0" step="0.01"
            placeholder="Montant max (€)" class="filter-input-num" />
        </template>

        <!-- Filtre dates -->
        <input v-model="filtreDateDebut" type="date" class="filter-date" title="Date début" />
        <input v-model="filtreDateFin"   type="date" class="filter-date" title="Date fin" />

        <button v-if="hasFiltres" class="btn btn-secondary btn-sm" @click="resetFiltres">Réinitialiser</button>
      </div>

      <span class="search-count">{{ filteredData.length }} résultat(s)</span>
    </div>

    <!-- ══ TABLEAUX ══ -->
    <div v-if="loading" class="spinner"></div>

    <div v-else class="card table-card">

      <!-- ── Recettes (versements) ── -->
      <table v-if="activeTab === 'recettes' && filteredData.length">
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
            <td><strong>Total période</strong></td>
            <td class="montant-pos font-bold">{{ formatMontant(filteredData.reduce((s,v)=>s+(v.montantTTC||0),0)) }}</td>
            <td colspan="3"></td>
          </tr>
        </tfoot>
      </table>

      <!-- ── Dépenses (charges + personnel combinés) ── -->
      <template v-if="activeTab === 'depenses' && filteredData.length">
        <!-- Répartition -->
        <div class="repartition-bar">
          <div class="rep-item">
            <span class="rep-dot" style="background:#e53935"></span>
            <span class="rep-label">Charges :</span>
            <span class="rep-val montant-neg">{{ formatMontant(totalChargesFiltrees) }}</span>
            <span class="rep-pct">({{ pctCharges }}%)</span>
          </div>
          <div class="rep-divider"></div>
          <div class="rep-item">
            <span class="rep-dot" style="background:#1e88e5"></span>
            <span class="rep-label">Personnel :</span>
            <span class="rep-val montant-neg">{{ formatMontant(totalPersonnelFiltree) }}</span>
            <span class="rep-pct">({{ pctPersonnel }}%)</span>
          </div>
        </div>

        <table>
          <thead>
            <tr>
              <th>Type</th>
              <th>Description</th>
              <th>Montant</th>
              <th>Date</th>
              <th>Mode</th>
              <th>Remarque</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredData" :key="item._uid">
              <td>
                <span :class="item._type === 'charge' ? 'badge badge-danger' : 'badge badge-secondary'">
                  {{ item._type === 'charge' ? 'Charge' : 'Personnel' }}
                </span>
              </td>
              <td class="font-semibold">{{ item._desc }}</td>
              <td class="montant-neg font-bold">{{ formatMontant(item.montant) }}</td>
              <td>{{ formatDate(item.datePaiement) }}</td>
              <td><span class="mode-badge">{{ formatMode(item.modePaiement) }}</span></td>
              <td class="text-muted-sm">{{ item.remarque || '-' }}</td>
            </tr>
          </tbody>
          <tfoot>
            <tr class="total-row">
              <td colspan="2"><strong>Total période</strong></td>
              <td class="montant-neg font-bold">{{ formatMontant(filteredData.reduce((s,p)=>s+(p.montant||0),0)) }}</td>
              <td colspan="3"></td>
            </tr>
          </tfoot>
        </table>
      </template>

      <!-- ── Global (toutes transactions) ── -->
      <template v-if="activeTab === 'global' && filteredData.length">
        <div class="solde-banner" :class="soldeFiltre >= 0 ? 'solde-pos' : 'solde-neg'">
          <span>Solde sur la sélection :</span>
          <strong>{{ soldeFiltre >= 0 ? '+' : '' }}{{ formatMontant(soldeFiltre) }}</strong>
        </div>
        <table>
          <thead>
            <tr>
              <th>Type</th>
              <th>Description</th>
              <th>Montant</th>
              <th>Date</th>
              <th>Mode</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredData" :key="item._uid">
              <td>
                <span :class="item._isRecette ? 'badge badge-success' : item._type === 'charge' ? 'badge badge-danger' : 'badge badge-secondary'">
                  {{ item._isRecette ? 'Versement' : item._type === 'charge' ? 'Charge' : 'Personnel' }}
                </span>
              </td>
              <td class="font-semibold">{{ item._desc }}</td>
              <td :class="item._isRecette ? 'montant-pos font-bold' : 'montant-neg font-bold'">
                {{ item._isRecette ? '+' : '-' }} {{ formatMontant(item._montant) }}
              </td>
              <td>{{ formatDate(item._date) }}</td>
              <td><span class="mode-badge">{{ formatMode(item.modePaiement) }}</span></td>
            </tr>
          </tbody>
        </table>
      </template>

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
  { key: 'recettes',  label: 'Recettes',  icon: '💰' },
  { key: 'depenses',  label: 'Dépenses',  icon: '💸' },
  { key: 'global',    label: 'Global',    icon: '📊' }
]

const activeTab          = ref('recettes')
const loading            = ref(false)
const search             = ref('')
const filtreMode         = ref('')
const filtreDateDebut    = ref('')
const filtreDateFin      = ref('')
const filtreTypeDepense  = ref('')
const filtreMontantMin   = ref('')
const filtreMontantMax   = ref('')

const versements        = ref([])
const paiementsCharges  = ref([])
const paiementsEmployes = ref([])

onMounted(loadAll)

function changeTab(key) {
  activeTab.value = key
  resetFiltres()
}

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

// ── Totaux globaux ─────────────────────────────────
const totalRecettes  = computed(() => versements.value.reduce((s,v) => s + (v.montantTTC || 0), 0))
const totalDepenses  = computed(() =>
  paiementsCharges.value.reduce((s,p) => s + (p.montant || 0), 0) +
  paiementsEmployes.value.reduce((s,p) => s + (p.montant || 0), 0)
)
const solde = computed(() => totalRecettes.value - totalDepenses.value)

// ── Compteurs onglets ──────────────────────────────
function tabCount(key) {
  if (key === 'recettes') return versements.value.length
  if (key === 'depenses') return paiementsCharges.value.length + paiementsEmployes.value.length
  return versements.value.length + paiementsCharges.value.length + paiementsEmployes.value.length
}

// ── Placeholder recherche ──────────────────────────
const searchPlaceholder = computed(() => {
  if (activeTab.value === 'recettes') return 'Rechercher par client...'
  if (activeTab.value === 'depenses') return 'Rechercher par charge ou employé...'
  return 'Rechercher...'
})

// ── Vérification filtres actifs ────────────────────
const hasFiltres = computed(() =>
  filtreMode.value || filtreDateDebut.value || filtreDateFin.value ||
  filtreTypeDepense.value || filtreMontantMin.value !== '' || filtreMontantMax.value !== ''
)

// ── Données filtrées ───────────────────────────────
const filteredData = computed(() => {
  let list = []

  if (activeTab.value === 'recettes') {
    list = versements.value.map(v => ({ ...v }))
    const q = search.value.trim().toLowerCase()
    if (q) list = list.filter(v => (v.clientNom || '').toLowerCase().includes(q))
    if (filtreMontantMin.value !== '') list = list.filter(v => (v.montantTTC || 0) >= filtreMontantMin.value)
    if (filtreMontantMax.value !== '') list = list.filter(v => (v.montantTTC || 0) <= filtreMontantMax.value)
    if (filtreMode.value) list = list.filter(v => v.modePaiement === filtreMode.value)
    if (filtreDateDebut.value) list = list.filter(v => v.dateVersement >= filtreDateDebut.value)
    if (filtreDateFin.value)   list = list.filter(v => v.dateVersement <= filtreDateFin.value)
  }

  if (activeTab.value === 'depenses') {
    const charges  = paiementsCharges.value.map(p => ({ ...p, _uid: `c-${p.id}`, _type: 'charge',   _desc: p.chargeNom }))
    const employes = paiementsEmployes.value.map(p => ({ ...p, _uid: `e-${p.id}`, _type: 'employe',  _desc: p.employeNom }))

    if (filtreTypeDepense.value === 'charges')       list = charges
    else if (filtreTypeDepense.value === 'employes') list = employes
    else list = [...charges, ...employes].sort((a,b) => b.datePaiement?.localeCompare(a.datePaiement))

    const q = search.value.trim().toLowerCase()
    if (q) list = list.filter(p => (p._desc || '').toLowerCase().includes(q))
    if (filtreMode.value) list = list.filter(p => p.modePaiement === filtreMode.value)
    if (filtreDateDebut.value) list = list.filter(p => p.datePaiement >= filtreDateDebut.value)
    if (filtreDateFin.value)   list = list.filter(p => p.datePaiement <= filtreDateFin.value)
  }

  if (activeTab.value === 'global') {
    const v  = versements.value.map(x => ({ ...x, _uid: `v-${x.id}`,  _isRecette: true,  _type: 'versement', _desc: x.clientNom,  _montant: x.montantTTC,  _date: x.dateVersement }))
    const pc = paiementsCharges.value.map(x => ({ ...x, _uid: `c-${x.id}`,  _isRecette: false, _type: 'charge',    _desc: x.chargeNom,  _montant: x.montant,     _date: x.datePaiement }))
    const pe = paiementsEmployes.value.map(x => ({ ...x, _uid: `e-${x.id}`,  _isRecette: false, _type: 'employe',   _desc: x.employeNom, _montant: x.montant,     _date: x.datePaiement }))
    list = [...v, ...pc, ...pe].sort((a,b) => b._date?.localeCompare(a._date))

    const q = search.value.trim().toLowerCase()
    if (q) list = list.filter(t => (t._desc || '').toLowerCase().includes(q))
    if (filtreMode.value) list = list.filter(t => t.modePaiement === filtreMode.value)
    if (filtreDateDebut.value) list = list.filter(t => t._date >= filtreDateDebut.value)
    if (filtreDateFin.value)   list = list.filter(t => t._date <= filtreDateFin.value)
  }

  return list
})

// ── Répartition dépenses filtrées ─────────────────
const totalChargesFiltrees = computed(() =>
  filteredData.value.filter(p => p._type === 'charge').reduce((s,p) => s + (p.montant || 0), 0)
)
const totalPersonnelFiltree = computed(() =>
  filteredData.value.filter(p => p._type === 'employe').reduce((s,p) => s + (p.montant || 0), 0)
)
const totalDepensesFiltrees = computed(() => totalChargesFiltrees.value + totalPersonnelFiltree.value)
const pctCharges   = computed(() => totalDepensesFiltrees.value > 0 ? ((totalChargesFiltrees.value / totalDepensesFiltrees.value) * 100).toFixed(1) : 0)
const pctPersonnel = computed(() => totalDepensesFiltrees.value > 0 ? ((totalPersonnelFiltree.value / totalDepensesFiltrees.value) * 100).toFixed(1) : 0)

// ── Solde global filtré ────────────────────────────
const soldeFiltre = computed(() => {
  const r = filteredData.value.filter(t => t._isRecette).reduce((s,t) => s + (t._montant || 0), 0)
  const d = filteredData.value.filter(t => !t._isRecette).reduce((s,t) => s + (t._montant || 0), 0)
  return r - d
})

function resetFiltres() {
  search.value = ''
  filtreMode.value = ''
  filtreDateDebut.value = ''
  filtreDateFin.value = ''
  filtreTypeDepense.value = ''
  filtreMontantMin.value = ''
  filtreMontantMax.value = ''
}

// ── Utilitaires ────────────────────────────────────
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
.page { max-width: 1200px; margin: 0 auto; padding: 0 1rem 2rem; }

/* ── En-tête ── */
.page-header   { margin-bottom: 1.25rem; }
.page-title    { font-size: 1.75rem; font-weight: 700; color: var(--color-gray-900); margin-bottom: 0.1rem; }
.page-subtitle { font-size: 0.875rem; color: var(--color-gray-500); }

/* ── Stats bar ── */
.stats-bar {
  display: flex; align-items: center; gap: 2rem;
  background: white; border-radius: 12px; padding: 1rem 1.5rem;
  box-shadow: var(--shadow-sm); margin-bottom: 1.25rem; flex-wrap: wrap;
}
.stat-item    { display: flex; flex-direction: column; }
.stat-value   { font-size: 1.3rem; font-weight: 700; color: var(--color-gray-900); }
.stat-label   { font-size: 0.75rem; color: var(--color-gray-500); text-transform: uppercase; letter-spacing: 0.04em; }
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
.tab-btn:hover   { color: var(--color-gray-900); }
.tab-active      { background: white; color: var(--color-primary); box-shadow: var(--shadow-sm); }
.tab-icon        { font-size: 1rem; }
.tab-count {
  background: var(--color-gray-200); color: var(--color-gray-600);
  border-radius: 10px; padding: 0.05rem 0.45rem; font-size: 0.75rem; font-weight: 600;
}
.tab-active .tab-count { background: var(--color-primary-light); color: var(--color-primary); }

/* ── Filtres ── */
.search-bar {
  display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem; flex-wrap: wrap;
}
.search-input-wrap { position: relative; flex: 1; max-width: 280px; }
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
.filter-group  { display: flex; align-items: center; gap: 0.5rem; flex-wrap: wrap; }
.filter-select, .filter-date, .filter-input-num {
  padding: 0.45rem 0.75rem; border: 1px solid var(--color-gray-300);
  border-radius: 8px; font-size: 0.875rem; background: white; color: var(--color-gray-700); cursor: pointer;
}
.filter-input-num { width: 130px; }
.filter-select:focus, .filter-date:focus, .filter-input-num:focus { outline: none; border-color: var(--color-primary); }
.search-count { font-size: 0.875rem; color: var(--color-gray-500); white-space: nowrap; }

/* ── Répartition dépenses ── */
.repartition-bar {
  display: flex; align-items: center; gap: 2rem;
  padding: 0.75rem 1rem; background: var(--color-gray-50);
  border-bottom: 1px solid var(--color-gray-200);
  flex-wrap: wrap;
}
.rep-item   { display: flex; align-items: center; gap: 0.4rem; font-size: 0.9rem; }
.rep-dot    { width: 10px; height: 10px; border-radius: 50%; display: inline-block; flex-shrink: 0; }
.rep-label  { color: var(--color-gray-600); }
.rep-val    { font-weight: 700; }
.rep-pct    { color: var(--color-gray-500); font-size: 0.82rem; }
.rep-divider { width: 1px; height: 24px; background: var(--color-gray-200); }

/* ── Solde global ── */
.solde-banner {
  display: flex; justify-content: space-between; align-items: center;
  padding: 0.7rem 1rem; font-size: 0.95rem;
  border-bottom: 1px solid var(--color-gray-200);
}
.solde-pos { background: #f0faf4; color: var(--color-success); }
.solde-neg { background: #fef2f2; color: var(--color-danger); }

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
  background: var(--color-gray-100); padding: 0.75rem 1rem;
  border-top: 2px solid var(--color-gray-300); font-size: 0.95rem;
}
</style>
