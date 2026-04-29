<template>
  <div id="app">
    <NavBar v-if="!isLoginPage" />
    <main :class="{ 'with-navbar': !isLoginPage }">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'

const route = useRoute()
const isLoginPage = computed(() => route.name === 'login')
</script>

<style>
#app {
  min-height: 100vh;
  background-color: var(--color-gray-100);
}

main {
  min-height: calc(100vh - 60px);
  padding: 1.5rem 1.25rem;
}

main.with-navbar {
  margin-top: 60px;
}
</style>
