<template>
  <div class="container">
    <div class="d-flex justify-between align-center mb-3">
      <h1>Paiements Charges</h1>
      <button class="btn btn-primary" @click="openForm()">+ Nouveau paiement</button>
    </div>

    <div v-if="loading" class="spinner"></div>

    <div v-else class="card">
      <table v-if="paiements.length">
        <thead>
          <tr>
            <th>Charge</th>
            <th>Montant</th>
            <th>Date</th>
            <th>Mode paiement</th>
            <th>Remarque</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in paiements" :key="p.id">
            <td>{{ p.chargeNom }}</td>
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
      <p v-else class="text-muted text-center mt-2">Aucun paiement de charge trouvé.</p>
    </div>

    <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
      <div class="modal-card">
        <h3>{{ editingId ? 'Modifier le paiement' : 'Nouveau paiement de charge' }}</h3>
        <form @submit.prevent="save">
          <div class="form-group">
            <label>Charge *</label>
            <select v-model="form.chargeId" :class="{ error: errors.chargeId }">
              <option value="">-- Sélectionner une charge --</option>
              <option v-for="c in charges" :key="c.id" :value="c.id">{{ c.libelle }}</option>
            </select>
            <span v-if="errors.chargeId" class="error-message">{{ errors.chargeId }}</span>
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

const paiements = ref([])
const charges = ref([])
const loading = ref(false)
const showForm = ref(false)
const saving = ref(false)
const editingId = ref(null)
const form = ref({ chargeId: '', montant: '', datePaiement: '', modePaiement: '', remarque: '' })
const errors = ref({})

onMounted(async () => {
  await Promise.all([loadPaiements(), loadCharges()])
  if (route.query.action === 'new') openForm()
})

async function loadPaiements() {
  loading.value = true
  try {
    const res = await api.get('/paiement-charges')
    paiements.value = res.data
  } catch {
    toast.error('Impossible de charger les paiements')
  } finally {
    loading.value = false
  }
}

async function loadCharges() {
  try {
    const res = await api.get('/charges')
    charges.value = res.data
  } catch {
    toast.error('Impossible de charger les charges')
  }
}

function openForm(p = null) {
  errors.value = {}
  if (p) {
    editingId.value = p.id
    form.value = {
      chargeId: p.chargeId,
      montant: p.montant,
      datePaiement: p.datePaiement,
      modePaiement: p.modePaiement || '',
      remarque: p.remarque || ''
    }
  } else {
    editingId.value = null
    form.value = { chargeId: '', montant: '', datePaiement: '', modePaiement: '', remarque: '' }
  }
  showForm.value = true
}

function closeForm() {
  showForm.value = false
}

function validate() {
  const e = {}
  if (!form.value.chargeId) e.chargeId = 'La charge est obligatoire'
  if (!form.value.montant || form.value.montant <= 0) e.montant = 'Le montant doit être supérieur à 0'
  if (!form.value.datePaiement) {
    e.datePaiement = 'La date est obligatoire'
  } else if (form.value.datePaiement > new Date().toISOString().split('T')[0]) {
    e.datePaiement = 'La date ne peut pas être dans le futur'
  }
  if (!form.value.modePaiement) e.modePaiement = 'Le mode de paiement est obligatoire'
  errors.value = e
  return Object.keys(e).length === 0
}

async function save() {
  if (!validate()) return
  saving.value = true
  const payload = {
    chargeId: form.value.chargeId,
    montant: parseFloat(form.value.montant),
    date: form.value.datePaiement,
    modePaiement: form.value.modePaiement,
    remarque: form.value.remarque || null
  }
  try {
    if (editingId.value) {
      await api.put(`/paiement-charges/${editingId.value}`, payload)
      toast.success('Paiement modifié')
    } else {
      await api.post('/paiement-charges', payload)
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
    await api.delete(`/paiement-charges/${id}`)
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
  const labels = { ESPECES: 'Espèces', VIREMENT: 'Virement', CHEQUE: 'Chèque', CARTE: 'Carte bancaire' }
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
