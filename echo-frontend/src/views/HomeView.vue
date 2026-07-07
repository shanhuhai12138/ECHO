<template>
  <div class="home">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="brand">⟡ ECHO</div>

      <div class="sidebar-section">我的对话</div>
      <div class="entity-list">
        <div
          v-for="box in chatBoxes"
          :key="box.id"
          class="entity-item"
          :class="{ active: activeBoxId === box.id }"
          @click="goToChat(box)"
        >
          <div class="entity-avatar" :class="box.type.toLowerCase()">
            {{ box.type === 'SELF' ? '我' : box.name.charAt(0) }}
          </div>
          <div class="entity-meta">
            <span class="entity-name">{{ box.name }}</span>
            <span class="entity-relation">{{ box.type === 'SELF' ? '自我对话' : box.scenario || '角色对话' }}</span>
          </div>
        </div>
      </div>

      <button class="add-entity" @click="showCreateModal = true">＋ 创建新对话</button>

      <div class="sidebar-footer">
        <router-link to="/quiz" class="btn-outline">📝 画像测评</router-link>
        <router-link to="/persona" class="btn-outline">📊 画像详情</router-link>
      </div>
    </aside>

    <!-- 主区域 -->
    <main class="main">
      <header class="topbar">
        <h1>欢迎回来，{{ authStore.nickname || authStore.username }}</h1>
        <p class="subtitle">今天想聊些什么？</p>
      </header>

      <!-- 画像概览卡片 -->
      <div class="persona-preview" @click="router.push('/persona')">
        <h3>你的画像概览</h3>
        <div class="dimensions-grid">
          <div v-for="dim in personaDims.slice(0, 5)" :key="dim.dimensionKey" class="dim-item">
            <span class="dim-label">{{ dim.dimensionLabel }}</span>
            <div class="dim-bar">
              <div class="dim-fill" :style="{ width: dim.score + '%' }"></div>
            </div>
            <span class="dim-score">{{ dim.score }}</span>
          </div>
        </div>
        <div class="more-link">查看全部 →</div>
      </div>

      <!-- 测评引导 -->
      <div class="quiz-cta" v-if="personaDims.length < 5">
        <h3>完善你的画像</h3>
        <p>完成测评可以让 ECHO 更了解你，给出更有针对性的建议</p>
        <router-link to="/quiz" class="btn btn-primary">开始测评</router-link>
      </div>

      <!-- 快捷入口 -->
      <div class="quick-actions">
        <button class="action-card" @click="createSelfChat()">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          </svg>
          <span>跟我自己聊</span>
        </button>
      </div>
    </main>

    <!-- 创建对话框 -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
      <div class="modal">
        <h3>新建对话</h3>
        <div class="field">
          <label>对话名称</label>
          <input v-model="createForm.name" type="text" placeholder="例如：张三、李四（同事）" />
        </div>
        <div class="field">
          <label>类型</label>
          <select v-model="createForm.type" class="form-input">
            <option value="SELF">跟我自己聊</option>
            <option value="ROLE">跟角色聊</option>
          </select>
        </div>
        <div v-if="createForm.type === 'ROLE'" class="field">
          <label>角色描述</label>
          <textarea v-model="createForm.roleDescription" rows="2" placeholder="描述这个角色"></textarea>
        </div>
        <div class="actions">
          <button class="btn-secondary" @click="showCreateModal = false">取消</button>
          <button class="btn-primary" @click="handleCreate" :disabled="creating">创建</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getChatBoxes, createChatBox } from '../api/services'
import api from '../api'

const router = useRouter()
const authStore = useAuthStore()

const chatBoxes = ref([])
const activeBoxId = ref(null)
const showCreateModal = ref(false)
const personaDims = ref([])
const personaLoading = ref(false)
const creating = ref(false)

const createForm = ref({ name: '', type: 'SELF', roleDescription: '', scenario: '' })

onMounted(async () => {
  try {
    const res = await getChatBoxes(authStore.userId)
    chatBoxes.value = res.data
  } catch (e) {
    console.error('Failed to load chat boxes:', e)
  }
  loadPersona()
})

async function loadPersona() {
  personaLoading.value = true
  try {
    const res = await api.get(`/api/persona/${authStore.userId}`)
    personaDims.value = res.data.dimensions || []
  } catch (e) {
    console.error('Failed to load persona:', e)
  } finally {
    personaLoading.value = false
  }
}

