import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Home', component: () => import('../views/HomeView.vue') },
  { path: '/chat', name: 'Chat', component: () => import('../views/ChatView.vue') },
  { path: '/chat/:id', name: 'ChatDetail', component: () => import('../views/ChatView.vue') },
  { path: '/persona', name: 'Persona', component: () => import('../views/PersonaView.vue') },
  { path: '/quiz', name: 'Quiz', component: () => import('../views/QuizView.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
