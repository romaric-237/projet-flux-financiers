<template>

  <!-- ── Overlay mobile ── -->
  <div v-if="sidebarOpen" class="sidebar-overlay" @click="sidebarOpen = false"></div>

  <!-- ══════════════════════════════════════════
       SIDEBAR
  ══════════════════════════════════════════ -->
  <aside :class="['sidebar', { 'sidebar-open': sidebarOpen }]">

    <!-- Logo -->
    <div class="sidebar-logo">
      <div class="logo-icon">
        <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
          <rect width="32" height="32" rx="9" fill="var(--accent-cyan)"/>
          <path d="M8 24V15l8-5 8 5v9h-5v-5h-6v5H8Z" fill="#000" opacity="0.9"/>
          <rect x="13" y="19" width="6" height="5" rx="1" fill="rgba(0,0,0,0.45)"/>
        </svg>
      </div>
      <div class="logo-text">
        <span class="logo-name">Flux Financiers</span>
        <span class="logo-org">ASBL Terra Sana</span>
      </div>
    </div>

    <!-- Navigation -->
    <nav class="sidebar-nav">

      <router-link to="/" class="nav-item" exact-active-class="nav-active" @click="sidebarOpen = false">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <rect x="3" y="3" width="7" height="7" rx="1"/>
          <rect x="14" y="3" width="7" height="7" rx="1"/>
          <rect x="3" y="14" width="7" height="7" rx="1"/>
          <rect x="14" y="14" width="7" height="7" rx="1"/>
        </svg>
        Dashboard
      </router-link>

      <!-- Section Gestion -->
      <p class="nav-section-label">Gestion</p>

      <router-link to="/clients" class="nav-item" active-class="nav-active" @click="sidebarOpen = false">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
          <circle cx="9" cy="7" r="4"/>
          <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
        </svg>
        Clients
      </router-link>

      <router-link to="/employes" class="nav-item" active-class="nav-active" @click="sidebarOpen = false">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
          <circle cx="12" cy="7" r="4"/>
        </svg>
        Employés
      </router-link>

      <router-link to="/charges" class="nav-item" active-class="nav-active" @click="sidebarOpen = false">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
        </svg>
        Charges
      </router-link>

      <!-- Section Finance -->
      <p class="nav-section-label">Finance</p>

      <router-link to="/versements" class="nav-item" active-class="nav-active" @click="sidebarOpen = false">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <line x1="12" y1="1" x2="12" y2="23"/>
          <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
        </svg>
        Versements
      </router-link>

      <router-link to="/paiements-employe" class="nav-item" active-class="nav-active" @click="sidebarOpen = false">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <rect x="2" y="5" width="20" height="14" rx="2"/>
          <line x1="2" y1="10" x2="22" y2="10"/>
        </svg>
        Paie. personnel
      </router-link>

      <router-link to="/paiements-charge" class="nav-item" active-class="nav-active" @click="sidebarOpen = false">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14 2 14 8 20 8"/>
          <line x1="16" y1="13" x2="8" y2="13"/>
          <line x1="16" y1="17" x2="8" y2="17"/>
        </svg>
        Paie. charges
      </router-link>

      <router-link to="/budget" class="nav-item" active-class="nav-active" @click="sidebarOpen = false">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <path d="M22 12h-4l-3 9L9 3l-3 9H2"/>
        </svg>
        Prévisions
      </router-link>

      <router-link to="/historique" class="nav-item" active-class="nav-active" @click="sidebarOpen = false">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <polyline points="12 8 12 12 14 14"/>
          <path d="M3.05 11a9 9 0 1 1 .5 4m-.5 5v-5h5"/>
        </svg>
        Historique
      </router-link>

      <router-link to="/export" class="nav-item" active-class="nav-active" @click="sidebarOpen = false">
        <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
          <polyline points="7 10 12 15 17 10"/>
          <line x1="12" y1="15" x2="12" y2="3"/>
        </svg>
        Export
      </router-link>

      <!-- Section Admin -->
      <template v-if="authStore.isAdmin">
        <p class="nav-section-label nav-section-admin">Administration</p>

        <router-link to="/admin" class="nav-item nav-item-admin" active-class="nav-active nav-active-admin" @click="sidebarOpen = false">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <circle cx="12" cy="12" r="3"/>
            <path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14"/>
          </svg>
          Utilisateurs
        </router-link>

        <router-link to="/audit" class="nav-item nav-item-admin" active-class="nav-active nav-active-admin" @click="sidebarOpen = false">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="16" y1="13" x2="8" y2="13"/>
          </svg>
          Journal d'audit
        </router-link>
      </template>

    </nav>

    <!-- User section -->
    <div class="sidebar-user">
      <div class="user-info-row" @click="router.push('/profil')" title="Mon profil">
        <div class="user-avatar">{{ initials }}</div>
        <div class="user-text">
          <span class="user-name">{{ displayName }}</span>
          <span class="user-role" :class="authStore.isAdmin ? 'role-admin' : 'role-gest'">
            {{ authStore.currentUser?.role }}
          </span>
        </div>
      </div>
      <button class="theme-btn" @click="toggleTheme" :title="isDark ? 'Mode jour' : 'Mode nuit'">
        <svg v-if="isDark" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="5"/>
          <line x1="12" y1="1" x2="12" y2="3"/>
          <line x1="12" y1="21" x2="12" y2="23"/>
          <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
          <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
          <line x1="1" y1="12" x2="3" y2="12"/>
          <line x1="21" y1="12" x2="23" y2="12"/>
          <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
          <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
        </svg>
      </button>
      <button class="logout-btn" @click="handleLogout" title="Déconnexion">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
          <polyline points="16 17 21 12 16 7"/>
          <line x1="21" y1="12" x2="9" y2="12"/>
        </svg>
      </button>
    </div>

  </aside>

  <!-- ── Topbar mobile ── -->
  <header class="mobile-topbar">
    <button class="hamburger-btn" @click="sidebarOpen = !sidebarOpen" aria-label="Menu">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <line x1="3" y1="6"  x2="21" y2="6"/>
        <line x1="3" y1="12" x2="21" y2="12"/>
        <line x1="3" y1="18" x2="21" y2="18"/>
      </svg>
    </button>
    <span class="mobile-logo">Flux Financiers</span>
    <div class="mobile-right">
      <button class="theme-btn" @click="toggleTheme" :title="isDark ? 'Mode jour' : 'Mode nuit'">
        <svg v-if="isDark" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="5"/>
          <line x1="12" y1="1" x2="12" y2="3"/>
          <line x1="12" y1="21" x2="12" y2="23"/>
          <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
          <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
          <line x1="1" y1="12" x2="3" y2="12"/>
          <line x1="21" y1="12" x2="23" y2="12"/>
          <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
          <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
        </svg>
      </button>
      <div class="user-avatar mobile-avatar">{{ initials }}</div>
    </div>
  </header>

