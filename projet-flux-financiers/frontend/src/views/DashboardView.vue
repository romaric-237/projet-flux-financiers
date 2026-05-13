<template>
  <div class="dashboard">

    <!-- ── Topbar ── -->
    <div class="topbar">
      <div class="breadcrumb">
        <span>Workspace</span>
        <span class="breadcrumb-sep">/</span>
        <span class="breadcrumb-current">Dashboard</span>
      </div>
      <div class="topbar-actions">




      </div>
    </div>

    <!-- ── Header ── -->
    <div class="db-header">

      <h1 class="db-title">
        Bonjour {{ displayName }}, Bienvenu sur le tableau de bord de  <span class="accent-text">Terra Sana</span>
      </h1>
      <p class="db-subtitle">
        Vue d'ensemble des flux financiers  {{ moisActuelLabel }}.
        {{ kpi.nbVersements }} versement(s) traité(s), {{ kpi.nbCharges + kpi.nbPersonnel }} paiement(s) émis.
      </p>
    </div>

    <!-- ── KPI Grid ── -->
    <div class="kpi-grid">

      <!-- Solde Net (featured) -->
      <div class="kpi-card kpi-featured">
        <div class="kpi-label">
          <svg class="kpi-lbl-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg>
          SOLDE NET
        </div>
        <div class="kpi-value kpi-value-accent">
          {{ kpi.solde >= 0 ? '+' : '' }}{{ formatMontant(kpi.solde) }}
        </div>
        <div class="kpi-meta">
          <template v-if="kpi.variation !== null">
            <span :class="['delta', kpi.variation >= 0 ? 'delta-up' : 'delta-down']">
              {{ kpi.variation >= 0 ? '↑' : '↓' }} {{ Math.abs(kpi.variation) }}%
            </span>
            <span>vs mois précédent</span>
          </template>
          <span v-else class="kpi-meta-muted">Données insuffisantes</span>
        </div>
        <svg class="sparkline" viewBox="0 0 80 24" preserveAspectRatio="none">
          <polyline :points="sparklinePoints" fill="none" stroke="#0891B2" stroke-width="1.5"/>
        </svg>
      </div>

      <!-- Recettes -->
      <div class="kpi-card">
        <div class="kpi-label">
          <svg class="kpi-lbl-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/></svg>
          RECETTES
        </div>
        <div class="kpi-value-mono">{{ formatMontant(kpi.totalRecettes) }}</div>
        <div class="kpi-meta">
          <template v-if="kpi.variationRecettes !== null">
            <span :class="['delta', kpi.variationRecettes >= 0 ? 'delta-up' : 'delta-down']">
              {{ kpi.variationRecettes >= 0 ? '↑' : '↓' }} {{ Math.abs(kpi.variationRecettes) }}%
            </span>
          </template>
          <span>{{ kpi.nbVersements }} versement(s)</span>
        </div>
      </div>

      <!-- Dépenses -->
      <div class="kpi-card">
        <div class="kpi-label">
          <svg class="kpi-lbl-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 18 13.5 8.5 8.5 13.5 1 6"/><polyline points="17 18 23 18 23 12"/></svg>
          DÉPENSES
        </div>
        <div class="kpi-value-mono">{{ formatMontant(kpi.totalDepenses) }}</div>
        <div class="kpi-meta">
          <template v-if="kpi.variationDepenses !== null">
            <span :class="['delta', kpi.variationDepenses <= 0 ? 'delta-up' : 'delta-down']">
              {{ kpi.variationDepenses <= 0 ? '↓' : '↑' }} {{ Math.abs(kpi.variationDepenses) }}%
            </span>
          </template>
          <span>{{ kpi.nbCharges + kpi.nbPersonnel }} paiement(s)</span>
        </div>
      </div>

      <!-- Prévisionnel -->
      <div class="kpi-card">
        <div class="kpi-label">
          <svg class="kpi-lbl-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          PRÉVISIONNEL
        </div>
        <div v-if="totalBudgetMensuel > 0" class="kpi-value-mono">
          {{ formatMontant(totalBudgetMensuel) }}
        </div>
        <div v-else class="kpi-value-mono kpi-value-muted">—</div>
        <div class="kpi-meta">
          <template v-if="totalBudgetMensuel > 0">
            <span :style="{ color: kpi.totalDepenses <= totalBudgetMensuel ? '#16A34A' : '#DC2626' }">●</span>
            <span>{{ kpi.totalDepenses <= totalBudgetMensuel ? 'Dans le budget' : 'Plafond dépassé' }}</span>
          </template>
          <span v-else class="kpi-meta-muted">Aucun seuil défini</span>
        </div>
      </div>

    </div>

    <!-- ── Alertes seuils ── -->
    <div v-if="alertesSeuils.length" class="alertes-section">
      <div
        v-for="a in alertesSeuils"
        :key="a.categorie"
        :class="['alerte-banner', a.niveau === 'over' ? 'alerte-red' : 'alerte-orange']"
      >
        <span class="alerte-icon">{{ a.niveau === 'over' ? '🔴' : '🟠' }}</span>
        <div class="alerte-text">
          <strong>{{ a.label }}</strong> —
          {{ formatMontant(a.depense) }} dépensé sur {{ formatMontant(a.plafond) }} ({{ a.pct }}%)
          <span v-if="a.niveau === 'over'"> · Plafond dépassé</span>
          <span v-else> · Proche du plafond</span>
        </div>
        <router-link to="/budget" class="alerte-link">Voir →</router-link>
      </div>
    </div>

    <!-- ── Content Grid : chart + actions rapides ── -->
    <div class="content-grid">

      <!-- Chart -->
      <div class="panel">
        <div class="panel-header">
          <div class="panel-title-row">
            <h3>Évolution des flux</h3>
            <span class="panel-tag">{{ filtreGraphique }}</span>
          </div>
          <div class="tab-group">
            <button
              v-for="t in ['6M','1A','YTD']"
              :key="t"
              :class="['tab', { 'tab-active': filtreGraphique === t }]"
              @click="filtreGraphique = t"
            >{{ t }}</button>
          </div>
        </div>
        <div class="panel-body">
          <div class="chart-wrapper">
            <Line v-if="lineReady" :data="lineData" :options="lineOptions" />
            <p v-else class="chart-empty">Aucune donnée disponible.</p>
          </div>
        </div>
      </div>


      <!-- Actions rapides -->
      <div class="panel">
        <div class="panel-header">
          <div class="panel-title-row"><h3>Actions rapides</h3></div>
        </div>
        <div class="panel-body">
          <div class="quick-actions">
            <router-link to="/versements?action=new" class="action-row">
              <div class="action-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
              </div>
              <div class="action-content">
                <div class="action-title">Nouveau versement</div>
                <div class="action-desc">Enregistrer une recette client</div>
              </div>
              <svg class="action-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
            </router-link>

            <router-link to="/paiements-employe?action=new" class="action-row">
              <div class="action-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 00-4-4H6a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
              </div>
              <div class="action-content">
                <div class="action-title">Paiement employé</div>
                <div class="action-desc">Salaire ou prime</div>
              </div>
              <svg class="action-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
            </router-link>

            <router-link to="/paiements-charge?action=new" class="action-row">
              <div class="action-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/></svg>
              </div>
              <div class="action-content">
                <div class="action-title">Paiement charge</div>
                <div class="action-desc">Véhicule, infrastructure...</div>
              </div>
              <svg class="action-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
            </router-link>

            <router-link to="/export" class="action-row">
              <div class="action-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
              </div>
              <div class="action-content">
                <div class="action-title">Export Excel</div>
                <div class="action-desc">Télécharger les données</div>
              </div>
              <svg class="action-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
            </router-link>
          </div>
        </div>
      </div>

    </div>


    <!-- ── Transactions récentes ── -->
    <div class="transactions-panel">
      <div class="panel-header">
        <div class="panel-title-row">
          <h3>Transactions récentes</h3>
          <span class="panel-tag">6 dernières</span>
        </div>
        <div class="tab-group">
          <button
            v-for="t in transactionTabs"
            :key="t.value"
            :class="['tab', { 'tab-active': filtreType === t.value }]"
            @click="filtreType = t.value"
          >{{ t.label }}</button>
        </div>
      </div>

      <div v-if="loading" class="spinner-wrap">
        <div class="db-spinner"></div>
      </div>

      <table v-else-if="transactionsFiltrees.length">
        <thead>
          <tr>
            <th>TYPE</th>
            <th>LIBELLÉ</th>
            <th>MODE</th>
            <th>DATE</th>
            <th style="text-align:right">MONTANT</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="t in transactionsFiltrees" :key="t.uid">
            <td><span :class="txBadgeClass(t.type)">{{ txBadgeLabel(t.type) }}</span></td>
            <td>{{ t.description }}</td>
            <td class="tx-mode">{{ formatMode(t.mode) }}</td>
            <td class="tx-date">{{ formatDate(t.date) }}</td>
            <td style="text-align:right">
              <span :class="['tx-amount', t.type === 'Versement' ? 'tx-pos' : 'tx-neg']">
                {{ t.type === 'Versement' ? '+' : '−' }}{{ formatMontant(t.montant) }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>

      <p v-else class="empty-state">Aucune transaction disponible.</p>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Line } from 'vue-chartjs'
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
const loading   = ref(false)

