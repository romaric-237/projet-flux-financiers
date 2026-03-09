<template>
  <div class="container">
    <h1>Historique</h1>

    <div class="tabs mb-3">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['btn', activeTab === tab.key ? 'btn-primary' : 'btn-secondary', 'btn-sm']"
        @click="setTab(tab.key)"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-if="loading" class="spinner"></div>

    <!-- Versements -->
    <div v-else-if="activeTab === 'versements'" class="card">
      <table v-if="versements.length">
        <thead>
          <tr>
            <th>Client</th>
            <th>Montant TTC</th>
            <th>Date</th>
            <th>Mode paiement</th>
            <th>Remarque</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="v in versements" :key="v.id">
            <td>{{ v.clientNom }}</td>
            <td>{{ formatMontant(v.montantTTC) }}</td>
            <td>{{ v.dateVersement }}</td>
            <td>{{ formatMode(v.modePaiement) }}</td>
            <td>{{ v.remarque || '-' }}</td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucun versement.</p>
    </div>

    <!-- Paiements Charges -->
    <div v-else-if="activeTab === 'charges'" class="card">
      <table v-if="paiementsCharges.length">
        <thead>
          <tr>
            <th>Charge</th>
            <th>Montant</th>
            <th>Date</th>
            <th>Mode paiement</th>
            <th>Remarque</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in paiementsCharges" :key="p.id">
            <td>{{ p.chargeNom }}</td>
            <td>{{ formatMontant(p.montant) }}</td>
            <td>{{ p.datePaiement }}</td>
            <td>{{ formatMode(p.modePaiement) }}</td>
            <td>{{ p.remarque || '-' }}</td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucun paiement de charge.</p>
    </div>

    <!-- Paiements Employés -->
    <div v-else-if="activeTab === 'employes'" class="card">
      <table v-if="paiementsEmployes.length">
        <thead>
          <tr>
            <th>Employé</th>
            <th>Type</th>
            <th>Montant</th>
            <th>Date</th>
            <th>Mode paiement</th>
            <th>Remarque</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in paiementsEmployes" :key="p.id">
            <td>{{ p.employeNom }}</td>
            <td>
              <span :class="p.typePaiement === 'SALAIRE' ? 'badge badge-success' : 'badge badge-secondary'">
                {{ p.typePaiement }}
              </span>
            </td>
            <td>{{ formatMontant(p.montant) }}</td>
            <td>{{ p.datePaiement }}</td>
            <td>{{ formatMode(p.modePaiement) }}</td>
            <td>{{ p.remarque || '-' }}</td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucun paiement employé.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

const toast = useToast()

const tabs = [
  { key: 'versements', label: 'Versements clients' },
  { key: 'charges', label: 'Paiements charges' },
  { key: 'employes', label: 'Paiements employés' }
]

const activeTab = ref('versements')
const loading = ref(false)
const versements = ref([])
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
    versements.value = v.data
    paiementsCharges.value = pc.data
    paiementsEmployes.value = pe.data
  } catch {
    toast.error('Impossible de charger l\'historique')
  } finally {
    loading.value = false
  }
}

function setTab(key) {
  activeTab.value = key
}

function formatMontant(m) {
  return new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' }).format(m)
}

function formatMode(mode) {
  const labels = { ESPECES: 'Espèces', VIREMENT: 'Virement', CHEQUE: 'Chèque', CARTE_BANCAIRE: 'Carte bancaire' }
  return labels[mode] || mode || '-'
}
</script>

<style scoped>
.tabs {
  display: flex;
  gap: 0.5rem;
}
</style>
