<template>
  <div class="container">
    <div class="d-flex justify-between align-center mb-3">
      <h1>Versements</h1>
      <button class="btn btn-primary" @click="openForm()">+ Nouveau versement</button>
    </div>

    <div v-if="loading" class="spinner"></div>

    <div v-else class="card">
      <table v-if="versements.length">
        <thead>
          <tr>
            <th>Client</th>
            <th>Montant TTC</th>
            <th>Date</th>
            <th>Mode paiement</th>
            <th>Remarque</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="v in versements" :key="v.id">
            <td>{{ v.clientNom }}</td>
            <td>{{ formatMontant(v.montantTTC) }}</td>
            <td>{{ v.dateVersement }}</td>
            <td>{{ formatMode(v.modePaiement) }}</td>
            <td>{{ v.remarque || '-' }}</td>
            <td class="d-flex gap-1">
              <button class="btn btn-secondary btn-sm" @click="openForm(v)">Modifier</button>
              <button class="btn btn-danger btn-sm" @click="remove(v.id)">Supprimer</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="text-muted text-center mt-2">Aucun versement trouvé.</p>
    </div>

    <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
      <div class="modal-card">
        <h3>{{ editingId ? 'Modifier le versement' : 'Nouveau versement' }}</h3>
        <form @submit.prevent="save">
          <div class="form-group">
            <label>Client *</label>
            <select v-model="form.clientId" :class="{ error: errors.clientId }">
              <option value="">-- Sélectionner un client --</option>
              <option v-for="c in clients" :key="c.id" :value="c.id">{{ c.nom }} {{ c.prenom }}</option>
            </select>
            <span v-if="errors.clientId" class="error-message">{{ errors.clientId }}</span>
          </div>
          <div class="form-group">
            <label>Montant TTC (€) *</label>
            <input v-model="form.montant" type="number" step="0.01" min="0.01" :class="{ error: errors.montant }" />
            <span v-if="errors.montant" class="error-message">{{ errors.montant }}</span>
          </div>
          <div class="form-group">
            <label>Date de versement *</label>
            <input v-model="form.date" type="date" :class="{ error: errors.date }" />
            <span v-if="errors.date" class="error-message">{{ errors.date }}</span>
          </div>
          <div class="form-group">
            <label>Mode de paiement *</label>
            <select v-model="form.modePaiement" :class="{ error: errors.modePaiement }">
              <option value="">-- Sélectionner --</option>
              <option value="ESPECES">Espèces</option>
              <option value="VIREMENT">Virement</option>
              <option value="CHEQUE">Chèque</option>
              <option value="CARTE">Carte bancaire</option>
            </select>
            <span v-if="errors.modePaiement" class="error-message">{{ errors.modePaiement }}</span>
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
import { useRoute } from 'vue-router'
import api from '@/services/api'
import { useToast } from 'vue-toastification'

const toast = useToast()
const route = useRoute()

const versements = ref([])
const clients = ref([])
const loading = ref(false)
const showForm = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = ref({ clientId: '', montant: '', date: '', modePaiement: '', remarque: '' })
const errors = ref({})

onMounted(async () => {
  await Promise.all([loadVersements(), loadClients()])
  if (route.query.action === 'new') openForm()
})

async function loadVersements() {
  loading.value = true
  try {
    const res = await api.get('/versements')
    versements.value = res.data
  } catch {
    toast.error('Impossible de charger les versements')
  } finally {
    loading.value = false
  }
}

async function loadClients() {
  try {
    const res = await api.get('/clients')
    clients.value = res.data
  } catch {
    toast.error('Impossible de charger les clients')
  }
}

function openForm(v = null) {
  errors.value = {}
  if (v) {
    editingId.value = v.id
    form.value = {
      clientId: v.clientId,
      montant: v.montantTTC,
      date: v.dateVersement,
      modePaiement: v.modePaiement || '',
      remarque: v.remarque || ''
    }
  } else {
    editingId.value = null
    form.value = { clientId: '', montant: '', date: '', modePaiement: '', remarque: '' }
  }
  showForm.value = true
}

function closeForm() {
  showForm.value = false
}

function validate() {
  const e = {}
  if (!form.value.clientId) e.clientId = 'Le client est obligatoire'
  if (!form.value.montant || form.value.montant <= 0) e.montant = 'Le montant doit être supérieur à 0'
  if (!form.value.modePaiement) e.modePaiement = 'Le mode de paiement est obligatoire'
  if (!form.value.date) {
    e.date = 'La date est obligatoire'
  } else if (form.value.date > new Date().toISOString().split('T')[0]) {
    e.date = 'La date ne peut pas être dans le futur'
  }
  errors.value = e
  return Object.keys(e).length === 0
}

async function save() {
  if (!validate()) return
  saving.value = true
  const payload = {
    clientId: form.value.clientId,
    montant: parseFloat(form.value.montant),
    date: form.value.date,
    modePaiement: form.value.modePaiement || null,
    remarque: form.value.remarque || null
  }
  try {
    if (editingId.value) {
      await api.put(`/versements/${editingId.value}`, payload)
      toast.success('Versement modifié')
    } else {
      await api.post('/versements', payload)
      toast.success('Versement créé')
    }
    closeForm()
    await loadVersements()
  } catch (err) {
    console.error('Erreur versement:', err.response?.data)
    const data = err.response?.data
    if (data?.errors) {
      const details = Object.entries(data.errors).map(([k, v]) => `${k}: ${v}`).join(' | ')
      toast.error(details)
    } else {
      toast.error(data?.message || 'Erreur lors de l\'enregistrement')
    }
  } finally {
    saving.value = false
  }
}

async function remove(id) {
  if (!confirm('Supprimer ce versement ?')) return
  try {
    await api.delete(`/versements/${id}`)
    toast.success('Versement supprimé')
    await loadVersements()
  } catch {
    toast.error('Impossible de supprimer ce versement')
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
.container { max-width: 1100px; margin: 0 auto; }

.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(15, 23, 42, 0.5); backdrop-filter: blur(2px);
  display: flex; align-items: center; justify-content: center;
  z-index: 2000; padding: 1rem; animation: fadeIn 0.15s ease;
}
.modal-card {
  background: white; border-radius: var(--border-radius-xl);
  width: 100%; max-width: 520px; max-height: 90vh; overflow-y: auto;
  box-shadow: var(--shadow-xl); animation: slideUp 0.2s ease;
}
.modal-card h3 {
  font-size: 1rem; font-weight: 600; margin: 0;
  padding: 1.1rem 1.5rem;
  background: var(--color-gray-50); border-bottom: 1px solid var(--color-gray-200);
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
}
form { padding: 1.5rem; }

@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes slideUp {
  from { opacity: 0; transform: translateY(10px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}
</style>
