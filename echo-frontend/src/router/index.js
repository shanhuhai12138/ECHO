import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue'),
  },
  {
    path: '/chat/:id',
    name: 'Chat',
    component: () => import('../views/ChatView.vue'),
  },
  {
    path: '/quiz',
    name: 'Quiz',
    component: () => import('../views/QuizView.vue'),
  },
  {
    path: '/persona',
    name: 'Persona',
    component: () => import('../views/PersonaView.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
