<template>
  <div class="page">

    <div class="page-header">
      <div>
        <h1 class="page-title">Export</h1>
        <p class="page-subtitle">Téléchargez vos données en Excel ou CSV</p>
      </div>
    </div>

    <!-- Filtres -->
    <div class="filters-card">
      <div class="filters-title">
        <span>🗓️</span> Filtres de période <span class="optional">(optionnel)</span>
      </div>
      <div class="filters-row">
        <div class="form-group">
          <label>Date de début</label>
          <input v-model="dateDebut" type="date" />
        </div>
        <div class="form-group">
          <label>Date de fin</label>
          <input v-model="dateFin" type="date" />
        </div>
        <div class="form-group">
          <label>Format</label>
          <select v-model="format">
            <option value="EXCEL">📊 Excel (.xlsx)</option>
            <option value="CSV">📄 CSV (.csv)</option>
          </select>
        </div>
        <button v-if="dateDebut || dateFin" class="btn btn-secondary btn-sm reset-btn" @click="dateDebut = ''; dateFin = ''">
          Réinitialiser
        </button>
      </div>
    </div>

    <!-- Export complet (principal) -->
    <div class="export-complet-card">
      <div class="export-complet-left">
        <div class="export-icon-lg">📊</div>
        <div>
          <h2 class="export-complet-title">Export complet</h2>
          <p class="export-complet-desc">Fichier Excel structuré en 4 feuilles avec totaux calculés automatiquement</p>
          <div class="export-sheets">
            <span class="sheet-badge sheet-green">Recettes</span>
            <span class="sheet-badge sheet-blue">Dépenses Personnel</span>
            <span class="sheet-badge sheet-red">Dépenses Charges</span>
            <span class="sheet-badge sheet-purple">Synthèse</span>
          </div>
        </div>
      </div>
      <button class="btn btn-export-complet" :disabled="downloading === 'complet'" @click="downloadComplet()">
        <span v-if="downloading === 'complet'" class="btn-spinner"></span>
        <span v-else>⬇️</span>
        {{ downloading === 'complet' ? 'Génération...' : 'Exporter tout' }}
      </button>
    </div>

    <!-- Séparateur -->
    <div class="section-divider">
      <span>Exports individuels</span>
    </div>

    <!-- Cards export individuels -->
    <div class="export-grid">

      <div class="export-card">
        <div class="export-icon" style="background:#ecfdf5; color:#059669">💰</div>
        <div class="export-info">
          <h3>Versements clients</h3>
          <p>Recettes enregistrées  montants, dates, modes de paiement</p>
          <div class="export-cols">
            <span>ID</span><span>Client</span><span>Montant TTC</span><span>Date</span><span>Mode</span>
          </div>
        </div>
        <button class="btn btn-export" :disabled="downloading === 'versements'" @click="download('versements')">
          <span v-if="downloading === 'versements'" class="btn-spinner"></span>
          <span v-else>⬇️</span>
          {{ downloading === 'versements' ? 'Export...' : 'Exporter' }}
        </button>
      </div>

      <div class="export-card">
        <div class="export-icon" style="background:#fef2f2; color:#ef4444">📦</div>
        <div class="export-info">
          <h3>Paiements charges</h3>
          <p>Véhicules, infrastructure, fiscales &amp; sociales, services externes</p>
          <div class="export-cols">
            <span>ID</span><span>Charge</span><span>Montant</span><span>Date</span><span>Mode</span>
          </div>
        </div>
        <button class="btn btn-export" :disabled="downloading === 'paiements-charges'" @click="download('paiements-charges')">
          <span v-if="downloading === 'paiements-charges'" class="btn-spinner"></span>
          <span v-else>⬇️</span>
          {{ downloading === 'paiements-charges' ? 'Export...' : 'Exporter' }}
        </button>
      </div>

      <div class="export-card">
        <div class="export-icon" style="background:#dbeafe; color:#3b82f6">👔</div>
        <div class="export-info">
          <h3>Paiements employés</h3>
          <p>Salaires et primes versés au personnel actif</p>
          <div class="export-cols">
            <span>ID</span><span>Employé</span><span>Type</span><span>Montant</span><span>Date</span>
          </div>
        </div>
        <button class="btn btn-export" :disabled="downloading === 'paiements-employes'" @click="download('paiements-employes')">
          <span v-if="downloading === 'paiements-employes'" class="btn-spinner"></span>
          <span v-else>⬇️</span>
          {{ downloading === 'paiements-employes' ? 'Export...' : 'Exporter' }}
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

