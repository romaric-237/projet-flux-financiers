<template>
  <div class="page">

    <div class="page-header">
      <div>
        <h1 class="page-title">Prévisions & Alertes</h1>
        <p class="page-subtitle">Budget, charges récurrentes et seuils budgétaires</p>
      </div>
    </div>

    <!-- Onglets -->
    <div class="tabs-wrapper">
      <button v-for="tab in tabs" :key="tab.key"
        :class="['tab-btn', activeTab === tab.key ? 'tab-active' : '']"
        @click="activeTab = tab.key">
        <span>{{ tab.icon }}</span> {{ tab.label }}
      </button>
    </div>

    <div v-if="loading" class="spinner"></div>

    <!-- ══════════════════════════════════════════════
         TAB 1 : BUDGET PRÉVISIONNEL
    ══════════════════════════════════════════════ -->
    <div v-else-if="activeTab === 'budget'">

      <!-- Sélecteurs mois/année -->
      <div class="period-selector card">
        <label>Mois</label>
        <select v-model="selectedMois">
          <option v-for="(nom, i) in MOIS_NOMS" :key="i+1" :value="i+1">{{ nom }}</option>
        </select>
        <label>Année</label>
        <select v-model="selectedAnnee">
          <option v-for="a in anneesDisponibles" :key="a" :value="a">{{ a }}</option>
        </select>
      </div>

      <!-- Grille budget par catégorie -->
      <div class="budget-grid">
        <div v-for="cat in CATEGORIES" :key="cat.value" class="budget-card" :class="statusClass(cat.value)">
          <div class="budget-card-header">
            <div class="cat-icon" :style="{ background: cat.bg, color: cat.color }">{{ cat.icon }}</div>
            <div>
              <p class="cat-name">{{ cat.label }}</p>
              <span class="cat-badge" :style="{ background: cat.bg, color: cat.color }">{{ cat.value }}</span>
            </div>
            <div class="budget-status-dot" :class="statusClass(cat.value)"></div>
          </div>

          <div class="budget-figures">
            <div class="figure-row">
              <span class="figure-label">Réalisé</span>
              <span class="figure-val realise">{{ fmt(realise(cat.value)) }}</span>
            </div>
            <div class="figure-row">
              <span class="figure-label">Budgété</span>
              <span class="figure-val budgete">
                {{ budgetFor(cat.value) ? fmt(budgetFor(cat.value).montantBudgete) : '—' }}
              </span>
            </div>
            <div v-if="budgetFor(cat.value)" class="figure-row">
              <span class="figure-label">Écart</span>
              <span class="figure-val" :class="ecart(cat.value) >= 0 ? 'text-success' : 'text-danger'">
                {{ ecart(cat.value) >= 0 ? '+' : '' }}{{ fmt(ecart(cat.value)) }}
              </span>
            </div>
          </div>

          <!-- Barre de progression -->
          <div v-if="budgetFor(cat.value)" class="progress-wrap">
            <div class="progress-bar">
              <div class="progress-fill" :class="statusClass(cat.value)"
                :style="{ width: Math.min(progressPct(cat.value), 100) + '%' }"></div>
            </div>
            <span class="progress-pct">{{ progressPct(cat.value) }}%</span>
          </div>

          <div class="budget-card-actions">
            <button v-if="authStore.isAdmin" class="btn btn-sm btn-secondary" @click="openBudgetForm(cat.value)">
              {{ budgetFor(cat.value) ? '✏️ Modifier' : '+ Définir' }}
            </button>
            <button v-if="authStore.isAdmin && budgetFor(cat.value)" class="btn btn-sm btn-danger"
              @click="deleteBudget(budgetFor(cat.value).id)">🗑️</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ══════════════════════════════════════════════
         TAB 2 : CHARGES RÉCURRENTES
    ══════════════════════════════════════════════ -->
    <div v-else-if="activeTab === 'recurrentes'">

      <div class="section-actions">
        <div>
          <p class="section-info">
            <strong>{{ chargesRecurrentes.filter(c => c.actif).length }}</strong> active(s) sur {{ chargesRecurrentes.length }} —
            génération automatique le <strong>1er de chaque mois</strong>
          </p>
        </div>
        <button v-if="authStore.isAdmin" class="btn btn-primary" @click="openCrForm()">+ Nouvelle charge récurrente</button>
      </div>

      <div class="card table-card">
        <table v-if="chargesRecurrentes.length">
          <thead>
            <tr>
              <th>Charge</th>
              <th>Catégorie</th>
              <th>Montant mensuel</th>
              <th>Statut</th>
              <th v-if="authStore.isAdmin">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cr in chargesRecurrentes" :key="cr.id" :class="{ 'row-inactive': !cr.actif }">
              <td class="font-semibold">{{ cr.chargeLibelle }}</td>
              <td>
                <span v-if="categorieOf(cr.chargeId)" class="cat-badge-sm"
                  :style="{ background: categorieOf(cr.chargeId).bg, color: categorieOf(cr.chargeId).color }">
                  {{ categorieOf(cr.chargeId).icon }} {{ categorieOf(cr.chargeId).label }}
                </span>
                <span v-else class="text-muted-sm">—</span>
              </td>
              <td class="montant-red font-bold">{{ fmt(cr.montantDefaut) }}</td>
              <td>
                <span :class="cr.actif ? 'badge badge-success' : 'badge badge-danger'">
                  {{ cr.actif ? 'Actif' : 'Inactif' }}
                </span>
              </td>
              <td v-if="authStore.isAdmin">
                <div class="d-flex gap-1">
                  <button class="btn btn-sm btn-secondary" @click="openCrForm(cr)">✏️</button>
                  <button class="btn btn-sm" :class="cr.actif ? 'btn-warning' : 'btn-success'" @click="toggleCr(cr)">
                    {{ cr.actif ? '⏸️' : '▶️' }}
                  </button>
                  <button class="btn btn-sm btn-danger" @click="deleteCr(cr.id)">🗑️</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <p v-else class="text-muted text-center mt-2">Aucune charge récurrente configurée.</p>
      </div>
    </div>

    <!-- ══════════════════════════════════════════════
         TAB 3 : SEUILS BUDGÉTAIRES
    ══════════════════════════════════════════════ -->
    <div v-else-if="activeTab === 'seuils'">

      <div class="section-actions">
        <p class="section-info">Plafonds de dépenses par catégorie — mois en cours</p>
        <button v-if="authStore.isAdmin" class="btn btn-primary" @click="openSeuilForm()">+ Nouveau seuil</button>
      </div>

      <div class="seuils-grid">
        <div v-for="cat in CATEGORIES" :key="cat.value" class="seuil-card">
          <div class="seuil-card-header">
            <div class="cat-icon" :style="{ background: cat.bg, color: cat.color }">{{ cat.icon }}</div>
            <div class="seuil-card-title">
              <p class="cat-name">{{ cat.label }}</p>
              <p class="text-muted-sm">Dépenses ce mois</p>
            </div>
          </div>

          <template v-if="seuilFor(cat.value)">
            <div class="seuil-figures">
              <div>
                <span class="figure-label">Dépensé</span>
                <span class="figure-val realise">{{ fmt(realise(cat.value)) }}</span>
              </div>
              <div>
                <span class="figure-label">Plafond</span>
                <span class="figure-val">{{ fmt(seuilFor(cat.value).plafond) }}</span>
              </div>
              <div>
                <span :class="['seuil-badge', seuilStatusClass(cat.value)]">
                  {{ seuilLabel(cat.value) }}
                </span>
              </div>
            </div>
            <div class="progress-wrap">
              <div class="progress-bar">
                <div class="progress-fill" :class="seuilStatusClass(cat.value)"
                  :style="{ width: Math.min(seuilPct(cat.value), 100) + '%' }"></div>
              </div>
              <span class="progress-pct">{{ seuilPct(cat.value) }}%</span>
            </div>
            <div v-if="authStore.isAdmin" class="budget-card-actions">
              <button class="btn btn-sm btn-secondary" @click="openSeuilForm(seuilFor(cat.value))">✏️ Modifier</button>
              <button class="btn btn-sm btn-danger" @click="deleteSeuil(seuilFor(cat.value).id)">🗑️</button>
            </div>
          </template>
          <div v-else class="seuil-empty">
            <p class="text-muted-sm">Aucun seuil défini</p>
            <button v-if="authStore.isAdmin" class="btn btn-sm btn-secondary" @click="openSeuilForm(null, cat.value)">+ Définir</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ══════════════════════════════════════════════
         MODALS
    ══════════════════════════════════════════════ -->

    <!-- Modal : Budget -->
    <div v-if="showBudgetForm" class="modal-overlay" @click.self="showBudgetForm = false">
      <div class="modal-card">
        <div class="modal-header">
          <h3>Budget — {{ labelFor(budgetForm.categorie) }} {{ MOIS_NOMS[budgetForm.mois - 1] }} {{ budgetForm.annee }}</h3>
          <button class="modal-close" @click="showBudgetForm = false">✕</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveBudget">
            <div class="form-group">
              <label>Montant budgété (€) *</label>
              <input v-model="budgetForm.montantBudgete" type="number" step="0.01" min="0.01"
                :class="{ error: budgetErrors.montantBudgete }" placeholder="Ex: 500" />
              <span v-if="budgetErrors.montantBudgete" class="error-message">{{ budgetErrors.montantBudgete }}</span>
            </div>
            <div class="modal-actions">
              <button type="submit" class="btn btn-primary" :disabled="saving">
                {{ saving ? 'Enregistrement...' : 'Enregistrer' }}
              </button>
              <button type="button" class="btn btn-secondary" @click="showBudgetForm = false">Annuler</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Modal : Charge récurrente -->
    <div v-if="showCrForm" class="modal-overlay" @click.self="showCrForm = false">
      <div class="modal-card">
        <div class="modal-header">
          <h3>{{ crEditingId ? 'Modifier' : 'Nouvelle' }} charge récurrente</h3>
          <button class="modal-close" @click="showCrForm = false">✕</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveCr">
            <div class="form-group">
              <label>Charge *</label>
              <select v-model="crForm.chargeId" :class="{ error: crErrors.chargeId }" :disabled="!!crEditingId">
                <option value="">— Sélectionner une charge —</option>
                <option v-for="c in chargesDisponibles" :key="c.id" :value="c.id">
                  {{ c.libelle }} ({{ categorieOf(c.id)?.label || c.categorie }})
                </option>
              </select>
              <span v-if="crErrors.chargeId" class="error-message">{{ crErrors.chargeId }}</span>
            </div>
            <div class="form-group">
              <label>Montant mensuel (€) *</label>
              <input v-model="crForm.montantDefaut" type="number" step="0.01" min="0.01"
                :class="{ error: crErrors.montantDefaut }" placeholder="Ex: 1200" />
              <span v-if="crErrors.montantDefaut" class="error-message">{{ crErrors.montantDefaut }}</span>
            </div>
            <div class="form-group">
              <label>Statut</label>
              <select v-model="crForm.actif">
                <option :value="true">Actif</option>
                <option :value="false">Inactif</option>
              </select>
            </div>
            <div class="modal-actions">
              <button type="submit" class="btn btn-primary" :disabled="saving">
                {{ saving ? 'Enregistrement...' : 'Enregistrer' }}
              </button>
              <button type="button" class="btn btn-secondary" @click="showCrForm = false">Annuler</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Modal : Seuil -->
    <div v-if="showSeuilForm" class="modal-overlay" @click.self="showSeuilForm = false">
      <div class="modal-card">
        <div class="modal-header">
          <h3>{{ seuilEditingId ? 'Modifier le seuil' : 'Nouveau seuil' }}</h3>
          <button class="modal-close" @click="showSeuilForm = false">✕</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveSeuil">
            <div class="form-group">
              <label>Catégorie *</label>
              <select v-model="seuilForm.categorie" :class="{ error: seuilErrors.categorie }" :disabled="!!seuilEditingId">
                <option value="">— Sélectionner —</option>
                <option v-for="cat in CATEGORIES" :key="cat.value" :value="cat.value">{{ cat.label }}</option>
              </select>
              <span v-if="seuilErrors.categorie" class="error-message">{{ seuilErrors.categorie }}</span>
            </div>
            <div class="form-group">
              <label>Plafond mensuel (€) *</label>
              <input v-model="seuilForm.plafond" type="number" step="0.01" min="0.01"
                :class="{ error: seuilErrors.plafond }" placeholder="Ex: 800" />
              <span v-if="seuilErrors.plafond" class="error-message">{{ seuilErrors.plafond }}</span>
            </div>
            <div class="modal-actions">
              <button type="submit" class="btn btn-primary" :disabled="saving">
                {{ saving ? 'Enregistrement...' : 'Enregistrer' }}
              </button>
              <button type="button" class="btn btn-secondary" @click="showSeuilForm = false">Annuler</button>
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

