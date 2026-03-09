<template>
  <div class="container">
    <div class="d-flex justify-between align-center mb-3">
      <h1>Employés</h1>
      <button class="btn btn-primary" @click="openForm()">+ Nouvel employé</button>
    </div>

    <div v-if="loading" class="spinner"></div>

    <div v-else class="card">
      <table v-if="employes.length">
        <thead>
          <tr>
            <th>Nom</th>
            <th>Prénom</th>
            <th>Statut</th>
            <th>Créé le</th>
            <th>Créé par</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="emp in employes" :key="emp.id">
            <td>{{ emp.nom }}</td>
            <td>{{ emp.prenom }}</td>
            <td>
              <span :class="emp.statut === 'ACTIF' ? 'badge badge-success' : 'badge badge-danger'">
                {{ emp.statut }}
              </span>
            </td>
            <td>{{ formatDate(emp.createdAt) }}</td>
            <td>{{ emp.createdByUsername }}</td>
            <td class="d-flex gap-1">
              <button class="btn btn-secondary btn-sm" @click="openForm(emp)">Modifier</button>
              <button class="btn btn-danger btn-sm" @click="remove(emp.id)">Supprimer</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucun employé trouvé.</p>
    </div>

    <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
      <div class="modal-card">
        <h3>{{ editingId ? 'Modifier l\'employé' : 'Nouvel employé' }}</h3>
        <form @submit.prevent="save">
          <div class="form-group">
            <label>Nom *</label>
            <input v-model="form.nom" type="text" :class="{ error: errors.nom }" />
            <span v-if="errors.nom" class="error-message">{{ errors.nom }}</span>
          </div>
          <div class="form-group">
            <label>Prénom *</label>
            <input v-model="form.prenom" type="text" :class="{ error: errors.prenom }" />
            <span v-if="errors.prenom" class="error-message">{{ errors.prenom }}</span>
          </div>
          <div class="form-group">
            <label>Statut</label>
            <select v-model="form.statut">
              <option value="ACTIF">Actif</option>
              <option value="INACTIF">Inactif</option>
            </select>
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

const employes = ref([])
const loading = ref(false)
const showForm = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = ref({ nom: '', prenom: '', statut: 'ACTIF' })
const errors = ref({})

onMounted(loadEmployes)

async function loadEmployes() {
  loading.value = true
  try {
    const res = await api.get('/employes')
    employes.value = res.data
  } catch {
    toast.error('Impossible de charger les employés')
  } finally {
    loading.value = false
  }
}

function openForm(emp = null) {
  errors.value = {}
  if (emp) {
    editingId.value = emp.id
    form.value = { nom: emp.nom, prenom: emp.prenom, statut: emp.statut || 'ACTIF' }
  } else {
    editingId.value = null
    form.value = { nom: '', prenom: '', statut: 'ACTIF' }
  }
  showForm.value = true
}

function closeForm() {
  showForm.value = false
}

function validate() {
  const e = {}
  if (!form.value.nom.trim()) e.nom = 'Le nom est obligatoire'
  if (!form.value.prenom.trim()) e.prenom = 'Le prénom est obligatoire'
  errors.value = e
  return Object.keys(e).length === 0
}

async function save() {
  if (!validate()) return
  saving.value = true
  const payload = { nom: form.value.nom, prenom: form.value.prenom, statut: form.value.statut, createdById: authStore.currentUser?.id }
  try {
    if (editingId.value) {
      await api.put(`/employes/${editingId.value}`, payload)
      toast.success('Employé modifié')
    } else {
      await api.post('/employes', payload)
      toast.success('Employé créé')
    }
    closeForm()
    await loadEmployes()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Erreur lors de l\'enregistrement')
  } finally {
    saving.value = false
  }
}

async function remove(id) {
  if (!confirm('Supprimer cet employé ?')) return
  try {
    await api.delete(`/employes/${id}`)
    toast.success('Employé supprimé')
    await loadEmployes()
  } catch {
    toast.error('Impossible de supprimer cet employé')
  }
}

function formatDate(dt) {
  if (!dt) return '-'
  return new Date(dt).toLocaleDateString('fr-BE')
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
