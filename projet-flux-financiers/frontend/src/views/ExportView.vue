<template>
  <div class="container">
    <h1>Export</h1>
    <p class="text-muted mb-3">Exportez les données en format Excel ou CSV avec un filtre de dates optionnel.</p>

    <div class="filters card">
      <h4>Filtres communs</h4>
      <div class="filter-row">
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
            <option value="EXCEL">Excel (.xlsx)</option>
            <option value="CSV">CSV (.csv)</option>
          </select>
        </div>
      </div>
    </div>

    <div class="export-cards">
      <div class="card export-card">
        <h3>Versements clients</h3>
        <p class="text-muted">Tous les versements effectués par les clients.</p>
        <button class="btn btn-primary" :disabled="downloading === 'versements'" @click="download('versements')">
          {{ downloading === 'versements' ? 'Téléchargement...' : 'Exporter' }}
        </button>
      </div>

      <div class="card export-card">
        <h3>Paiements charges</h3>
        <p class="text-muted">Tous les paiements de charges (véhicules, infrastructure, etc.).</p>
        <button class="btn btn-primary" :disabled="downloading === 'paiements-charges'" @click="download('paiements-charges')">
          {{ downloading === 'paiements-charges' ? 'Téléchargement...' : 'Exporter' }}
        </button>
      </div>

      <div class="card export-card">
        <h3>Paiements employés</h3>
        <p class="text-muted">Tous les paiements effectués aux employés (salaires, primes).</p>
        <button class="btn btn-primary" :disabled="downloading === 'paiements-employes'" @click="download('paiements-employes')">
          {{ downloading === 'paiements-employes' ? 'Téléchargement...' : 'Exporter' }}
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

const dateDebut = ref('')
const dateFin = ref('')
const format = ref('EXCEL')
const downloading = ref(null)

async function download(type) {
  downloading.value = type
  try {
    const params = { format: format.value }
    if (dateDebut.value) params.dateDebut = dateDebut.value
    if (dateFin.value) params.dateFin = dateFin.value

    const res = await api.get(`/export/${type}`, { params, responseType: 'blob' })

    const ext = format.value === 'CSV' ? 'csv' : 'xlsx'
    const filename = `${type}_${new Date().toISOString().slice(0, 10)}.${ext}`

    const url = URL.createObjectURL(res.data)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    a.click()
    URL.revokeObjectURL(url)

    toast.success(`Fichier "${filename}" téléchargé`)
  } catch {
    toast.error('Erreur lors de l\'export')
  } finally {
    downloading.value = null
  }
}
</script>

<style scoped>
.filter-row {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}
.filter-row .form-group {
  flex: 1;
  min-width: 160px;
}
.export-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}
.export-card {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
.filters {
  margin-bottom: 1.5rem;
}
</style>