function goToChat(box) {
  router.push(`/chat/${box.id}`)
}

function createSelfChat() {
  createForm.value.name = authStore.nickname || authStore.username || '张三'
  createForm.value.type = 'SELF'
  createForm.value.roleDescription = ''
  createForm.value.scenario = ''
  showCreateModal.value = true
}

async function handleCreate() {
  if (!createForm.value.name) return
  creating.value = true
  try {
    await createChatBox(authStore.userId, createForm.value)
    const res = await getChatBoxes(authStore.userId)
    chatBoxes.value = res.data
    showCreateModal.value = false
    // 自动跳转到新创建的聊天框
    if (chatBoxes.value.length > 0) {
      goToChat(chatBoxes.value[0])
    }
  } catch (e) {
    alert('创建失败：' + (e.response?.data?.error || e.message))
  } finally {
    creating.value = false
  }
}
</script>

<style scoped>
.home {
  display: flex;
  height: 100vh;
  background: var(--color-bg);
}

/* 侧边栏 */
.sidebar {
  width: 280px;
  background: var(--color-primary-bg);
  border-right: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  padding: 24px 16px;
  flex-shrink: 0;
}

.brand {
  font-family: var(--font-serif);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 28px;
  padding-left: 8px;
}

.sidebar-section {
  font-size: 11px;
  font-weight: 600;
  color: var(--color-primary);
  letter-spacing: 1px;
  text-transform: uppercase;
  margin: 16px 0 8px 8px;
}

.entity-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.entity-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--color-foreground);
  font-weight: 500;
  border: 1.5px solid transparent;
}

.entity-item:hover {
  background: var(--color-primary-bg);
}

.entity-item.active {
  background: var(--color-bg-card);
  border-color: var(--color-border-strong);
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.08);
}

.entity-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--color-border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  color: var(--color-primary);
  flex-shrink: 0;
}

.entity-avatar.self {
  background: var(--color-primary);
  color: white;
}

.entity-meta {
  display: flex;
  flex-direction: column;
}

.entity-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-foreground);
}

.entity-relation {
  font-size: 11px;
  color: var(--color-primary-light);
  font-weight: 400;
}

.add-entity {
  margin-top: 16px;
  padding: 12px;
  border: 1.5px dashed var(--color-border-strong);
  border-radius: 14px;
  text-align: center;
  color: var(--color-primary);
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: 0.2s ease;
  background: transparent;
}

.add-entity:hover {
  background: var(--color-primary-bg);
  border-color: var(--color-primary);
}

.sidebar-footer {
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px solid var(--color-border);
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 主区域 */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  padding: var(--space-xl);
  overflow-y: auto;
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

/* 测评引导 */
.quiz-cta {
  margin-top: var(--space-lg);
  background: linear-gradient(135deg, var(--color-accent-bg), var(--color-primary-bg));
  border-radius: var(--radius-lg);
  padding: var(--space-xl);
  text-align: center;
}

.quiz-cta h3 {
  font-family: var(--font-serif);
  font-size: 1.25rem;
  margin: 0 0 var(--space-sm);
  color: var(--color-foreground);
}

.quiz-cta p {
  font-size: 0.875rem;
  color: var(--color-text);
  margin: 0 0 var(--space-md);
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

/* 弹窗 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(76, 29, 149, 0.2);
  backdrop-filter: blur(6px);
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
  max-width: 500px;
  width: 100%;
}

.modal-lg {
  max-width: 700px;
}

.modal h3 {
  font-family: var(--font-serif);
  font-size: 1.5rem;
  margin: 0 0 var(--space-lg);
  color: var(--color-foreground);
}

.field {
  margin-bottom: var(--space-md);
}

.field label {
  font-weight: 600;
  font-size: 0.875rem;
  color: var(--color-foreground);
  display: block;
  margin-bottom: var(--space-xs);
}

.field input,
.field textarea,
.field select {
  width: 100%;
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-md);
  border: 1.5px solid var(--color-border);
  font-size: 0.9375rem;
  outline: none;
  transition: border-color var(--transition-fast);
  font-family: inherit;
  background: var(--color-bg-muted);
}

.field input:focus,
.field textarea:focus,
.field select:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.12);
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-sm);
  margin-top: var(--space-lg);
}

.loading {
  text-align: center;
  padding: var(--space-xl);
  color: var(--color-text-light);
}

/* 响应式 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }
  .dimensions-grid {
    grid-template-columns: 1fr;
  }
}
</style>
