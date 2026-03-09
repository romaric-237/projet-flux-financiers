<template>
  <div class="container">
    <div class="d-flex justify-between align-center mb-3">
      <h1>Clients</h1>
      <button class="btn btn-primary" @click="openForm()">+ Nouveau client</button>
    </div>

    <div class="card mb-2">
      <input
        v-model="search"
        type="text"
        placeholder="Rechercher par nom ou prénom..."
        style="max-width: 350px"
      />
    </div>

    <div v-if="loading" class="spinner"></div>

    <div v-else class="card">
      <table v-if="filteredClients.length">
        <thead>
          <tr>
            <th>Nom</th>
            <th>Prénom</th>
            <th>Créé le</th>
            <th>Créé par</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="client in filteredClients" :key="client.id">
            <td>{{ client.nom }}</td>
            <td>{{ client.prenom }}</td>
            <td>{{ formatDate(client.createdAt) }}</td>
            <td>{{ client.createdByUsername }}</td>
            <td class="d-flex gap-1">
              <button class="btn btn-secondary btn-sm" @click="openForm(client)">Modifier</button>
              <button class="btn btn-danger btn-sm" @click="remove(client.id)">Supprimer</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucun client trouvé.</p>
      <p v-if="clients.length" class="text-muted mt-1" style="font-size:0.875rem">
        {{ filteredClients.length }} / {{ clients.length }} client(s)
      </p>
    </div>

    <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
      <div class="modal-card">
        <h3>{{ editingId ? 'Modifier le client' : 'Nouveau client' }}</h3>
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
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const toast = useToast()
const authStore = useAuthStore()

const clients = ref([])
const search = ref('')
const loading = ref(false)

const filteredClients = computed(() => {
  const q = search.value.trim().toLowerCase()
  if (!q) return clients.value
  return clients.value.filter(c =>
    c.nom.toLowerCase().includes(q) || c.prenom.toLowerCase().includes(q)
  )
})
const showForm = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = ref({ nom: '', prenom: '' })
const errors = ref({})

onMounted(loadClients)

async function loadClients() {
  loading.value = true
  try {
    const res = await api.get('/clients')
    clients.value = res.data
  } catch {
    toast.error('Impossible de charger les clients')
  } finally {
    loading.value = false
  }
}

function openForm(client = null) {
  errors.value = {}
  if (client) {
    editingId.value = client.id
    form.value = { nom: client.nom, prenom: client.prenom }
  } else {
    editingId.value = null
    form.value = { nom: '', prenom: '' }
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
  const payload = { nom: form.value.nom, prenom: form.value.prenom, createdById: authStore.currentUser?.id }
  try {
    if (editingId.value) {
      await api.put(`/clients/${editingId.value}`, payload)
      toast.success('Client modifié')
    } else {
      await api.post('/clients', payload)
      toast.success('Client créé')
    }
    closeForm()
    await loadClients()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Erreur lors de l\'enregistrement')
  } finally {
    saving.value = false
  }
}

async function remove(id) {
  if (!confirm('Supprimer ce client ?')) return
  try {
    await api.delete(`/clients/${id}`)
    toast.success('Client supprimé')
    await loadClients()
  } catch {
    toast.error('Impossible de supprimer ce client')
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