const authStore = useAuthStore()
const toast = useToast()

// ── Constantes ────────────────────────────────────────────
const CATEGORIES = [
  { value: 'VEHICULES',         label: 'Véhicules',           color: '#1e88e5', bg: '#e3f2fd', icon: '🚗' },
  { value: 'INFRASTRUCTURE',    label: 'Infrastructure',      color: '#00897b', bg: '#e0f2f1', icon: '🏢' },
  { value: 'FISCALES_SOCIALES', label: 'Fiscales & Sociales', color: '#8e24aa', bg: '#f3e5f5', icon: '📋' },
  { value: 'SERVICES_EXTERNES', label: 'Services Externes',   color: '#f59e0b', bg: '#fffde7', icon: '🔧' },
]
const MOIS_NOMS = ['Janvier','Février','Mars','Avril','Mai','Juin','Juillet','Août','Septembre','Octobre','Novembre','Décembre']

const anneesDisponibles = computed(() => {
  const y = new Date().getFullYear()
  return [y - 1, y, y + 1]
})

const tabs = [
  { key: 'budget',      label: 'Budget prévisionnel',  icon: '📊' },
  { key: 'recurrentes', label: 'Charges récurrentes',  icon: '🔄' },
  { key: 'seuils',      label: 'Seuils budgétaires',   icon: '⚠️' },
]