const allTransactions  = ref([])
const rawVersements    = ref([])
const rawPaiementsChar = ref([])
const rawPaiementsEmpl = ref([])
const rawSeuils        = ref([])
const rawCharges       = ref([])

const filtreType      = ref('TOUS')
const filtreGraphique = ref('6M')

const transactionTabs = [
  { value: 'TOUS',     label: 'TOUS' },
  { value: 'RECETTES', label: 'RECETTES' },
  { value: 'DEPENSES', label: 'DÉPENSES' }
]

const kpi = ref({
  totalRecettes: 0,  nbVersements: 0,
  totalDepenses: 0,  totalCharges: 0,  nbCharges: 0,
  totalPersonnel: 0, nbPersonnel: 0,
  solde: 0,
  variation: null, variationRecettes: null, variationDepenses: null
})

const CATEGORIES_LABELS = {
  VEHICULES:         'Véhicules',
  INFRASTRUCTURE:    'Infrastructure',
  FISCALES_SOCIALES: 'Fiscales & Sociales',
  SERVICES_EXTERNES: 'Services Externes',
}

// ── Helpers d'affichage ─────────────────────────────────────
const displayName = computed(() => {
  const u = authStore.currentUser
  if (!u) return ''
  return u.prenom || u.username || ''
})

const moisActuelLabel = new Date().toLocaleDateString('fr-BE', { month: 'long', year: 'numeric' })

