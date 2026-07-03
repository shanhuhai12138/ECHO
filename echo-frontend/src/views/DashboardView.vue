<template>
  <div class="dashboard">
    <!-- Top bar -->
    <header class="topbar">
      <div class="topbar-inner container">
        <div class="topbar-brand">
          <span class="logo-small">E</span>
          <span class="brand-name">ECHO</span>
        </div>
        <div class="topbar-user">
          <span>{{ authStore.nickname || authStore.username }}</span>
          <button class="btn btn-secondary btn-sm" @click="handleLogout">退出</button>
        </div>
      </div>
    </header>

    <!-- Main content -->
    <main class="main container">
      <div class="dashboard-header">
        <h2>我的对话</h2>
        <button class="btn btn-primary" @click="showCreateModal = true">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          新建对话
        </button>
      </div>

      <!-- Loading state -->
      <div v-if="loading" class="chat-grid">
        <div v-for="i in 3" :key="i" class="card skeleton-card">
          <div class="skeleton" style="height: 20px; width: 60%; margin-bottom: 8px;"></div>
          <div class="skeleton" style="height: 14px; width: 40%; margin-bottom: 16px;"></div>
          <div class="skeleton" style="height: 44px; width: 100%;"></div>
        </div>
      </div>

      <!-- Empty state -->
      <div v-else-if="boxes.length === 0" class="empty-state">
        <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
        </svg>
        <h3>还没有对话</h3>
        <p>点击"新建对话"开始你的 ECHO 之旅</p>
      </div>

      <!-- Chat boxes list -->
      <div v-else class="chat-grid">
        <router-link
          v-for="box in boxes"
          :key="box.id"
          :to="`/chat/${box.id}`"
          class="card chat-card"
        >
          <div class="chat-card-header">
            <span class="chat-type-badge" :class="box.type.toLowerCase()">
              {{ box.type === 'SELF' ? '我' : '角色' }}
            </span>
            <span class="chat-card-name">{{ box.name }}</span>
          </div>
          <p v-if="box.scenario" class="chat-card-scenario">{{ box.scenario }}</p>
          <p v-else class="chat-card-placeholder">点击开始对话...</p>
          <div class="chat-card-footer">
            <span class="chat-card-date">{{ formatDate(box.createdAt) }}</span>
          </div>
        </router-link>
      </div>
    </main>

    <!-- Create modal -->
    <Teleport to="body">
      <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
        <div class="modal">
          <h3>新建对话</h3>
          <form @submit.prevent="handleCreate">
            <div class="form-group">
              <label class="form-label" for="box-name">对话名称</label>
              <input
                id="box-name"
                v-model="createForm.name"
                class="form-input"
                type="text"
                placeholder="例如：张三、李四（同事）"
                required
              />
            </div>

            <div class="form-group">
              <label class="form-label" for="box-type">类型</label>
              <select id="box-type" v-model="createForm.type" class="form-input">
                <option value="SELF">跟我自己聊</option>
                <option value="ROLE">跟角色聊</option>
              </select>
            </div>

            <div v-if="createForm.type === 'ROLE'" class="form-group">
              <label class="form-label" for="box-desc">角色描述</label>
              <textarea
                id="box-desc"
                v-model="createForm.roleDescription"
                class="form-input"
                rows="2"
                placeholder="描述这个角色的特点，例如：我的同事，性格内向，做事认真"
              ></textarea>
            </div>

            <div class="form-group">
              <label class="form-label" for="box-scenario">场景标签</label>
              <input
                id="box-scenario"
                v-model="createForm.scenario"
                class="form-input"
                type="text"
                placeholder="例如：绩效面谈、日常闲聊"
              />
            </div>

            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" @click="showCreateModal = false">取消</button>
              <button type="submit" class="btn btn-primary" :disabled="creating">创建</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getChatBoxes, createChatBox } from '../api/services'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const boxes = ref([])
