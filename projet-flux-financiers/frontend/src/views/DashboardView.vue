<template>
  <div class="dashboard">

    <!-- Bandeau de bienvenue -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <div>
          <p class="welcome-label">Bonjour,</p>
          <h1 class="welcome-name">{{ authStore.currentUser?.username }}</h1>
          <p class="welcome-date">{{ dateAujourdhui }}</p>
        </div>
        <div class="welcome-logo">
          <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="32" cy="32" r="32" fill="rgba(255,255,255,0.15)"/>
            <path d="M20 44V28l12-8 12 8v16H36v-8h-8v8H20Z" fill="white" opacity="0.9"/>
            <rect x="28" y="36" width="8" height="8" rx="1" fill="rgba(255,255,255,0.5)"/>
          </svg>
        </div>
      </div>
    </div>

    <!-- Actions rapides -->
    <h2 class="section-title">Actions rapides</h2>
    <div class="actions-grid">
      <router-link to="/versements?action=new" class="action-card action-green">
        <div class="action-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
        </div>
        <div>
          <p class="action-title">Nouveau versement</p>
          <p class="action-desc">Enregistrer un paiement client</p>
        </div>
      </router-link>

      <router-link to="/paiements-charge?action=new" class="action-card action-red">
        <div class="action-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
        </div>
        <div>
          <p class="action-title">Nouveau paiement charge</p>
          <p class="action-desc">Véhicule, infrastructure, fiscale…</p>
        </div>
      </router-link>

      <router-link to="/paiements-employe?action=new" class="action-card action-blue">
        <div class="action-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
        </div>
        <div>
          <p class="action-title">Nouveau paiement personnel</p>
          <p class="action-desc">Salaire, prime</p>
        </div>
      </router-link>
    </div>

    <!-- KPI Cards -->
    <div class="kpi-grid">
      <!-- Total Recettes -->
      <div class="kpi-card kpi-green">
        <div class="kpi-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
          </svg>
        </div>
        <div class="kpi-body">
          <p class="kpi-label">Total recettes</p>
          <p class="kpi-value">{{ formatMontant(kpi.totalRecettes) }}</p>
          <p class="kpi-sub">{{ kpi.nbVersements }} versement(s)</p>
        </div>
      </div>

      <!-- Total Dépenses -->
      <div class="kpi-card kpi-red">
        <div class="kpi-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="2" y="7" width="20" height="14" rx="2"/>
            <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
          </svg>
        </div>
        <div class="kpi-body">
          <p class="kpi-label">Total dépenses</p>
          <p class="kpi-value">{{ formatMontant(kpi.totalDepenses) }}</p>
          <p class="kpi-sub">{{ kpi.nbCharges + kpi.nbPersonnel }} paiement(s)</p>
        </div>
      </div>

      <!-- Solde -->
      <div class="kpi-card" :class="kpi.solde >= 0 ? 'kpi-green' : 'kpi-red'">
        <div class="kpi-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="1" x2="12" y2="23"/>
            <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
          </svg>
        </div>
        <div class="kpi-body">
          <p class="kpi-label">Solde</p>
          <p class="kpi-value" :class="kpi.solde >= 0 ? 'text-success' : 'text-danger'">
            {{ kpi.solde >= 0 ? '+' : '' }}{{ formatMontant(kpi.solde) }}
          </p>
          <p class="kpi-sub">Recettes - Dépenses</p>
        </div>
      </div>

      <!-- Variation -->
      <div class="kpi-card kpi-orange">
        <div class="kpi-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/>
            <polyline points="16 7 22 7 22 13"/>
          </svg>
        </div>
        <div class="kpi-body">
          <p class="kpi-label">Variation</p>
          <p class="kpi-value" :class="kpi.variation >= 0 ? 'text-success' : 'text-danger'">
            <span v-if="kpi.variation !== null">
              {{ kpi.variation >= 0 ? '+' : '' }}{{ kpi.variation }}%
            </span>
            <span v-else class="text-muted-sm">—</span>
          </p>
          <p class="kpi-sub">vs mois précédent</p>
        </div>
      </div>
    </div>

    <!-- Graphiques -->
    <div class="charts-grid">
      <!-- Donut : répartition dépenses Personnel vs Charges -->
      <div class="card chart-card">
        <h3 class="chart-title">Répartition des dépenses</h3>
        <div class="chart-wrapper">
          <Doughnut v-if="donutReady" :data="donutData" :options="donutOptions" />
          <p v-else class="text-muted text-center mt-2">Aucune donnée disponible.</p>
        </div>
      </div>

      <!-- Line : évolution recettes et dépenses 6 derniers mois -->
      <div class="card chart-card">
        <h3 class="chart-title">Évolution — 6 derniers mois</h3>
        <div class="chart-wrapper">
          <Line v-if="lineReady" :data="lineData" :options="lineOptions" />
          <p v-else class="text-muted text-center mt-2">Aucune donnée disponible.</p>
        </div>
      </div>
    </div>

    <!-- Dernières transactions -->
    <div class="section-header">
      <h2 class="section-title">Dernières transactions</h2>
      <div class="filtre-periode">
        <button
          v-for="p in periodes" :key="p.value"
          class="btn-filtre"
          :class="{ active: filtrePeriode === p.value }"
          @click="filtrePeriode = p.value"
        >{{ p.label }}</button>
      </div>
    </div>

    <div class="card">
      <div v-if="loading" class="spinner"></div>

      <table v-else-if="transactionsFiltrees.length">
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
          <tr v-for="t in transactionsFiltrees" :key="t.uid">
            <td><span :class="badgeClass(t.type)">{{ t.type }}</span></td>
            <td>{{ t.description }}</td>
            <td class="montant-cell" :class="t.type === 'Versement' ? 'montant-pos' : 'montant-neg'">
              {{ t.type === 'Versement' ? '+' : '-' }} {{ formatMontant(t.montant) }}
            </td>
            <td>{{ formatDate(t.date) }}</td>
            <td>{{ formatMode(t.mode) }}</td>
          </tr>
        </tbody>
      </table>

      <p v-else class="text-muted text-center mt-2">Aucune transaction sur cette période.</p>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Doughnut, Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Filler
} from 'chart.js'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, PointElement, LineElement, Title, Filler)