const toast = useToast()
const dateDebut   = ref('')
const dateFin     = ref('')
const format      = ref('EXCEL')
const downloading = ref(null)

async function download(type) {
  downloading.value = type
  try {
    const params = { format: format.value }
    if (dateDebut.value) params.dateDebut = dateDebut.value
    if (dateFin.value)   params.dateFin   = dateFin.value

    const res = await api.get(`/export/${type}`, { params, responseType: 'blob' })
    const ext  = format.value === 'CSV' ? 'csv' : 'xlsx'
    const filename = `${type}_${new Date().toISOString().slice(0, 10)}.${ext}`

    triggerDownload(res.data, filename)
    toast.success(`"${filename}" téléchargé`)
  } catch {
    toast.error('Erreur lors de l\'export')
  } finally {
    downloading.value = null
  }
}

async function downloadComplet() {
  downloading.value = 'complet'
  try {
    const params = {}
    if (dateDebut.value) params.dateDebut = dateDebut.value
    if (dateFin.value)   params.dateFin   = dateFin.value

    const res = await api.get('/export/complet', { params, responseType: 'blob' })
    const filename = `flux-financiers-complet_${new Date().toISOString().slice(0, 10)}.xlsx`

    triggerDownload(res.data, filename)
    toast.success(`"${filename}" téléchargé (4 feuilles)`)
  } catch {
    toast.error('Erreur lors de l\'export complet')
  } finally {
    downloading.value = null
  }
}

