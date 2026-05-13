<template>
  <div id="app">
    <NavBar v-if="!isLoginPage" />
    <main :class="isLoginPage ? 'main-login' : 'main-content'">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'
import { useTheme } from './composables/useTheme'

const route = useRoute()
const isLoginPage = computed(() => route.name === 'login')

useTheme()
</script>

<style>
#app {
  min-height: 100vh;
  background: var(--bg-base);
}

/* ── Layout avec sidebar ── */
.main-content {
  margin-left: var(--sidebar-width);
  min-height: 100vh;
  padding: 1.75rem 1.75rem 2.5rem;
  background: var(--bg-base);
}

/* ── Login : plein écran ── */
.main-login {
  min-height: 100vh;
  background: var(--bg-base);
}

/* ── Mobile : sidebar en overlay, topbar fixe ── */
@media (max-width: 768px) {
  .main-content {
    margin-left: 0;
    padding: 1rem 1rem 2rem;
    padding-top: calc(52px + 1rem);
  }
}
</style>