// ── Transactions filtrées ───────────────────────────────────
const transactionsFiltrees = computed(() => {
  let list = allTransactions.value
  if (filtreType.value === 'RECETTES') list = list.filter(t => t.type === 'Versement')
  if (filtreType.value === 'DEPENSES') list = list.filter(t => t.type !== 'Versement')
  return list.slice(0, 6)
})

// ── Budget total mensuel ────────────────────────────────────
const totalBudgetMensuel = computed(() =>
  rawSeuils.value.reduce((s, x) => s + Number(x.plafond || 0), 0)
)

// ── Alertes seuils ──────────────────────────────────────────
const alertesSeuils = computed(() => {
  if (!rawSeuils.value.length || !rawCharges.value.length) return []
  const now = new Date()
  const moisKey = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  const chargeMap = {}
  rawCharges.value.forEach(c => { chargeMap[c.id] = c.categorie })
  const depenseParCat = {}
  rawPaiementsChar.value.forEach(p => {
    if (p.datePaiement?.startsWith(moisKey)) {
      const cat = chargeMap[p.chargeId]
      if (cat) depenseParCat[cat] = (depenseParCat[cat] || 0) + (Number(p.montant) || 0)
    }
  })
  return rawSeuils.value
    .map(s => {
      const dep = depenseParCat[s.categorie] || 0
      const pct = s.plafond > 0 ? Math.round((dep / Number(s.plafond)) * 100) : 0
      return {
        categorie: s.categorie,
        label: CATEGORIES_LABELS[s.categorie] || s.categorie,
        depense: dep, plafond: Number(s.plafond), pct,
        niveau: pct >= 100 ? 'over' : pct >= 80 ? 'warn' : null
      }
    })
    .filter(a => a.niveau !== null)
    .sort((a, b) => b.pct - a.pct)
})

