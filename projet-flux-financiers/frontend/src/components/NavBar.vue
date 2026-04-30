<template>
  <nav class="navbar">
    <div class="navbar-container">

      <!-- Logo -->
      <router-link to="/" class="navbar-brand">
        <div class="brand-dot"></div>
        <span>Flux Financiers</span>
      </router-link>

      <!-- Menu principal -->
      <div :class="['navbar-menu', menuOpen ? 'menu-open' : '']">

        <router-link to="/" class="nav-link" @click="menuOpen = false">
          <span class="nav-icon">📊</span> Dashboard
        </router-link>

        <!-- Dropdown Gestion -->
        <div class="nav-dropdown" :class="{ open: gestionOpen }" v-click-outside="closeGestion">
          <button
            class="nav-link nav-dropdown-trigger"
            :class="{ active: isGestionActive }"
            @click="gestionOpen = !gestionOpen"
          >
            <span class="nav-icon">🗂️</span>
            Gestion
            <svg class="chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </button>
          <div class="dropdown-menu">
            <router-link to="/clients"  class="dropdown-item" @click="closeGestion(); menuOpen = false">
              <span>👥</span> Clients
            </router-link>
            <router-link to="/employes" class="dropdown-item" @click="closeGestion(); menuOpen = false">
              <span>👔</span> Employés
            </router-link>
            <router-link to="/charges"  class="dropdown-item" @click="closeGestion(); menuOpen = false">
              <span>📦</span> Charges
            </router-link>
          </div>
        </div>

        <router-link to="/budget"     class="nav-link" @click="menuOpen = false">
          <span class="nav-icon">🎯</span> Prévisions
        </router-link>
        <router-link to="/historique" class="nav-link" @click="menuOpen = false">
          <span class="nav-icon">📜</span> Historique
        </router-link>
        <router-link to="/export"     class="nav-link" @click="menuOpen = false">
          <span class="nav-icon">⬇️</span> Export
        </router-link>

        <!-- Liens Admin -->
        <template v-if="authStore.isAdmin">
          <div class="nav-separator"></div>
          <router-link to="/admin" class="nav-link nav-link-admin" @click="menuOpen = false">
            <span class="nav-icon">⚙️</span> Utilisateurs
          </router-link>
          <router-link to="/audit" class="nav-link nav-link-admin" @click="menuOpen = false">
            <span class="nav-icon">📋</span> Journal
          </router-link>
        </template>
      </div>

      <!-- Actions droite -->
      <div class="navbar-right">
        <div class="user-chip" @click="router.push('/profil')" title="Mon profil" style="cursor:pointer">
          <div class="user-avatar">{{ initials }}</div>
          <div class="user-info">
            <span class="user-name">{{ displayName }}</span>
            <span class="user-role" :class="authStore.isAdmin ? 'role-admin' : 'role-gest'">
              {{ authStore.currentUser?.role }}
            </span>
          </div>
        </div>
        <button @click="handleLogout" class="btn-logout" title="Déconnexion">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
        </button>
        <button class="hamburger" @click="menuOpen = !menuOpen" aria-label="Menu">
          <span :class="{ open: menuOpen }"></span>
          <span :class="{ open: menuOpen }"></span>
          <span :class="{ open: menuOpen }"></span>
        </button>
      </div>

    </div>
  </nav>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router    = useRouter()
const route     = useRoute()
const authStore = useAuthStore()
const menuOpen  = ref(false)
const gestionOpen = ref(false)

const GESTION_ROUTES = ['/clients', '/employes', '/charges']
const isGestionActive = computed(() => GESTION_ROUTES.includes(route.path))

function closeGestion() { gestionOpen.value = false }

const vClickOutside = {
  mounted(el, binding) {
    el._clickOutside = (e) => { if (!el.contains(e.target)) binding.value() }
    document.addEventListener('click', el._clickOutside)
  },
  unmounted(el) {
    document.removeEventListener('click', el._clickOutside)
  }
}

const displayName = computed(() => {
  const u = authStore.currentUser
  if (!u) return ''
  return u.prenom ? `${u.prenom} ${u.nom}` : u.username
})

const initials = computed(() => {
  const u = authStore.currentUser
  if (!u) return ''
  if (u.prenom && u.nom) return (u.prenom[0] + u.nom[0]).toUpperCase()
  return (u.username || '').substring(0, 2).toUpperCase()
})

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  position: fixed; top: 0; left: 0; right: 0; height: 60px;
  background: white; border-bottom: 1px solid var(--color-gray-200);
  box-shadow: var(--shadow-xs); z-index: 1000;
}

.navbar-container {
  max-width: 1400px; margin: 0 auto; height: 100%;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 1.5rem; gap: 1.5rem;
}

/* ── Brand ── */
.navbar-brand {
  display: flex; align-items: center; gap: 0.6rem;
  text-decoration: none; font-size: 1rem; font-weight: 700;
  color: var(--color-gray-900); letter-spacing: -0.01em; flex-shrink: 0;
}
.brand-dot {
  width: 28px; height: 28px; border-radius: 8px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  flex-shrink: 0;
}

/* ── Menu ── */
.navbar-menu {
  display: flex; align-items: center; gap: 0.25rem; flex: 1; justify-content: center;
}

.nav-separator {
  width: 1px; height: 20px; background: var(--color-gray-200); margin: 0 0.25rem;
}