function triggerDownload(blob, filename) {
  const url = URL.createObjectURL(blob)
  const a   = document.createElement('a')
  a.href = url; a.download = filename; a.click()
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
.page { max-width: 900px; margin: 0 auto; padding: 0 1rem 2rem; }

/* ── Filtres ── */
.filters-card {
  background: var(--surface-1); border-radius: var(--border-radius-lg);
  border: 1px solid var(--border); box-shadow: var(--shadow-sm);
  padding: 1.25rem 1.5rem; margin-bottom: 1.5rem;
}
.filters-title {
  font-size: 0.88rem; font-weight: 600; color: var(--color-gray-700);
  margin-bottom: 1rem; display: flex; align-items: center; gap: 0.4rem;
}
.optional { font-weight: 400; color: var(--color-gray-400); font-size: 0.8rem; }
.filters-row {
  display: flex; gap: 1rem; flex-wrap: wrap; align-items: flex-end;
}
.filters-row .form-group { flex: 1; min-width: 150px; margin-bottom: 0; }
.reset-btn { align-self: flex-end; }

/* ── Export complet ── */
.export-complet-card {
  background: var(--color-success-light);
  border: 1px solid rgba(74, 222, 128, 0.25);
  border-radius: var(--border-radius-lg);
  padding: 1.5rem;
  display: flex; align-items: center; justify-content: space-between; gap: 1.5rem;
  margin-bottom: 1rem;
  box-shadow: var(--shadow-sm);
}
.export-complet-left { display: flex; align-items: center; gap: 1.25rem; }
.export-icon-lg {
  width: 64px; height: 64px; border-radius: 16px;
  background: rgba(74, 222, 128, 0.15); color: var(--color-success);
  display: flex; align-items: center; justify-content: center;
  font-size: 1.8rem; flex-shrink: 0;
  border: 1px solid rgba(74, 222, 128, 0.25);
}
.export-complet-title { font-size: 1.1rem; font-weight: 700; color: var(--color-success); margin-bottom: 0.2rem; }
.export-complet-desc  { font-size: 0.82rem; color: var(--text-secondary); margin-bottom: 0.6rem; }

.export-sheets { display: flex; gap: 0.4rem; flex-wrap: wrap; }
.sheet-badge {
  font-size: 0.72rem; padding: 0.2rem 0.6rem;
  border-radius: 99px; font-weight: 600;
}
.sheet-green  { background: var(--color-success-light); color: var(--color-success); }
.sheet-blue   { background: var(--color-info-light);    color: var(--color-info); }
.sheet-red    { background: var(--color-danger-light);  color: var(--color-danger); }
.sheet-purple { background: var(--color-violet-light);  color: var(--color-violet); }

.btn-export-complet {
  display: inline-flex; align-items: center; gap: 0.5rem;
  padding: 0.7rem 1.5rem; border-radius: var(--border-radius);
  background: #16a34a; color: white; border: none;
  font-size: 0.95rem; font-weight: 700; cursor: pointer;
  white-space: nowrap; flex-shrink: 0; transition: all 0.15s;
}
.btn-export-complet:hover:not(:disabled) { background: #15803d; box-shadow: 0 4px 12px rgba(22,163,74,0.4); }
.btn-export-complet:disabled { opacity: 0.6; cursor: not-allowed; }

/* ── Séparateur ── */
.section-divider {
  display: flex; align-items: center; gap: 1rem;
  margin: 1.5rem 0 1rem;
  color: var(--color-gray-400); font-size: 0.82rem; font-weight: 600;
}
.section-divider::before,
.section-divider::after {
  content: ''; flex: 1; height: 1px; background: var(--color-gray-200);
}

/* ── Cards ── */
.export-grid { display: flex; flex-direction: column; gap: 1rem; }

.export-card {
  background: var(--surface-1); border-radius: var(--border-radius-lg);
  border: 1px solid var(--border); box-shadow: var(--shadow-sm);
  padding: 1.25rem 1.5rem;
  display: flex; align-items: center; gap: 1.25rem;
  transition: box-shadow 0.2s, transform 0.15s;
}
.export-card:hover { box-shadow: var(--shadow-md); transform: translateY(-1px); }

.export-icon {
  width: 52px; height: 52px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  font-size: 1.4rem; flex-shrink: 0;
}

.export-info { flex: 1; }
.export-info h3 { font-size: 1rem; font-weight: 600; margin-bottom: 0.2rem; color: var(--color-gray-900); }
.export-info p  { font-size: 0.82rem; color: var(--color-gray-500); margin-bottom: 0.5rem; }

.export-cols {
  display: flex; gap: 0.4rem; flex-wrap: wrap;
}
.export-cols span {
  font-size: 0.72rem; padding: 0.15rem 0.5rem;
  background: var(--color-gray-100); border-radius: 4px;
  color: var(--color-gray-600); font-weight: 500;
}

.btn-export {
  display: inline-flex; align-items: center; gap: 0.5rem;
  padding: 0.55rem 1.25rem; border-radius: var(--border-radius);
  background: var(--color-primary); color: white; border: none;
  font-size: 0.88rem; font-weight: 600; cursor: pointer;
  white-space: nowrap; flex-shrink: 0; transition: all 0.15s;
}
.btn-export:hover:not(:disabled) { background: var(--color-primary-dark); box-shadow: 0 2px 8px rgba(5,150,105,0.35); }
.btn-export:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-export:active:not(:disabled) { transform: scale(0.97); }

.btn-spinner {
  width: 14px; height: 14px;
  border: 2px solid rgba(255,255,255,0.3); border-top-color: white;
  border-radius: 50%; animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 600px) {
  .export-card { flex-direction: column; align-items: flex-start; }
  .btn-export  { width: 100%; justify-content: center; }
}
</style>