// ── Sparkline (6 derniers mois — solde) ─────────────────────
const sparklinePoints = computed(() => {
  const now = new Date()
  const months = []
  for (let i = 5; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
    months.push(`${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`)
  }
  const soldes = months.map(m => {
    const r = rawVersements.value
      .filter(v => v.dateVersement?.startsWith(m))
      .reduce((s, v) => s + (v.montantTTC || 0), 0)
    const d = [
      ...rawPaiementsChar.value.filter(p => p.datePaiement?.startsWith(m)),
      ...rawPaiementsEmpl.value.filter(p => p.datePaiement?.startsWith(m))
    ].reduce((s, p) => s + (p.montant || 0), 0)
    return r - d
  })
  if (soldes.every(s => s === 0)) {
    return Array.from({ length: soldes.length }, (_, i) =>
      `${((i / (soldes.length - 1)) * 80).toFixed(1)},12`
    ).join(' ')
  }
  const max = Math.max(...soldes)
  const min = Math.min(...soldes)
  const range = max - min || 1
  return soldes.map((s, i) => {
    const x = (i / (soldes.length - 1)) * 80
    const y = 22 - ((s - min) / range) * 20
    return `${x.toFixed(1)},${y.toFixed(1)}`
  }).join(' ')
})

// ── Line chart ──────────────────────────────────────────────
const lineReady = computed(() =>
  rawVersements.value.length > 0 || rawPaiementsChar.value.length > 0 || rawPaiementsEmpl.value.length > 0
)

const lineData = computed(() => {
  const now = new Date()
  let nbMois = 6
  if (filtreGraphique.value === '1A') nbMois = 12
  else if (filtreGraphique.value === 'YTD') nbMois = now.getMonth() + 1

  const mois = []
  for (let i = nbMois - 1; i >= 0; i--) {
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
    const c = rawPaiementsChar.value.filter(p => p.datePaiement?.startsWith(m.key)).reduce((s, p) => s + (p.montant || 0), 0)
    const e = rawPaiementsEmpl.value.filter(p => p.datePaiement?.startsWith(m.key)).reduce((s, p) => s + (p.montant || 0), 0)
    return c + e
  })

  return {
    labels: mois.map(m => m.label),
    datasets: [
      {
        label: 'Recettes',
        data: recettes,
        borderColor: '#0891B2',
        backgroundColor: 'rgba(8, 145, 178, 0.08)',
        borderWidth: 2,
        pointRadius: 4,
        pointBackgroundColor: '#0891B2',
        pointBorderColor: '#fff',
        pointBorderWidth: 1.5,
        tension: 0.35,
        fill: true
      },
      {
        label: 'Dépenses',
        data: depenses,
        borderColor: '#DC2626',
        backgroundColor: 'rgba(220, 38, 38, 0.04)',
        borderWidth: 1.5,
        pointRadius: 3,
        pointBackgroundColor: '#DC2626',
        borderDash: [4, 3],
        tension: 0.35,
        fill: false
      }
    ]
  }
})

const lineOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom',
      labels: {
        color: '#80808C',
        padding: 18,
        font: { size: 11, family: "'JetBrains Mono', monospace" },
        usePointStyle: true,
        pointStyleWidth: 8
      }
    },
    tooltip: {
      backgroundColor: '#16161D',
      titleColor: '#F0F0F3',
      bodyColor: '#80808C',
      borderColor: '#2B2B35',
      borderWidth: 1,
      callbacks: {
        label: ctx => `  ${ctx.dataset.label} : ${new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' }).format(ctx.parsed.y)}`
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      grid: { color: '#2B2B35', drawBorder: false },
      ticks: {
        color: '#80808C',
        font: { size: 11 },
        callback: val => new Intl.NumberFormat('fr-BE', { notation: 'compact' }).format(val) + ' €'
      },
      border: { display: false }
    },
    x: {
      grid: { display: false },
      ticks: { color: '#80808C', font: { size: 11 } },
      border: { display: false }
    }
  }
}