// ── État ──────────────────────────────────────────────────
const activeTab     = ref('budget')
const loading       = ref(false)
const saving        = ref(false)

const now = new Date()
const selectedMois  = ref(now.getMonth() + 1)
const selectedAnnee = ref(now.getFullYear())

const budgets           = ref([])
const chargesRecurrentes = ref([])
const seuils            = ref([])
const paiementsCharges  = ref([])
const charges           = ref([])

// ── Chargement ────────────────────────────────────────────
onMounted(loadAll)

async function loadAll() {
  loading.value = true
  try {
    const [b, cr, s, pc, ch] = await Promise.all([
      api.get('/budgets'),
      api.get('/charges-recurrentes'),
      api.get('/seuils-budget'),
      api.get('/paiement-charges'),
      api.get('/charges'),
    ])
    budgets.value            = b.data
    chargesRecurrentes.value = cr.data
    seuils.value             = s.data
    paiementsCharges.value   = pc.data
    charges.value            = ch.data
  } catch {
    toast.error('Impossible de charger les données')
  } finally {
    loading.value = false
  }
}

// ── Helpers catégories ────────────────────────────────────
const chargeMap = computed(() => {
  const m = {}
  charges.value.forEach(c => m[c.id] = c)
  return m
})

function categorieOf(chargeId) {
  const c = chargeMap.value[chargeId]
  if (!c) return null
  return CATEGORIES.find(cat => cat.value === c.categorie) || null
}

