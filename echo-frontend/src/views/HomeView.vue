<template>
  <div class="home-page">
    <header class="topbar">
      <h1>欢迎回来，{{ authStore.nickname || authStore.username }}</h1>
      <p class="subtitle">今天想聊些什么？</p>
    </header>

    <!-- 画像概览 -->
    <div class="persona-preview" @click="router.push('/persona')">
      <h3>你的画像概览</h3>
      <div class="dimensions-grid" v-if="personaDims.length > 0">
        <div v-for="dim in personaDims" :key="dim.dimensionKey" class="dim-item">
          <span class="dim-label">{{ dim.dimensionLabel }}</span>
          <div class="dim-bar">
            <div class="dim-fill" :style="{ width: (dim.score || 0) + '%' }"></div>
          </div>
          <span class="dim-score">{{ dim.score || 0 }}</span>
        </div>
      </div>
      <div v-else class="empty-persona">
        <p>还没有画像数据</p>
        <router-link to="/quiz" class="btn btn-primary">开始测评</router-link>
      </div>
      <div class="more-link" v-if="personaDims.length > 0">查看全部 →</div>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-actions">
      <button class="action-card" @click="handleCreateSelf">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
        </svg>
        <span>跟我自己聊</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { createChatBox } from '../api/services'
import api from '../api'

const router = useRouter()
const authStore = useAuthStore()
const personaDims = ref([])

onMounted(async () => {
  try {
    const res = await api.get(`/persona/${authStore.userId}`)
    personaDims.value = res.data.dimensions || []
  } catch (e) {
    console.error('Failed to load persona:', e)
  }
})

async function handleCreateSelf() {
  const name = authStore.nickname || authStore.username || '张三'
  try {
    await createChatBox(authStore.userId, { name, type: 'SELF' })
    router.push('/chat')
  } catch (e) {
    alert('创建失败：' + (e.response?.data?.error || e.message))
  }
}
</script>

<style scoped>
.home-page {
  padding: var(--space-xl);
  overflow-y: auto;
  max-width: 900px;
  margin: 0 auto;
  width: 100%;
}

.topbar h1 {
  font-family: var(--font-serif);
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-text-heading);
  margin: 0 0 var(--space-xs);
}

.subtitle {
  font-size: 1.125rem;
  color: var(--color-text-light);
  margin: 0;
}

/* 画像概览 */
.persona-preview {
  margin-top: var(--space-xl);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  padding: var(--space-lg);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: box-shadow var(--transition-base);
}

.persona-preview:hover {
  box-shadow: var(--shadow-md);
}

.persona-preview h3 {
  font-family: var(--font-serif);
  font-size: 1.125rem;
  margin: 0 0 var(--space-md);
  color: var(--color-foreground);
}

.dimensions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--space-md);
}

.dim-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
}

.dim-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-foreground);
}

.dim-bar {
  height: 8px;
  background: var(--color-bg-muted);
  border-radius: 4px;
  overflow: hidden;
}

.dim-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
  border-radius: 4px;
  transition: width 0.5s ease;
}

.dim-score {
  font-size: 0.75rem;
  color: var(--color-text-light);
  text-align: right;
}

.more-link {
  margin-top: var(--space-md);
  font-size: 0.875rem;
  color: var(--color-primary);
  text-align: center;
  font-weight: 500;
}

.empty-persona {
  text-align: center;
  padding: var(--space-lg) 0;
}

.empty-persona p {
  margin-bottom: var(--space-md);
  color: var(--color-text);
}

/* 快捷操作 */
.quick-actions {
  margin-top: var(--space-lg);
  display: flex;
  gap: var(--space-md);
  flex-wrap: wrap;
}

.action-card {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-md) var(--space-lg);
  background: var(--color-bg-card);
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-base);
  font-family: var(--font-sans);
  font-size: 0.9375rem;
  font-weight: 500;
  color: var(--color-foreground);
}

.action-card:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.action-card svg {
  color: var(--color-primary);
}

/* 响应式 */
@media (max-width: 768px) {
  .dimensions-grid {
    grid-template-columns: 1fr;
  }
}
</style>