const authStore = useAuthStore()

const loading = ref(false)
const allTransactions = ref([])
const filtrePeriode = ref('mois')

const periodes = [
  { value: 'mois',      label: 'Ce mois' },
  { value: 'trimestre', label: 'Trimestre' },
  { value: 'annee',     label: 'Année' },
  { value: 'tout',      label: 'Tout' }
]

const kpi = ref({
  totalRecettes: 0, nbVersements: 0,
  totalDepenses: 0,
  totalCharges: 0,  nbCharges: 0,
  totalPersonnel: 0, nbPersonnel: 0,
  solde: 0,
  variation: null
})

const rawVersements    = ref([])
const rawPaiementsChar = ref([])
const rawPaiementsEmpl = ref([])

const dateAujourdhui = new Date().toLocaleDateString('fr-BE', {
  weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
})

// ── Filtre transactions ──────────────────────────────────
const transactionsFiltrees = computed(() => {
  const now = new Date()
  const debut = new Date()

  if (filtrePeriode.value === 'mois') {
    debut.setDate(1)
    debut.setHours(0, 0, 0, 0)
  } else if (filtrePeriode.value === 'trimestre') {
    debut.setMonth(now.getMonth() - 2)
    debut.setDate(1)
    debut.setHours(0, 0, 0, 0)
  } else if (filtrePeriode.value === 'annee') {
    debut.setMonth(0)
    debut.setDate(1)
    debut.setHours(0, 0, 0, 0)
  }

  let list = allTransactions.value
  if (filtrePeriode.value !== 'tout') {
    list = list.filter(t => new Date(t.date) >= debut)
  }
  return list.slice(0, 10)
})