function labelFor(val) {
  return CATEGORIES.find(c => c.value === val)?.label || val
}

// ── Budget ────────────────────────────────────────────────
const budgetsMoisAnnee = computed(() =>
  budgets.value.filter(b => b.mois === selectedMois.value && b.annee === selectedAnnee.value)
)

function budgetFor(categorie) {
  return budgetsMoisAnnee.value.find(b => b.categorie === categorie) || null
}

function realise(categorie) {
  const moisKey = `${selectedAnnee.value}-${String(selectedMois.value).padStart(2, '0')}`
  return paiementsCharges.value
    .filter(p => {
      const cat = categorieOf(p.chargeId)
      return cat?.value === categorie && p.datePaiement?.startsWith(moisKey)
    })
    .reduce((s, p) => s + (Number(p.montant) || 0), 0)
}

function ecart(categorie) {
  const b = budgetFor(categorie)
  if (!b) return 0
  return Number(b.montantBudgete) - realise(categorie)
}

function progressPct(categorie) {
  const b = budgetFor(categorie)
  if (!b || Number(b.montantBudgete) === 0) return 0
  return Math.round((realise(categorie) / Number(b.montantBudgete)) * 100)
}

function statusClass(categorie) {
  const b = budgetFor(categorie)
  if (!b) return 'neutral'
  const pct = progressPct(categorie)
  if (pct >= 100) return 'over'
  if (pct >= 90)  return 'warn'
  return 'ok'
}