.nav-link {
  display: flex; align-items: center; gap: 0.4rem;
  padding: 0.4rem 0.85rem; border-radius: var(--border-radius);
  text-decoration: none; font-size: 0.875rem; font-weight: 500;
  color: var(--color-gray-600); transition: all 0.15s; white-space: nowrap;
  background: none; border: none; cursor: pointer;
}
.nav-link:hover { background: var(--color-gray-100); color: var(--color-gray-900); }
.nav-link.router-link-active,
.nav-link.router-link-exact-active {
  background: var(--color-primary-light);
  color: var(--color-primary-dark); font-weight: 600;
}
.nav-link.active {
  background: var(--color-primary-light);
  color: var(--color-primary-dark); font-weight: 600;
}
.nav-link-admin { color: #7c3aed; }
.nav-link-admin:hover { background: #ede9fe; color: #6d28d9; }
.nav-link-admin.router-link-active { background: #ede9fe; color: #6d28d9; }
.nav-icon { font-size: 0.9rem; }

/* ── Dropdown Gestion ── */
.nav-dropdown { position: relative; }

.nav-dropdown-trigger { display: flex; align-items: center; gap: 0.4rem; }

.chevron {
  width: 13px; height: 13px; margin-left: 0.1rem;
  transition: transform 0.2s;
}
.nav-dropdown.open .chevron { transform: rotate(180deg); }

.dropdown-menu {
  display: none;
  position: absolute; top: calc(100% + 8px); left: 50%;
  transform: translateX(-50%);
  background: white; border: 1px solid var(--color-gray-200);
  border-radius: var(--border-radius-lg); box-shadow: var(--shadow-lg);
  padding: 0.35rem; min-width: 180px; z-index: 2000;
  animation: dropIn 0.15s ease;
}
.nav-dropdown.open .dropdown-menu { display: block; }

.dropdown-item {
  display: flex; align-items: center; gap: 0.6rem;
  padding: 0.55rem 0.85rem; border-radius: var(--border-radius);
  text-decoration: none; font-size: 0.875rem; font-weight: 500;
  color: var(--color-gray-700); transition: all 0.12s; white-space: nowrap;
}
.dropdown-item:hover { background: var(--color-gray-100); color: var(--color-gray-900); }
.dropdown-item.router-link-active {
  background: var(--color-primary-light); color: var(--color-primary-dark); font-weight: 600;
}

@keyframes dropIn {
  from { opacity: 0; transform: translateX(-50%) translateY(-6px); }
  to   { opacity: 1; transform: translateX(-50%) translateY(0); }
}

/* ── Droite ── */
.navbar-right { display: flex; align-items: center; gap: 0.75rem; flex-shrink: 0; }

.user-chip {
  display: flex; align-items: center; gap: 0.5rem;
  padding: 0.3rem 0.75rem 0.3rem 0.3rem;
  background: var(--color-gray-100); border-radius: 20px; transition: background 0.15s;
}
.user-chip:hover { background: var(--color-gray-200); }
.user-avatar {
  width: 28px; height: 28px; border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: white; font-size: 0.7rem; font-weight: 700;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.user-info { display: flex; flex-direction: column; }
.user-name {
  font-size: 0.82rem; font-weight: 500; color: var(--color-gray-700);
  max-width: 110px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; line-height: 1.2;
}
.user-role { font-size: 0.65rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.04em; }
.role-admin { color: #7c3aed; }
.role-gest  { color: var(--color-primary); }

.btn-logout {
  width: 34px; height: 34px; border: 1px solid var(--color-gray-200);
  border-radius: var(--border-radius); background: white; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  color: var(--color-gray-500); transition: all 0.15s;
}
.btn-logout:hover { border-color: var(--color-danger); color: var(--color-danger); background: var(--color-danger-light); }
.btn-logout svg { width: 16px; height: 16px; }

/* ── Hamburger ── */
.hamburger {
  display: none; flex-direction: column; justify-content: center;
  gap: 5px; background: none; border: none; cursor: pointer;
  padding: 4px; width: 34px; height: 34px;
}
.hamburger span {
  display: block; width: 20px; height: 2px;
  background: var(--color-gray-600); border-radius: 2px;
  transition: all 0.2s; transform-origin: center;
}
.hamburger span:nth-child(1).open { transform: translateY(7px) rotate(45deg); }
.hamburger span:nth-child(2).open { opacity: 0; transform: scaleX(0); }
.hamburger span:nth-child(3).open { transform: translateY(-7px) rotate(-45deg); }

/* ── Responsive ── */
@media (max-width: 900px) { .user-name { display: none; } .user-role { display: none; } }

@media (max-width: 768px) {
  .hamburger { display: flex; }
  .navbar-menu {
    display: none; position: fixed; top: 60px; left: 0; right: 0;
    background: white; flex-direction: column; align-items: stretch;
    gap: 0; padding: 0.5rem; border-bottom: 1px solid var(--color-gray-200);
    box-shadow: var(--shadow-md); z-index: 999;
  }
  .navbar-menu.menu-open { display: flex; }
  .nav-link { padding: 0.75rem 1rem; font-size: 0.9rem; }
  .nav-separator { width: auto; height: 1px; margin: 0.25rem 0; }
  .nav-dropdown { width: 100%; }
  .nav-dropdown-trigger { width: 100%; justify-content: flex-start; }
  .dropdown-menu {
    position: static; transform: none; box-shadow: none;
    border: none; border-left: 2px solid var(--color-primary-light);
    border-radius: 0; padding: 0 0 0 1rem; margin-left: 1rem;
    animation: none;
  }
  .nav-dropdown.open .dropdown-menu { display: block; }
}
</style>