const loading = ref(true)
const showCreateModal = ref(false)
const creating = ref(false)

const createForm = reactive({
  name: '',
  type: 'SELF',
  roleDescription: '',
  scenario: '',
})

onMounted(async () => {
  if (!authStore.userId) {
    router.push('/')
    return
  }
  try {
    const res = await getChatBoxes(authStore.userId)
    boxes.value = res.data
  } catch (e) {
    // 忽略错误，可能是还没有数据
  } finally {
    loading.value = false
  }
})

async function handleCreate() {
  creating.value = true
  try {
    await createChatBox(authStore.userId, createForm)
    // 刷新列表
    const res = await getChatBoxes(authStore.userId)
    boxes.value = res.data
    showCreateModal.value = false
    // 重置表单
    createForm.name = ''
    createForm.type = 'SELF'
    createForm.roleDescription = ''
    createForm.scenario = ''
  } catch (e) {
    alert('创建失败：' + (e.response?.data?.error || e.message))
  } finally {
    creating.value = false
  }
}

function handleLogout() {
  authStore.clearUser()
  router.push('/')
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  return `${d.getMonth() + 1}月${d.getDate()}日`
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: var(--color-bg);
}

.topbar {
  background: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border);
  position: sticky;
  top: 0;
  z-index: 50;
}

.topbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.topbar-brand {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.logo-small {
  width: 32px;
  height: 32px;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-serif);
  font-weight: 700;
  font-size: 1.125rem;
}

.brand-name {
  font-family: var(--font-serif);
  font-weight: 700;
  font-size: 1.125rem;
  color: var(--color-primary);
  letter-spacing: 0.08em;
}

.topbar-user {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  font-size: 0.875rem;
  color: var(--color-text);
}

.btn-sm {
  padding: var(--space-xs) var(--space-md);
  font-size: 0.8125rem;
}

.main {
  padding-top: var(--space-xl);
  padding-bottom: var(--space-2xl);
}

.dashboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-xl);
}

.dashboard-header h2 {
  font-size: 1.5rem;
}

.chat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--space-md);
}

.chat-card {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
  text-decoration: none;
  color: inherit;
  cursor: pointer;
  transition: transform var(--transition-fast), box-shadow var(--transition-fast);
}

.chat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.chat-card-header {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.chat-type-badge {
  font-size: 0.6875rem;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 999px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.chat-type-badge.self {
  background: var(--color-primary-bg);
  color: var(--color-primary);
}

.chat-type-badge.role {
  background: var(--color-accent-bg);
  color: var(--color-accent);
}

.chat-card-name {
  font-family: var(--font-serif);
  font-weight: 600;
  font-size: 1rem;
  color: var(--color-text-heading);
}

.chat-card-scenario {
  font-size: 0.8125rem;
  color: var(--color-text);
}

.chat-card-placeholder {
  font-size: 0.8125rem;
  color: var(--color-text-light);
  font-style: italic;
}

.chat-card-footer {
  margin-top: auto;
  font-size: 0.75rem;
  color: var(--color-text-light);
}

.empty-state {
  text-align: center;
  padding: var(--space-2xl) 0;
}

.empty-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto var(--space-md);
  color: var(--color-border-strong);
}

.empty-state h3 {
  font-size: 1.25rem;
  margin-bottom: var(--space-sm);
}

.empty-state p {
  color: var(--color-text-light);
  font-size: 0.875rem;
}

.skeleton-card {
  padding: var(--space-lg);
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(30, 27, 75, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: var(--space-lg);
}

.modal {
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-lg);
  padding: var(--space-xl);
  width: 100%;
  max-width: 440px;
}

.modal h3 {
  margin-bottom: var(--space-lg);
}

.modal .form-group {
  margin-bottom: var(--space-md);
}

.modal-actions {
  display: flex;
  gap: var(--space-sm);
  justify-content: flex-end;
  margin-top: var(--space-lg);
}
</style>