// ── Seuils ────────────────────────────────────────────────
function seuilFor(categorie) {
  return seuils.value.find(s => s.categorie === categorie) || null
}

function seuilPct(categorie) {
  const s = seuilFor(categorie)
  if (!s || Number(s.plafond) === 0) return 0
  const moisKey = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  const dep = paiementsCharges.value
    .filter(p => {
      const cat = categorieOf(p.chargeId)
      return cat?.value === categorie && p.datePaiement?.startsWith(moisKey)
    })
    .reduce((sum, p) => sum + (Number(p.montant) || 0), 0)
  return Math.round((dep / Number(s.plafond)) * 100)
}

function seuilStatusClass(categorie) {
  const pct = seuilPct(categorie)
  if (pct >= 100) return 'over'
  if (pct >= 80)  return 'warn'
  return 'ok'
}

function seuilLabel(categorie) {
  const pct = seuilPct(categorie)
  if (pct >= 100) return '🔴 Dépassé'
  if (pct >= 80)  return '🟠 Proche du plafond'
  return '🟢 Normal'
}

// ── Charges disponibles (pour dropdown CR) ─────────────────
const chargesDisponibles = computed(() => {
  const usedIds = new Set(chargesRecurrentes.value.map(cr => cr.chargeId))
  return charges.value.filter(c => !usedIds.has(c.id))
})

// ══════════════════════════════════════════════
// ACTIONS — BUDGET
// ══════════════════════════════════════════════
const showBudgetForm  = ref(false)
const budgetEditingId = ref(null)
const budgetForm      = ref({ categorie: '', mois: 1, annee: 2026, montantBudgete: '' })
const budgetErrors    = ref({})

function openBudgetForm(categorie) {
  budgetErrors.value = {}
  const existing = budgetFor(categorie)
  budgetEditingId.value = existing?.id || null
  budgetForm.value = {
    categorie,
    mois:           selectedMois.value,
    annee:          selectedAnnee.value,
    montantBudgete: existing ? existing.montantBudgete : '',
  }
  showBudgetForm.value = true
}

async function saveBudget() {
  const e = {}
  if (!budgetForm.value.montantBudgete || budgetForm.value.montantBudgete <= 0)
    e.montantBudgete = 'Le montant doit être supérieur à 0'
  budgetErrors.value = e
  if (Object.keys(e).length) return

  saving.value = true
  const payload = {
    categorie:       budgetForm.value.categorie,
    mois:            budgetForm.value.mois,
    annee:           budgetForm.value.annee,
    montantBudgete:  parseFloat(budgetForm.value.montantBudgete),
    createdById:     authStore.currentUser?.id,
  }
  try {
    if (budgetEditingId.value) {
      await api.put(`/budgets/${budgetEditingId.value}`, payload)
      toast.success('Budget modifié')
    } else {
      await api.post('/budgets', payload)
      toast.success('Budget créé')
    }
    showBudgetForm.value = false
    const res = await api.get('/budgets')
    budgets.value = res.data
  } catch (err) {
    toast.error(err.response?.data?.message || 'Erreur')
  } finally {
    saving.value = false
  }
}

