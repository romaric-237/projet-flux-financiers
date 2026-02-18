<template>
  <nav class="navbar">
    <div class="navbar-container">
      <div class="navbar-brand">
        <router-link to="/" class="brand-link">
          🏦 Gestion Flux Financiers
        </router-link>
      </div>
      
      <div class="navbar-menu">
        <router-link to="/" class="nav-link">Dashboard</router-link>
        <router-link to="/clients" class="nav-link">Clients</router-link>
        <router-link to="/employes" class="nav-link">Employés</router-link>
        <router-link to="/charges" class="nav-link">Charges</router-link>
        <router-link to="/historique" class="nav-link">Historique</router-link>
        <router-link to="/export" class="nav-link">Export</router-link>
      </div>
      
      <div class="navbar-actions">
        <span class="user-info">👤 {{ authStore.currentUser?.username || 'Utilisateur' }}</span>
        <button @click="handleLogout" class="btn btn-secondary btn-sm">
          Déconnexion
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.navbar-container {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
}

.navbar-brand .brand-link {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-primary);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.navbar-menu {
  display: flex;
  gap: 1.5rem;
  flex: 1;
  justify-content: center;
}

.nav-link {
  color: var(--color-gray-700);
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: var(--border-radius);
  transition: all 0.2s;
}

.nav-link:hover {
  background-color: var(--color-primary-light);
  color: var(--color-primary);
}

.nav-link.router-link-active {
  background-color: var(--color-primary);
  color: white;
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-info {
  font-size: 0.9rem;
  color: var(--color-gray-600);
  font-weight: 500;
}

@media (max-width: 768px) {
  .navbar-menu {
    display: none;
  }
}
</style>
