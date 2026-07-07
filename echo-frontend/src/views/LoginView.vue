<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">⟡</div>
        <h1>ECHO</h1>
        <p class="subtitle">你的情绪伙伴，陪你一起成长</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label class="form-label" for="username">用户名</label>
          <input id="username" v-model="form.username" class="form-input" type="text" placeholder="请输入用户名" required autocomplete="username" />
        </div>

        <div class="form-group">
          <label class="form-label" for="password">密码</label>
          <input id="password" v-model="form.password" class="form-input" type="password" placeholder="请输入密码" required autocomplete="current-password" />
        </div>

        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          <svg v-if="loading" class="spinner" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" stroke-dasharray="60" stroke-dashoffset="15">
              <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
            </circle>
          </svg>
          登录
        </button>
      </form>

      <p class="login-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </p>

      <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/services'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  loading.value = true
  errorMsg.value = ''

  try {
    // 注册即登录（初期简化版）
    const res = await register(form.username, form.password, form.username)
    authStore.setUser(res.data)
    router.push('/dashboard')
  } catch (err) {
    if (err.response?.status === 409) {
      // 用户已存在，直接进 dashboard
      authStore.setUser({ id: null, username: form.username, nickname: form.username })
      router.push('/dashboard')
    } else {
      errorMsg.value = err.response?.data?.error || '登录失败，请稍后重试'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-lg);
  background: linear-gradient(135deg, var(--color-primary-bg) 0%, var(--color-bg) 50%, var(--color-accent-bg) 100%);
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-lg);
  padding: var(--space-2xl) var(--space-xl);
}

.login-header {
  text-align: center;
  margin-bottom: var(--space-xl);
}

.logo-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto var(--space-md);
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light));
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  font-family: var(--font-serif);
}

.login-header h1 {
  font-family: var(--font-serif);
  font-size: 1.75rem;
  margin-bottom: var(--space-xs);
  color: var(--color-primary);
  letter-spacing: 0.1em;
}

.subtitle {
  font-size: 0.875rem;
  color: var(--color-text-light);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.login-footer {
  text-align: center;
  font-size: 0.875rem;
  color: var(--color-text);
  margin-top: var(--space-md);
}

.login-footer a {
  font-weight: 500;
  color: var(--color-primary);
}

.error-msg {
  color: var(--color-destructive);
  font-size: 0.8125rem;
  text-align: center;
  margin-top: var(--space-sm);
  padding: var(--space-sm);
  background: var(--color-destructive-bg);
  border-radius: var(--radius-sm);
}

.spinner {
  width: 18px;
  height: 18px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