async function deleteBudget(id) {
  if (!confirm('Supprimer ce budget ?')) return
  try {
    await api.delete(`/budgets/${id}`)
    toast.success('Budget supprimé')
    budgets.value = budgets.value.filter(b => b.id !== id)
  } catch {
    toast.error('Erreur lors de la suppression')
  }
}

// ══════════════════════════════════════════════
// ACTIONS — CHARGES RÉCURRENTES
// ══════════════════════════════════════════════
const showCrForm  = ref(false)
const crEditingId = ref(null)
const crForm      = ref({ chargeId: '', montantDefaut: '', actif: true })
const crErrors    = ref({})

function openCrForm(cr = null) {
  crErrors.value = {}
  crEditingId.value = cr?.id || null
  crForm.value = {
    chargeId:     cr?.chargeId     || '',
    montantDefaut: cr?.montantDefaut || '',
    actif:        cr !== null ? cr.actif : true,
  }
  showCrForm.value = true
}

async function saveCr() {
  const e = {}
  if (!crForm.value.chargeId) e.chargeId = 'La charge est obligatoire'
  if (!crForm.value.montantDefaut || crForm.value.montantDefaut <= 0)
    e.montantDefaut = 'Le montant doit être supérieur à 0'
  crErrors.value = e
  if (Object.keys(e).length) return

  saving.value = true
  const payload = {
    chargeId:      crForm.value.chargeId,
    montantDefaut: parseFloat(crForm.value.montantDefaut),
    actif:         crForm.value.actif,
    createdById:   authStore.currentUser?.id,
  }
  try {
    if (crEditingId.value) {
      await api.put(`/charges-recurrentes/${crEditingId.value}`, payload)
      toast.success('Charge récurrente modifiée')
    } else {
      await api.post('/charges-recurrentes', payload)
      toast.success('Charge récurrente créée')
    }
    showCrForm.value = false
    const res = await api.get('/charges-recurrentes')
    chargesRecurrentes.value = res.data
  } catch (err) {
    toast.error(err.response?.data?.message || 'Erreur')
  } finally {
    saving.value = false
  }
}

async function toggleCr(cr) {
  try {
    await api.put(`/charges-recurrentes/${cr.id}`, {
      chargeId:      cr.chargeId,
      montantDefaut: cr.montantDefaut,
      actif:         !cr.actif,
      createdById:   authStore.currentUser?.id,
    })
    cr.actif = !cr.actif
    toast.success(cr.actif ? 'Activée' : 'Désactivée')
  } catch {
    toast.error('Erreur')
  }
}

async function deleteCr(id) {
  if (!confirm('Supprimer cette charge récurrente ?')) return
  try {
    await api.delete(`/charges-recurrentes/${id}`)
    toast.success('Supprimée')
    chargesRecurrentes.value = chargesRecurrentes.value.filter(c => c.id !== id)
  } catch {
    toast.error('Erreur lors de la suppression')
  }
}

// ══════════════════════════════════════════════
// ACTIONS — SEUILS
// ══════════════════════════════════════════════
const showSeuilForm  = ref(false)
const seuilEditingId = ref(null)
const seuilForm      = ref({ categorie: '', plafond: '' })
const seuilErrors    = ref({})

function openSeuilForm(seuil = null, categoriePrefill = '') {
  seuilErrors.value = {}
  seuilEditingId.value = seuil?.id || null
  seuilForm.value = {
    categorie: seuil?.categorie || categoriePrefill || '',
    plafond:   seuil?.plafond   || '',
  }
  showSeuilForm.value = true
}