// ── Donut : Personnel vs Charges ────────────────────────
const donutReady = computed(() =>
  kpi.value.totalCharges + kpi.value.totalPersonnel > 0
)

const donutData = computed(() => ({
  labels: ['Personnel', 'Charges'],
  datasets: [{
    data: [kpi.value.totalPersonnel, kpi.value.totalCharges],
    backgroundColor: ['#1e88e5', '#e53935'],
    borderWidth: 0,
    hoverOffset: 8
  }]
}))

const donutOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { position: 'bottom', labels: { padding: 16, font: { size: 13 } } },
    tooltip: {
      callbacks: {
        label: ctx => {
          const total = ctx.dataset.data.reduce((a, b) => a + b, 0)
          const pct = total > 0 ? ((ctx.parsed / total) * 100).toFixed(1) : 0
          return `  ${ctx.label} : ${new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' }).format(ctx.parsed)} (${pct}%)`
        }
      }
    }
  }
}

// ── Line : recettes et dépenses 6 mois ──────────────────
const lineReady = computed(() =>
  rawVersements.value.length > 0 || rawPaiementsChar.value.length > 0 || rawPaiementsEmpl.value.length > 0
)

const lineData = computed(() => {
  const now = new Date()
  const mois = []
  for (let i = 5; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
    mois.push({
      label: d.toLocaleDateString('fr-BE', { month: 'short', year: '2-digit' }),
      key: `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
    })
  }

  const recettes = mois.map(m =>
    rawVersements.value
      .filter(v => v.dateVersement?.startsWith(m.key))
      .reduce((s, v) => s + (v.montantTTC || 0), 0)
  )

  const depenses = mois.map(m => {
    const charges = rawPaiementsChar.value
      .filter(p => p.datePaiement?.startsWith(m.key))
      .reduce((s, p) => s + (p.montant || 0), 0)
    const personnel = rawPaiementsEmpl.value
      .filter(p => p.datePaiement?.startsWith(m.key))
      .reduce((s, p) => s + (p.montant || 0), 0)
    return charges + personnel
  })

  return {
    labels: mois.map(m => m.label),
    datasets: [
      {
        label: 'Recettes',
        data: recettes,
        borderColor: '#2E7D5E',
        backgroundColor: 'rgba(46, 125, 94, 0.08)',
        borderWidth: 2,
        pointRadius: 4,
        tension: 0.3,
        fill: true
      },
      {
        label: 'Dépenses',
        data: depenses,
        borderColor: '#e53935',
        backgroundColor: 'rgba(229, 57, 53, 0.08)',
        borderWidth: 2,
        pointRadius: 4,
        tension: 0.3,
        fill: true
      }
    ]
  }
})

const lineOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { position: 'bottom', labels: { padding: 16, font: { size: 13 } } },
    tooltip: {
      callbacks: {
        label: ctx => `  ${ctx.dataset.label} : ${new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' }).format(ctx.parsed.y)}`
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: val => new Intl.NumberFormat('fr-BE', { notation: 'compact' }).format(val) + ' €'
      },
      grid: { color: 'rgba(0,0,0,0.05)' }
    },
    x: { grid: { display: false } }
  }
}

// ── Chargement ───────────────────────────────────────────
onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const [v, pc, pe] = await Promise.all([
      api.get('/versements'),
      api.get('/paiement-charges'),
      api.get('/paiement-employes')
    ])

    rawVersements.value    = v.data
    rawPaiementsChar.value = pc.data
    rawPaiementsEmpl.value = pe.data

    const totalRecettes  = v.data.reduce((s, x) => s + (x.montantTTC || 0), 0)
    const totalCharges   = pc.data.reduce((s, x) => s + (x.montant || 0), 0)
    const totalPersonnel = pe.data.reduce((s, x) => s + (x.montant || 0), 0)
    const totalDepenses  = totalCharges + totalPersonnel
    const solde          = totalRecettes - totalDepenses

    // Variation vs mois précédent
    const now = new Date()
    const moisActuel = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
    const prev = new Date(now.getFullYear(), now.getMonth() - 1, 1)
    const moisPrec = `${prev.getFullYear()}-${String(prev.getMonth() + 1).padStart(2, '0')}`

    const soldeMoisActuel = calculerSoldeMois(v.data, pc.data, pe.data, moisActuel)
    const soldeMoisPrec   = calculerSoldeMois(v.data, pc.data, pe.data, moisPrec)

    let variation = null
    if (soldeMoisPrec !== 0) {
      variation = parseFloat(((soldeMoisActuel - soldeMoisPrec) / Math.abs(soldeMoisPrec) * 100).toFixed(1))
    } else if (soldeMoisActuel !== 0) {
      variation = 100
    }

    kpi.value = {
      totalRecettes, nbVersements: v.data.length,
      totalDepenses, totalCharges, nbCharges: pc.data.length,
      totalPersonnel, nbPersonnel: pe.data.length,
      solde, variation
    }

    const versements = v.data.map(x => ({
      uid: `v-${x.id}`, type: 'Versement',
      description: x.clientNom, montant: x.montantTTC,
      date: x.dateVersement, mode: x.modePaiement
    }))
    const paiementsCharges = pc.data.map(x => ({
      uid: `pc-${x.id}`, type: 'Charge',
      description: x.chargeNom, montant: x.montant,
      date: x.datePaiement, mode: x.modePaiement
    }))
    const paiementsEmployes = pe.data.map(x => ({
      uid: `pe-${x.id}`, type: 'Personnel',
      description: x.employeNom, montant: x.montant,
      date: x.datePaiement, mode: x.modePaiement
    }))

    allTransactions.value = [...versements, ...paiementsCharges, ...paiementsEmployes]
      .sort((a, b) => b.date.localeCompare(a.date))

  } catch {
    // silencieux
  } finally {
    loading.value = false
  }
}