</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useTheme } from '@/composables/useTheme'

const router    = useRouter()
const authStore = useAuthStore()
const sidebarOpen = ref(false)
const { isDark, toggleTheme } = useTheme()

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
/* ══════════════════════════════════════════
   SIDEBAR
══════════════════════════════════════════ */
.sidebar {
  position: fixed;
  top: 0; left: 0; bottom: 0;
  width: var(--sidebar-width);
  background: var(--surface-1);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  z-index: 1000;
  overflow-y: auto;
  overflow-x: hidden;
}

/* ── Logo ── */
.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1.25rem 1rem 1.1rem;
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
}
.logo-icon { width: 32px; height: 32px; flex-shrink: 0; }
.logo-icon svg { width: 100%; height: 100%; }
.logo-text  { display: flex; flex-direction: column; min-width: 0; }
.logo-name  { font-size: 0.9rem; font-weight: 700; color: var(--text-primary); letter-spacing: -0.01em; white-space: nowrap; }
.logo-org   { font-size: 0.67rem; color: var(--text-tertiary); font-family: var(--font-mono); text-transform: uppercase; letter-spacing: 0.08em; margin-top: 0.05rem; }

/* ── Nav ── */
.sidebar-nav {
  flex: 1;
  padding: 0.75rem 0.6rem;
  display: flex;
  flex-direction: column;
  gap: 1px;
  overflow-y: auto;
}