async function saveSeuil() {
  const e = {}
  if (!seuilForm.value.categorie) e.categorie = 'La catégorie est obligatoire'
  if (!seuilForm.value.plafond || seuilForm.value.plafond <= 0)
    e.plafond = 'Le plafond doit être supérieur à 0'
  seuilErrors.value = e
  if (Object.keys(e).length) return

  saving.value = true
  const payload = {
    categorie:   seuilForm.value.categorie,
    plafond:     parseFloat(seuilForm.value.plafond),
    createdById: authStore.currentUser?.id,
  }
  try {
    if (seuilEditingId.value) {
      await api.put(`/seuils-budget/${seuilEditingId.value}`, payload)
      toast.success('Seuil modifié')
    } else {
      await api.post('/seuils-budget', payload)
      toast.success('Seuil créé')
    }
    showSeuilForm.value = false
    const res = await api.get('/seuils-budget')
    seuils.value = res.data
  } catch (err) {
    toast.error(err.response?.data?.message || 'Erreur')
  } finally {
    saving.value = false
  }
}

async function deleteSeuil(id) {
  if (!confirm('Supprimer ce seuil ?')) return
  try {
    await api.delete(`/seuils-budget/${id}`)
    toast.success('Seuil supprimé')
    seuils.value = seuils.value.filter(s => s.id !== id)
  } catch {
    toast.error('Erreur lors de la suppression')
  }
}

// ── Utilitaires ───────────────────────────────────────────
function fmt(m) {
  return new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' }).format(m || 0)
}
</script>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; padding: 0 1rem 2rem; }

/* ── Onglets ── */
.tabs-wrapper {
  display: flex; gap: 0.25rem;
  background: var(--color-gray-200); border-radius: 10px;
  padding: 0.25rem; margin-bottom: 1.5rem; width: fit-content;
}
.tab-btn {
  display: flex; align-items: center; gap: 0.4rem;
  padding: 0.5rem 1.25rem; border: none; border-radius: 8px;
  background: transparent; cursor: pointer;
  font-size: 0.9rem; color: var(--color-gray-600); font-weight: 500;
  transition: all 0.15s;
}
.tab-btn:hover { color: var(--color-gray-900); }
.tab-active { background: white; color: var(--color-primary); box-shadow: var(--shadow-sm); font-weight: 600; }

/* ── Sélecteur période ── */
.period-selector {
  display: flex; align-items: center; gap: 1rem;
  padding: 1rem 1.5rem; margin-bottom: 1.5rem; flex-wrap: wrap;
}
.period-selector label { font-size: 0.85rem; font-weight: 600; color: var(--color-gray-600); }
.period-selector select { padding: 0.4rem 0.75rem; border: 1px solid var(--color-gray-300); border-radius: 8px; font-size: 0.9rem; }

