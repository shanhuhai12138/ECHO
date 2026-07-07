<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <div class="logo-icon">⟡</div>
        <h1>创建 ECHO 账号</h1>
        <p class="subtitle">开始你的情绪成长之旅</p>
      </div>

      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label class="form-label" for="reg-username">用户名</label>
          <input id="reg-username" v-model="form.username" class="form-input" type="text" placeholder="选择一个用户名" required minlength="3" maxlength="50" autocomplete="username" />
        </div>

        <div class="form-group">
          <label class="form-label" for="reg-nickname">昵称（可选）</label>
          <input id="reg-nickname" v-model="form.nickname" class="form-input" type="text" placeholder="大家怎么称呼你" maxlength="100" />
        </div>

        <div class="form-group">
          <label class="form-label" for="reg-password">密码</label>
          <input id="reg-password" v-model="form.password" class="form-input" type="password" placeholder="至少 6 个字符" required minlength="6" autocomplete="new-password" />
        </div>

        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          <svg v-if="loading" class="spinner" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" stroke-dasharray="60" stroke-dashoffset="15">
              <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
            </circle>
          </svg>
          注册
        </button>
      </form>

      <p class="register-footer">
        已有账号？<router-link to="/">去登录</router-link>
      </p>

      <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success-msg">{{ successMsg }}</p>
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

const form = reactive({ username: '', nickname: '', password: '' })
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

async function handleRegister() {
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''

  try {
    const res = await register(form.username, form.password, form.nickname || form.username)
    authStore.setUser(res.data)
    successMsg.value = '注册成功！即将跳转...'
    setTimeout(() => router.push('/dashboard'), 800)
  } catch (err) {
    if (err.response?.status === 409) {
      errorMsg.value = '该用户名已被注册，换一个试试吧'
    } else {
      errorMsg.value = '注册失败，请稍后重试'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-lg);
  background: linear-gradient(135deg, var(--color-accent-bg) 0%, var(--color-bg) 50%, var(--color-primary-bg) 100%);
}

.register-card {
  width: 100%;
  max-width: 420px;
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-lg);
  padding: var(--space-2xl) var(--space-xl);
}

.register-header {
  text-align: center;
  margin-bottom: var(--space-xl);
}

.logo-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto var(--space-md);
  background: linear-gradient(135deg, var(--color-accent), var(--color-accent-light));
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  font-family: var(--font-serif);
}

.register-header h1 {
  font-family: var(--font-serif);
  font-size: 1.5rem;
  margin-bottom: var(--space-xs);
}

.subtitle {
  font-size: 0.875rem;
  color: var(--color-text-light);
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.register-footer {
  text-align: center;
  font-size: 0.875rem;
  color: var(--color-text);
  margin-top: var(--space-md);
}

.register-footer a {
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

.success-msg {
  color: var(--color-accent);
  font-size: 0.8125rem;
  text-align: center;
  margin-top: var(--space-sm);
  padding: var(--space-sm);
  background: var(--color-accent-bg);
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
