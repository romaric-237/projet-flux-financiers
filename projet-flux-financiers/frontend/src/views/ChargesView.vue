<template>
  <div class="container">
    <div class="d-flex justify-between align-center mb-3">
      <h1>Charges</h1>
      <button class="btn btn-primary" @click="openForm()">+ Nouvelle charge</button>
    </div>

    <div v-if="loading" class="spinner"></div>

    <div v-else class="card">
      <table v-if="charges.length">
        <thead>
          <tr>
            <th>Nom</th>
            <th>Type</th>
            <th>Créé le</th>
            <th>Créé par</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="charge in charges" :key="charge.id">
            <td>{{ charge.nomCharge }}</td>
            <td><span class="badge badge-secondary">{{ formatType(charge.typeCharge) }}</span></td>
            <td>{{ formatDate(charge.createdAt) }}</td>
            <td>{{ charge.createdByUsername }}</td>
            <td class="d-flex gap-1">
              <button class="btn btn-secondary btn-sm" @click="openForm(charge)">Modifier</button>
              <button class="btn btn-danger btn-sm" @click="remove(charge.id)">Supprimer</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucune charge trouvée.</p>
    </div>

    <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
      <div class="modal-card">
        <h3>{{ editingId ? 'Modifier la charge' : 'Nouvelle charge' }}</h3>
        <form @submit.prevent="save">
          <div class="form-group">
            <label>Nom de la charge *</label>
            <input v-model="form.nomCharge" type="text" :class="{ error: errors.nomCharge }" />
            <span v-if="errors.nomCharge" class="error-message">{{ errors.nomCharge }}</span>
          </div>
          <div class="form-group">
            <label>Type *</label>
            <select v-model="form.typeCharge" :class="{ error: errors.typeCharge }">
              <option value="">-- Sélectionner --</option>
              <option value="VEHICULES">Véhicules</option>
              <option value="INFRASTRUCTURE">Infrastructure</option>
              <option value="FISCALES_SOCIALES">Fiscales & Sociales</option>
              <option value="SERVICES_EXTERNES">Services Externes</option>
            </select>
            <span v-if="errors.typeCharge" class="error-message">{{ errors.typeCharge }}</span>
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
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const toast = useToast()
const authStore = useAuthStore()

const charges = ref([])
const loading = ref(false)
const showForm = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = ref({ nomCharge: '', typeCharge: '' })
const errors = ref({})

onMounted(loadCharges)

async function loadCharges() {
  loading.value = true
  try {
    const res = await api.get('/charges')
    charges.value = res.data
  } catch {
    toast.error('Impossible de charger les charges')
  } finally {
    loading.value = false
  }
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

function closeForm() {
  showForm.value = false
}

function validate() {
  const e = {}
  if (!form.value.nomCharge.trim()) e.nomCharge = 'Le nom est obligatoire'
  if (!form.value.typeCharge) e.typeCharge = 'Le type est obligatoire'
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
    await loadCharges()
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
    await loadCharges()
  } catch {
    toast.error('Impossible de supprimer cette charge')
  }
}

function formatDate(dt) {
  if (!dt) return '-'
  return new Date(dt).toLocaleDateString('fr-BE')
}

function formatType(type) {
  const labels = {
    VEHICULES: 'Véhicules',
    INFRASTRUCTURE: 'Infrastructure',
    FISCALES_SOCIALES: 'Fiscales & Sociales',
    SERVICES_EXTERNES: 'Services Externes'
  }
  return labels[type] || type
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
  max-width: 480px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}
</style>