function calculerSoldeMois(versements, charges, employes, moisKey) {
  const r = versements.filter(x => x.dateVersement?.startsWith(moisKey)).reduce((s, x) => s + (x.montantTTC || 0), 0)
  const d = [
    ...charges.filter(x => x.datePaiement?.startsWith(moisKey)),
    ...employes.filter(x => x.datePaiement?.startsWith(moisKey))
  ].reduce((s, x) => s + (x.montant || 0), 0)
  return r - d
}

function badgeClass(type) {
  if (type === 'Versement') return 'badge badge-success'
  if (type === 'Charge')    return 'badge badge-danger'
  return 'badge badge-secondary'
}

function formatMontant(m) {
  return new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' }).format(m || 0)
}

function formatDate(d) {
  if (!d) return '-'
  return new Date(d).toLocaleDateString('fr-BE', { day: '2-digit', month: 'short', year: 'numeric' })
}

function formatMode(mode) {
  const labels = { ESPECES: 'Espèces', VIREMENT: 'Virement', CHEQUE: 'Chèque', CARTE_BANCAIRE: 'Carte' }
  return labels[mode] || mode || '-'
}
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem 2rem;
}

/* ── Bandeau bienvenue ── */
.welcome-banner {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-radius: 12px;
  padding: 2rem 2.5rem;
  margin-bottom: 1.5rem;
  color: white;
}
.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.welcome-label { font-size: 0.95rem; opacity: 0.85; margin-bottom: 0.2rem; }
.welcome-name  { font-size: 2rem; font-weight: 700; color: white; margin-bottom: 0.3rem; text-transform: capitalize; }
.welcome-date  { font-size: 0.9rem; opacity: 0.75; text-transform: capitalize; }
.welcome-logo svg { width: 72px; height: 72px; opacity: 0.6; }

/* ── KPI Grid ── */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}
.kpi-card {
  background: white;
  border-radius: 12px;
  padding: 1.25rem 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: var(--shadow-sm);
  border-left: 4px solid transparent;
  transition: transform 0.15s, box-shadow 0.15s;
}
.kpi-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
.kpi-green  { border-left-color: var(--color-success); }
.kpi-red    { border-left-color: var(--color-danger); }
.kpi-orange { border-left-color: var(--color-warning); }