.nav-section-label {
  font-family: var(--font-mono);
  font-size: 0.6rem;
  font-weight: 600;
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  padding: 1rem 0.5rem 0.3rem;
  margin: 0;
}
.nav-section-admin { color: rgba(129, 140, 248, 0.7); }

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  padding: 0.5rem 0.75rem;
  border-radius: var(--border-radius);
  text-decoration: none;
  font-size: 0.845rem;
  font-weight: 500;
  color: var(--text-tertiary);
  transition: all 0.12s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.nav-item:hover { background: var(--hover-overlay); color: var(--text-primary); }

.nav-active {
  background: rgba(34, 211, 238, 0.1) !important;
  color: var(--accent-cyan) !important;
}
.nav-active .nav-icon { color: var(--accent-cyan); }

.nav-item-admin { color: rgba(129, 140, 248, 0.7); }
.nav-item-admin:hover { background: rgba(129, 140, 248, 0.08); color: var(--color-violet); }
.nav-active-admin {
  background: rgba(129, 140, 248, 0.1) !important;
  color: var(--color-violet) !important;
}

.nav-icon {
  width: 16px; height: 16px;
  flex-shrink: 0;
  opacity: 0.7;
  transition: opacity 0.12s;
}
.nav-item:hover .nav-icon,
.nav-active .nav-icon { opacity: 1; }

/* ── User ── */
.sidebar-user {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.85rem 0.75rem;
  border-top: 1px solid var(--border);
  flex-shrink: 0;
}

.user-info-row {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  flex: 1;
  min-width: 0;
  cursor: pointer;
  border-radius: var(--border-radius);
  padding: 0.3rem 0.4rem;
  transition: background 0.12s;
}
.user-info-row:hover { background: var(--hover-overlay); }

.user-avatar {
  width: 30px; height: 30px;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--accent-cyan), var(--accent-cyan-dim));
  color: #000;
  font-size: 0.68rem;
  font-weight: 700;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  font-family: var(--font-mono);
}

.user-text  { display: flex; flex-direction: column; min-width: 0; }
.user-name  { font-size: 0.8rem; font-weight: 500; color: var(--text-primary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; line-height: 1.3; }
.user-role  { font-size: 0.6rem; font-family: var(--font-mono); text-transform: uppercase; letter-spacing: 0.08em; line-height: 1.2; }
.role-admin { color: var(--color-violet); }
.role-gest  { color: var(--accent-cyan); }

.theme-btn {
  width: 30px; height: 30px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: transparent;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  color: var(--text-tertiary);
  transition: all 0.15s;
  flex-shrink: 0;
}
.theme-btn:hover { border-color: var(--accent-cyan); color: var(--accent-cyan); background: var(--accent-glow); }
.theme-btn svg { width: 14px; height: 14px; }

.logout-btn {
  width: 30px; height: 30px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: transparent;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  color: var(--text-tertiary);
  transition: all 0.15s;
  flex-shrink: 0;
}
.logout-btn:hover { border-color: var(--color-danger); color: var(--color-danger); background: var(--color-danger-light); }
.logout-btn svg { width: 14px; height: 14px; }

/* ══════════════════════════════════════════
   MOBILE TOPBAR
══════════════════════════════════════════ */
.mobile-topbar {
  display: none;
  position: fixed;
  top: 0; left: 0; right: 0;
  height: 52px;
  background: var(--surface-1);
  border-bottom: 1px solid var(--border);
  align-items: center;
  justify-content: space-between;
  padding: 0 1rem;
  z-index: 999;
}

.hamburger-btn {
  width: 36px; height: 36px;
  background: transparent;
  border: 1px solid var(--border);
  border-radius: var(--border-radius);
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.15s;
}
.hamburger-btn:hover { background: var(--surface-3); color: var(--text-primary); }
.hamburger-btn svg { width: 18px; height: 18px; }

.mobile-logo {
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.mobile-avatar { font-size: 0.65rem; }

.mobile-right {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* ── Overlay mobile ── */
.sidebar-overlay {
  display: none;
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.65);
  backdrop-filter: blur(2px);
  z-index: 999;
}

/* ══════════════════════════════════════════
   RESPONSIVE
══════════════════════════════════════════ */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    top: 0;
    z-index: 1001;
  }
  .sidebar.sidebar-open { transform: translateX(0); }

  .sidebar-overlay { display: block; }

  .mobile-topbar { display: flex; }
}
</style>