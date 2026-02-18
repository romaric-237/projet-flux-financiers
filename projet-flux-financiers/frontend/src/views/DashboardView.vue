<template>
  <div class="container">
    <h1>📊 Tableau de Bord</h1>
    <p class="text-muted">Bienvenue {{ authStore.currentUser?.username }} !</p>
    
    <div class="card">
      <h3>✅ Structure de base installée !</h3>
      <p> </p>
      
      <div class="mt-3">
        <h4>Pages disponibles :</h4>
        <ul>
          <li><router-link to="/clients">📋 Clients</router-link></li>
          <li><router-link to="/employes">👥 Employés</router-link></li>
          <li><router-link to="/charges">💰 Charges</router-link></li>
          <li><router-link to="/versements">💵 Versements</router-link></li>
          <li><router-link to="/paiements-employe">💳 Paiements Employés</router-link></li>
          <li><router-link to="/paiements-charge">🧾 Paiements Charges</router-link></li>
          <li><router-link to="/historique">📅 Historique</router-link></li>
          <li><router-link to="/export">📥 Export Excel</router-link></li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
</script>

<style scoped>
ul {
  list-style: none;
  padding-left: 0;
}

li {
  margin: 0.5rem 0;
}

li a {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
}

li a:hover {
  text-decoration: underline;
}
</style>
