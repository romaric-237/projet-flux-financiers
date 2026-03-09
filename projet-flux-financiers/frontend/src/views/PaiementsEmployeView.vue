<template>
  <div class="container">
    <div class="d-flex justify-between align-center mb-3">
      <h1>Paiements Employés</h1>
      <button class="btn btn-primary" @click="openForm()">+ Nouveau paiement</button>
    </div>

    <div v-if="loading" class="spinner"></div>

    <div v-else class="card">
      <table v-if="paiements.length">
        <thead>
          <tr>
            <th>Employé</th>
            <th>Type</th>
            <th>Montant</th>
            <th>Date</th>
            <th>Mode paiement</th>
            <th>Remarque</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in paiements" :key="p.id">
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
            <td class="d-flex gap-1">
              <button class="btn btn-secondary btn-sm" @click="openForm(p)">Modifier</button>
              <button class="btn btn-danger btn-sm" @click="remove(p.id)">Supprimer</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucun paiement employé trouvé.</p>
    </div>

    <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
      <div class="modal-card">
        <h3>{{ editingId ? 'Modifier le paiement' : 'Nouveau paiement employé' }}</h3>
        <form @submit.prevent="save">
          <div class="form-group">
            <label>Employé *</label>
            <select v-model="form.employeId" :class="{ error: errors.employeId }">
              <option value="">-- Sélectionner un employé --</option>
              <option v-for="e in employes" :key="e.id" :value="e.id">{{ e.nom }} {{ e.prenom }}</option>
            </select>
            <span v-if="errors.employeId" class="error-message">{{ errors.employeId }}</span>
          </div>
          <div class="form-group">
            <label>Type de paiement *</label>
            <select v-model="form.typePaiement" :class="{ error: errors.typePaiement }">
              <option value="">-- Sélectionner --</option>
              <option value="SALAIRE">Salaire</option>
              <option value="PRIME">Prime</option>
            </select>
            <span v-if="errors.typePaiement" class="error-message">{{ errors.typePaiement }}</span>
          </div>
          <div class="form-group">
            <label>Mode de paiement *</label>
            <select v-model="form.modePaiement" :class="{ error: errors.modePaiement }">
              <option value="">-- Sélectionner --</option>
              <option value="ESPECES">Espèces</option>
              <option value="VIREMENT">Virement</option>
              <option value="CHEQUE">Chèque</option>
              <option value="CARTE_BANCAIRE">Carte bancaire</option>
            </select>
            <span v-if="errors.modePaiement" class="error-message">{{ errors.modePaiement }}</span>
          </div>
          <div class="form-group">
            <label>Montant (€) *</label>
            <input v-model="form.montant" type="number" step="0.01" min="0.01" :class="{ error: errors.montant }" />
            <span v-if="errors.montant" class="error-message">{{ errors.montant }}</span>
          </div>
          <div class="form-group">
            <label>Date de paiement *</label>
            <input v-model="form.datePaiement" type="date" :class="{ error: errors.datePaiement }" />
            <span v-if="errors.datePaiement" class="error-message">{{ errors.datePaiement }}</span>
          </div>
          <div class="form-group">
            <label>Remarque</label>
            <textarea v-model="form.remarque" rows="2"></textarea>
          </div>
          <div class="d-flex gap-2 mt-2">
            <button type="submit" class="btn btn-primary" :disabled="saving">
              {{ saving ? 'Enregistrement...' : 'Enregistrer' }}
            </button>
            <button type="button" class="btn btn-secondary" @click="closeForm">Annuler</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

const toast = useToast()

const paiements = ref([])
const employes = ref([])
const loading = ref(false)
const showForm = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = ref({ employeId: '', typePaiement: '', modePaiement: '', montant: '', datePaiement: '', remarque: '' })
const errors = ref({})

onMounted(async () => {
  await Promise.all([loadPaiements(), loadEmployes()])
})

async function loadPaiements() {
  loading.value = true
  try {
    const res = await api.get('/paiement-employes')
    paiements.value = res.data
  } catch {
    toast.error('Impossible de charger les paiements')
  } finally {
    loading.value = false
  }
}

async function loadEmployes() {
  try {
    const res = await api.get('/employes')
    employes.value = res.data
  } catch {
    toast.error('Impossible de charger les employés')
  }
}

function openForm(p = null) {
  errors.value = {}
  if (p) {
    editingId.value = p.id
    form.value = {
      employeId: p.employeId,
      typePaiement: p.typePaiement || '',
      modePaiement: p.modePaiement || '',
      montant: p.montant,
      datePaiement: p.datePaiement,
      remarque: p.remarque || ''
    }
  } else {
    editingId.value = null
    form.value = { employeId: '', typePaiement: '', modePaiement: '', montant: '', datePaiement: '', remarque: '' }
  }
  showForm.value = true
}

function closeForm() {
  showForm.value = false
}

function validate() {
  const e = {}
  if (!form.value.employeId) e.employeId = 'L\'employé est obligatoire'
  if (!form.value.typePaiement) e.typePaiement = 'Le type de paiement est obligatoire'
  if (!form.value.modePaiement) e.modePaiement = 'Le mode de paiement est obligatoire'
  if (!form.value.montant || form.value.montant <= 0) e.montant = 'Le montant doit être supérieur à 0'
  if (!form.value.datePaiement) e.datePaiement = 'La date est obligatoire'
  errors.value = e
  return Object.keys(e).length === 0
}

async function save() {
  if (!validate()) return
  saving.value = true
  const payload = {
    employeId: form.value.employeId,
    typePaiement: form.value.typePaiement,
    modePaiement: form.value.modePaiement,
    montant: form.value.montant,
    datePaiement: form.value.datePaiement,
    remarque: form.value.remarque || null
  }
  try {
    if (editingId.value) {
      await api.put(`/paiement-employes/${editingId.value}`, payload)
      toast.success('Paiement modifié')
    } else {
      await api.post('/paiement-employes', payload)
      toast.success('Paiement créé')
    }
    closeForm()
    await loadPaiements()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Erreur lors de l\'enregistrement')
  } finally {
    saving.value = false
  }
}

async function remove(id) {
  if (!confirm('Supprimer ce paiement ?')) return
  try {
    await api.delete(`/paiement-employes/${id}`)
    toast.success('Paiement supprimé')
    await loadPaiements()
  } catch {
    toast.error('Impossible de supprimer ce paiement')
  }
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
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.modal-card {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  width: 100%;
  max-width: 520px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}
</style>