// ── Chargement ───────────────────────────────────────────────
onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const [v, pc, pe, seuils, ch] = await Promise.all([
      api.get('/versements'),
      api.get('/paiement-charges'),
      api.get('/paiement-employes'),
      api.get('/seuils-budget'),
      api.get('/charges'),
    ])
    rawVersements.value    = v.data
    rawPaiementsChar.value = pc.data
    rawPaiementsEmpl.value = pe.data
    rawSeuils.value        = seuils.data
    rawCharges.value       = ch.data

    const totalRecettes  = v.data.reduce((s, x)  => s + (x.montantTTC || 0), 0)
    const totalCharges   = pc.data.reduce((s, x) => s + (x.montant || 0), 0)
    const totalPersonnel = pe.data.reduce((s, x) => s + (x.montant || 0), 0)
    const totalDepenses  = totalCharges + totalPersonnel
    const solde          = totalRecettes - totalDepenses

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

    const recMA = v.data.filter(x => x.dateVersement?.startsWith(moisActuel)).reduce((s, x) => s + (x.montantTTC || 0), 0)
    const recMP = v.data.filter(x => x.dateVersement?.startsWith(moisPrec)).reduce((s, x) => s + (x.montantTTC || 0), 0)
    const variationRecettes = recMP > 0 ? parseFloat(((recMA - recMP) / recMP * 100).toFixed(1)) : null

    const depMA = [...pc.data, ...pe.data].filter(x => x.datePaiement?.startsWith(moisActuel)).reduce((s, x) => s + (x.montant || 0), 0)
    const depMP = [...pc.data, ...pe.data].filter(x => x.datePaiement?.startsWith(moisPrec)).reduce((s, x) => s + (x.montant || 0), 0)
    const variationDepenses = depMP > 0 ? parseFloat(((depMA - depMP) / depMP * 100).toFixed(1)) : null

    kpi.value = {
      totalRecettes, nbVersements: v.data.length,
      totalDepenses, totalCharges, nbCharges: pc.data.length,
      totalPersonnel, nbPersonnel: pe.data.length,
      solde, variation, variationRecettes, variationDepenses
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

function txBadgeClass(type) {
  if (type === 'Versement') return 'tx-type tx-recette'
  if (type === 'Charge')    return 'tx-type tx-charge'
  return 'tx-type tx-salaire'
}

function txBadgeLabel(type) {
  if (type === 'Versement') return '↑ Recette'
  if (type === 'Charge')    return '↓ Charge'
  return '↓ Salaire'
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
/* ── Tokens locaux remappés vers le thème dark global ───── */
.dashboard {
  --db-bg:           var(--bg-base);
  --db-bg1:          var(--surface-1);
  --db-bg2:          var(--surface-2);
  --db-border:       var(--border);
  --db-border-b:     var(--border-bright);
  --db-text-0:       var(--text-primary);
  --db-text-1:       var(--text-secondary);
  --db-text-2:       var(--text-tertiary);
  --db-accent:       var(--accent-cyan);
  --db-accent-light: var(--accent-cyan);
  --db-accent-bg:    var(--accent-glow);
  --db-success:      var(--color-success);
  --db-success-bg:   var(--color-success-light);
  --db-danger:       var(--color-danger);
  --db-danger-bg:    var(--color-danger-light);
  --db-purple:       var(--color-violet);
  --db-purple-bg:    var(--color-violet-light);

  /* Bleed sur le fond du main-content */
  margin: -1.75rem -1.75rem -2.5rem;
  padding: 28px 36px 2.5rem;
  min-height: 100vh;
  background: var(--db-bg);
  color: var(--db-text-0);
  font-family: 'Inter', system-ui, sans-serif;
}

/* ── Topbar ────────────────────────────────────────────────── */
.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  color: var(--db-text-2);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}
.breadcrumb-sep     { opacity: 0.5; }
.breadcrumb-current { color: var(--db-accent); }

.topbar-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.icon-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: var(--db-bg);
  border: 1px solid var(--db-border);
  color: var(--db-text-1);
  display: grid;
  place-items: center;
  cursor: pointer;
  transition: all 0.15s;
}
.icon-btn:hover {
  background: var(--db-bg1);
  border-color: var(--db-border-b);
  color: var(--db-text-0);
}
.icon-btn-alert { position: relative; }
.icon-btn-alert::after {
  content: '';
  position: absolute;
  top: 7px; right: 7px;
  width: 6px; height: 6px;
  border-radius: 50%;
  background: #DC2626;
  border: 1.5px solid var(--db-bg);
}

.btn-new {
  background: var(--db-accent);
  color: white;
  padding: 9px 16px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 7px;
  text-decoration: none;
  transition: all 0.15s;
  box-shadow: 0 1px 3px rgba(8, 145, 178, 0.3);
  white-space: nowrap;
}
.btn-new:hover {
  background: #0E7490;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(8, 145, 178, 0.25);
}

