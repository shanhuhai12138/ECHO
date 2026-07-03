import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/DashboardView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/chat/:id',
    name: 'Chat',
    component: () => import('../views/ChatView.vue'),
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫 — 未登录跳转到登录页
router.beforeEach((to, from, next) => {
  const hasUserId = localStorage.getItem('echo_userId')
  if (to.meta.requiresAuth && !hasUserId) {
    next({ name: 'Login' })
  } else {
    next()
  }
})

export default router