.kpi-icon {
  flex-shrink: 0;
  width: 44px; height: 44px;
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
}
.kpi-icon svg { width: 22px; height: 22px; }
.kpi-green  .kpi-icon { background: #e8f5e9; color: var(--color-success); }
.kpi-red    .kpi-icon { background: #fdecea; color: var(--color-danger); }
.kpi-orange .kpi-icon { background: #fff8e1; color: #f59e0b; }

.kpi-label {
  font-size: 0.78rem; color: var(--color-gray-600); font-weight: 500;
  text-transform: uppercase; letter-spacing: 0.04em; margin-bottom: 0.2rem;
}
.kpi-value { font-size: 1.3rem; font-weight: 700; color: var(--color-gray-900); line-height: 1.2; }
.kpi-sub   { font-size: 0.78rem; color: var(--color-gray-500); margin-top: 0.2rem; }

.text-success { color: var(--color-success) !important; }
.text-danger  { color: var(--color-danger)  !important; }
.text-muted-sm { color: var(--color-gray-400); font-size: 1rem; }

/* ── Graphiques ── */
.charts-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 1rem;
  margin-bottom: 1.5rem;
}
.chart-card { margin-bottom: 0; }
.chart-title { font-size: 1rem; font-weight: 600; color: var(--color-gray-700); margin-bottom: 1rem; }
.chart-wrapper { height: 260px; position: relative; }

/* ── Titres de section ── */
.section-title {
  font-size: 1rem; font-weight: 600; color: var(--color-gray-700);
  margin-bottom: 0.75rem; text-transform: uppercase; letter-spacing: 0.05em;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}
.section-header .section-title { margin-bottom: 0; }

/* ── Filtre période ── */
.filtre-periode { display: flex; gap: 0.4rem; }
.btn-filtre {
  padding: 0.3rem 0.75rem;
  border-radius: 20px;
  border: 1px solid var(--color-gray-300);
  background: white;
  font-size: 0.8rem;
  color: var(--color-gray-600);
  cursor: pointer;
  transition: all 0.15s;
}
.btn-filtre:hover { border-color: var(--color-primary); color: var(--color-primary); }
.btn-filtre.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
  font-weight: 600;
}

/* ── Actions rapides ── */
.actions-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}
.action-card {
  display: flex; align-items: center; gap: 1rem;
  padding: 1.25rem 1.5rem; border-radius: 12px;
  text-decoration: none; color: white;
  transition: transform 0.15s, box-shadow 0.15s, filter 0.15s;
  box-shadow: var(--shadow-sm);
}
.action-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); filter: brightness(1.05); }
.action-green { background: linear-gradient(135deg, #2E7D5E, #1a5c43); }
.action-red   { background: linear-gradient(135deg, #e53935, #b71c1c); }
.action-blue  { background: linear-gradient(135deg, #1e88e5, #1565c0); }

.action-icon {
  flex-shrink: 0; width: 44px; height: 44px;
  border-radius: 10px; background: rgba(255,255,255,0.2);
  display: flex; align-items: center; justify-content: center;
}
.action-icon svg { width: 22px; height: 22px; stroke: white; }
.action-title { font-weight: 600; font-size: 0.95rem; margin-bottom: 0.2rem; }
.action-desc  { font-size: 0.8rem; opacity: 0.8; }

/* ── Tableau ── */
.montant-cell { font-weight: 600; }
.montant-pos  { color: var(--color-success); }
.montant-neg  { color: var(--color-danger); }

/* ── Responsive ── */
@media (max-width: 1024px) { .charts-grid { grid-template-columns: 1fr 1fr; } }
@media (max-width: 900px)  { .kpi-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 700px)  { .charts-grid { grid-template-columns: 1fr; } .actions-grid { grid-template-columns: 1fr; } }
@media (max-width: 600px)  { .kpi-grid { grid-template-columns: 1fr; } .welcome-logo { display: none; } .welcome-name { font-size: 1.5rem; } }
</style>