/* ── Header ────────────────────────────────────────────────── */
.db-header   { margin-bottom: 32px; }

.greeting {
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  color: var(--db-accent);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.greeting::before {
  content: '';
  width: 6px; height: 6px;
  background: var(--db-accent);
  border-radius: 50%;
  animation: pulse-dot 2s ease-in-out infinite;
  flex-shrink: 0;
}
@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50%       { opacity: 0.35; }
}

.db-title {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -0.025em;
  line-height: 1.2;
  margin-bottom: 8px;
  color: var(--db-text-0);
}
.accent-text { color: var(--db-accent); }

.db-subtitle {
  color: var(--db-text-1);
  font-size: 14px;
}

/* ── KPI Grid ──────────────────────────────────────────────── */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 24px;
}

.kpi-card {
  background: var(--db-bg);
  border: 1px solid var(--db-border);
  border-radius: 14px;
  padding: 18px;
  position: relative;
  overflow: hidden;
  transition: all 0.2s;
}
.kpi-card:hover {
  border-color: var(--db-border-b);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
}

.kpi-featured {
  background: linear-gradient(135deg, var(--db-bg) 0%, rgba(8, 145, 178, 0.04) 100%);
  border-color: var(--db-accent);
}
.kpi-featured::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--db-accent-light), var(--db-accent));
}

.kpi-label {
  font-family: 'JetBrains Mono', monospace;
  font-size: 10px;
  color: var(--db-text-2);
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 7px;
}
.kpi-lbl-icon { width: 13px; height: 13px; flex-shrink: 0; color: var(--db-text-2); }
.kpi-featured .kpi-lbl-icon { color: var(--db-accent); }

.kpi-value {
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin-bottom: 7px;
  color: var(--db-text-0);
}
.kpi-value-accent { color: var(--db-accent); }

.kpi-value-mono {
  font-family: 'JetBrains Mono', monospace;
  font-size: 22px;
  font-weight: 600;
  color: var(--db-text-0);
  margin-bottom: 7px;
  letter-spacing: -0.01em;
}
.kpi-value-muted { color: var(--db-text-2); }

.kpi-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--db-text-2);
  flex-wrap: wrap;
}
.kpi-meta-muted { color: var(--db-text-2); font-style: italic; }

.delta {
  display: inline-flex;
  align-items: center;
  font-family: 'JetBrains Mono', monospace;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 600;
}
.delta-up   { background: var(--db-success-bg); color: var(--db-success); }
.delta-down { background: var(--db-danger-bg);  color: var(--db-danger); }

.sparkline {
  position: absolute;
  bottom: 14px; right: 14px;
  width: 80px; height: 24px;
  opacity: 0.45;
}

/* ── Alertes ────────────────────────────────────────────────── */
.alertes-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 24px;
}
.alerte-banner {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 10px;
  border-left: 3px solid;
  font-size: 13px;
}
.alerte-red    { background: #FEF2F2; border-color: #DC2626; color: #7F1D1D; }
.alerte-orange { background: #FFFBEB; border-color: #D97706; color: #78350F; }
.alerte-icon   { font-size: 14px; flex-shrink: 0; }
.alerte-text   { flex: 1; }
.alerte-link   { font-size: 12px; font-weight: 600; color: inherit; text-decoration: underline; white-space: nowrap; }

/* ── Panels communs ────────────────────────────────────────── */
.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 14px;
  margin-bottom: 24px;
}

.panel, .transactions-panel {
  background: var(--db-bg);
  border: 1px solid var(--db-border);
  border-radius: 14px;
  overflow: hidden;
}

.panel-header {
  padding: 16px 18px;
  border-bottom: 1px solid var(--db-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--db-bg1);
}

.panel-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.panel-title-row h3 {
  font-size: 14px;
  font-weight: 600;
  color: var(--db-text-0);
  margin: 0;
}
.panel-tag {
  font-family: 'JetBrains Mono', monospace;
  font-size: 10px;
  color: var(--db-text-2);
  background: var(--db-bg2);
  padding: 2px 8px;
  border-radius: 4px;
}

.tab-group {
  display: flex;
  gap: 2px;
  background: var(--db-bg2);
  padding: 2px;
  border-radius: 6px;
}
.tab {
  padding: 4px 10px;
  font-size: 10px;
  font-family: 'JetBrains Mono', monospace;
  color: var(--db-text-2);
  background: transparent;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  transition: all 0.12s;
}
.tab-active {
  background: var(--db-bg);
  color: var(--db-text-0);
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}

.panel-body { padding: 20px 18px; }

/* ── Chart ─────────────────────────────────────────────────── */
.chart-wrapper { height: 240px; position: relative; }
.chart-empty   { font-size: 13px; color: var(--db-text-2); text-align: center; margin-top: 3rem; }

/* ── Quick Actions ─────────────────────────────────────────── */
.quick-actions { display: flex; flex-direction: column; gap: 8px; }

.action-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 12px;
  background: var(--db-bg1);
  border: 1px solid var(--db-border);
  border-radius: 10px;
  cursor: pointer;
  text-decoration: none;
  color: inherit;
  transition: all 0.15s;
}
.action-row:hover {
  background: var(--db-bg2);
  border-color: var(--db-border-b);
}