/* ── Budget cards ── */
.budget-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}
.budget-card {
  background: white; border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-gray-200); box-shadow: var(--shadow-sm);
  padding: 1.25rem; transition: box-shadow 0.2s;
}
.budget-card:hover { box-shadow: var(--shadow-md); }
.budget-card.ok     { border-left: 4px solid var(--color-success); }
.budget-card.warn   { border-left: 4px solid #f59e0b; }
.budget-card.over   { border-left: 4px solid var(--color-danger); }
.budget-card.neutral { border-left: 4px solid var(--color-gray-300); }

.budget-card-header { display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem; }
.cat-icon {
  width: 40px; height: 40px; border-radius: 10px; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center; font-size: 1.1rem;
}
.cat-name { font-weight: 600; font-size: 0.95rem; color: var(--color-gray-900); margin-bottom: 0.1rem; }
.cat-badge {
  font-size: 0.7rem; padding: 0.15rem 0.5rem; border-radius: 20px; font-weight: 500;
}
.cat-badge-sm { font-size: 0.75rem; padding: 0.2rem 0.6rem; border-radius: 20px; font-weight: 500; }
.budget-status-dot {
  margin-left: auto; width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0;
}
.budget-status-dot.ok { background: var(--color-success); }
.budget-status-dot.warn { background: #f59e0b; }
.budget-status-dot.over { background: var(--color-danger); }
.budget-status-dot.neutral { background: var(--color-gray-300); }

.budget-figures { display: flex; flex-direction: column; gap: 0.4rem; margin-bottom: 1rem; }
.figure-row { display: flex; justify-content: space-between; align-items: center; }
.figure-label { font-size: 0.78rem; color: var(--color-gray-500); text-transform: uppercase; letter-spacing: 0.04em; }
.figure-val { font-size: 0.95rem; font-weight: 600; }
.realise  { color: var(--color-gray-800); }
.budgete  { color: var(--color-gray-600); }
.text-success { color: var(--color-success) !important; }
.text-danger  { color: var(--color-danger) !important; }

.progress-wrap { display: flex; align-items: center; gap: 0.5rem; margin-bottom: 1rem; }
.progress-bar { flex: 1; height: 6px; background: var(--color-gray-200); border-radius: 3px; overflow: hidden; }
.progress-fill { height: 100%; border-radius: 3px; transition: width 0.4s; }
.progress-fill.ok   { background: var(--color-success); }
.progress-fill.warn { background: #f59e0b; }
.progress-fill.over { background: var(--color-danger); }
.progress-fill.neutral { background: var(--color-gray-300); }
.progress-pct { font-size: 0.78rem; font-weight: 600; color: var(--color-gray-600); white-space: nowrap; }

.budget-card-actions { display: flex; gap: 0.5rem; }

/* ── Seuils grid ── */
.seuils-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 1rem;
}
.seuil-card {
  background: white; border-radius: var(--border-radius-lg);
  border: 1px solid var(--color-gray-200); box-shadow: var(--shadow-sm);
  padding: 1.25rem;
}
.seuil-card-header { display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem; }
.seuil-card-title .cat-name { font-weight: 600; }
.seuil-figures { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem; flex-wrap: wrap; gap: 0.5rem; }
.seuil-badge {
  font-size: 0.78rem; font-weight: 600; padding: 0.25rem 0.6rem;
  border-radius: 20px;
}
.seuil-badge.ok   { background: var(--color-success-light); color: #065f46; }
.seuil-badge.warn { background: #fff7ed; color: #92400e; }
.seuil-badge.over { background: var(--color-danger-light); color: #991b1b; }
.seuil-empty { text-align: center; padding: 1rem 0; }

/* ── Section actions ── */
.section-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; gap: 0.75rem; }
.section-info { font-size: 0.88rem; color: var(--color-gray-600); }

/* ── Table ── */
.table-card { padding: 0; overflow: hidden; }
.table-card table { margin: 0; border-radius: 0; }
.row-inactive td { opacity: 0.55; }
.font-semibold { font-weight: 600; }
.font-bold { font-weight: 700; }
.montant-red { color: var(--color-danger); }
.text-muted-sm { font-size: 0.82rem; color: var(--color-gray-500); }

/* ── Modal ── */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(15, 23, 42, 0.5); backdrop-filter: blur(2px);
  display: flex; align-items: center; justify-content: center;
  z-index: 2000; padding: 1rem; animation: fadeIn 0.15s ease;
}
.modal-card {
  background: white; border-radius: var(--border-radius-xl);
  width: 100%; max-width: 460px; max-height: 90vh; overflow-y: auto;
  box-shadow: var(--shadow-xl); animation: slideUp 0.2s ease;
}
.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 1.1rem 1.5rem; background: var(--color-gray-50);
  border-bottom: 1px solid var(--color-gray-200);
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
}
.modal-header h3 { font-size: 1rem; font-weight: 600; margin: 0; }
.modal-close {
  width: 28px; height: 28px; border-radius: 6px; background: none; border: none;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  color: var(--color-gray-500); transition: all 0.15s;
}
.modal-close:hover { background: var(--color-gray-200); }
.modal-body { padding: 1.5rem; }
.modal-actions { display: flex; gap: 0.75rem; margin-top: 1.25rem; }

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes slideUp {
  from { opacity: 0; transform: translateY(10px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

@media (max-width: 768px) {
  .budget-grid, .seuils-grid { grid-template-columns: 1fr; }
}
</style>