.action-icon {
  width: 34px; height: 34px;
  border-radius: 8px;
  background: var(--db-accent-bg);
  display: grid;
  place-items: center;
  color: var(--db-accent);
  flex-shrink: 0;
}
.action-content { flex: 1; min-width: 0; }
.action-title   { font-size: 13px; font-weight: 600; color: var(--db-text-0); margin-bottom: 2px; }
.action-desc    { font-size: 11px; color: var(--db-text-2); }
.action-arrow   { color: var(--db-border-b); flex-shrink: 0; }

/* ── Transactions table ────────────────────────────────────── */
.spinner-wrap { padding: 2rem; display: flex; justify-content: center; }
.db-spinner {
  width: 28px; height: 28px;
  border: 2px solid var(--db-border);
  border-top-color: var(--db-accent);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state { padding: 2rem; text-align: center; font-size: 13px; color: var(--db-text-2); }

/* Override des styles globaux sombres pour la table */
table {
  width: 100%;
  border-collapse: collapse;
  background: var(--db-bg);
  font-size: 13px;
}
thead { background: var(--db-bg1) !important; }
th {
  padding: 12px 18px;
  text-align: left;
  font-family: 'JetBrains Mono', monospace;
  font-size: 10px;
  color: var(--db-text-2) !important;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-weight: 500;
  border-bottom: 1px solid var(--db-border);
  background: var(--db-bg1) !important;
}
td {
  padding: 13px 18px;
  font-size: 13px;
  border-bottom: 1px solid var(--db-border) !important;
  color: var(--db-text-0) !important;
  background: transparent !important;
}
tbody tr { transition: background 0.1s; }
tbody tr:nth-child(even) { background: transparent !important; }
tbody tr:hover { background: var(--db-bg1) !important; }
tbody tr:last-child td { border-bottom: none !important; }

.tx-type {
  display: inline-flex;
  align-items: center;
  font-family: 'JetBrains Mono', monospace;
  font-size: 10px;
  padding: 3px 8px;
  border-radius: 4px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 600;
  white-space: nowrap;
}
.tx-recette { background: var(--db-success-bg); color: var(--db-success); }
.tx-charge  { background: var(--db-danger-bg);  color: var(--db-danger); }
.tx-salaire { background: var(--db-purple-bg);  color: var(--db-purple); }

.tx-mode {
  font-size: 12px;
  color: var(--db-text-2) !important;
}
.tx-date {
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
  color: var(--db-text-1) !important;
}
.tx-amount {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 600;
  font-size: 13px;
}
.tx-pos { color: var(--db-success) !important; }
.tx-neg { color: var(--db-danger)  !important; }

/* ── Responsive ────────────────────────────────────────────── */
@media (max-width: 1100px) {
  .kpi-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 900px) {
  .content-grid { grid-template-columns: 1fr; }
}
@media (max-width: 768px) {
  .dashboard {
    margin: calc(-52px - 1rem) -1rem -2rem;
    padding: calc(52px + 1.25rem) 1rem 2rem;
  }
  .kpi-grid { grid-template-columns: repeat(2, 1fr); }
  .db-title { font-size: 22px; }
}
@media (max-width: 480px) {
  .kpi-grid { grid-template-columns: 1fr; }
  .topbar-actions .btn-new span { display: none; }
}
</style>
