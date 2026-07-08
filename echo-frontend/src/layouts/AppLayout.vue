<template>
  <div class="app-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="brand">
        <span class="logo-icon" aria-hidden="true">⟡</span>
        <span class="brand-name">ECHO</span>
      </div>

      <div class="sidebar-section">我的对话</div>
      <div class="entity-list" role="list">
        <div
          v-for="box in chatBoxes"
          :key="box.id"
          class="entity-item"
          role="listitem"
          :class="{ active: activeBoxId === box.id }"
          @click="goToChat(box)"
          tabindex="0"
          :aria-label="box.name + ' - ' + (box.type === 'SELF' ? '自我对话' : box.scenario || '角色对话')"
          @keydown.enter="goToChat(box)"
          @keydown.space.prevent="goToChat(box)"
        >
          <div class="entity-avatar" :class="box.type.toLowerCase()" aria-hidden="true">
            {{ box.type === 'SELF' ? '我' : box.name.charAt(0) }}
          </div>
          <div class="entity-meta">
            <span class="entity-name">{{ box.name }}</span>
            <span class="entity-relation">{{ box.type === 'SELF' ? '核心画像' : box.scenario || '角色对话' }}</span>
          </div>
          <span v-if="box.type === 'SELF'" class="badge" aria-label="活跃">活跃</span>
        </div>
      </div>

      <button class="add-entity" @click="handleCreateSelf" aria-label="创建新对话">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        创建新对话
      </button>

      <div class="sidebar-footer">
        <router-link to="/quiz" class="btn-outline" aria-label="画像测评">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>
          </svg>
          画像测评
        </router-link>
        <router-link to="/persona" class="btn-outline" aria-label="画像详情">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
          </svg>
          画像详情
        </router-link>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <RouterView />
    </main>
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

onMounted(async () => {
  if (!authStore.userId) {
    router.push('/')
    return
  }
  try {
    const res = await getChatBoxes(authStore.userId)
    chatBoxes.value = res.data
  } catch (e) {
    console.error('Failed to load chat boxes:', e)
  }
})

function goToChat(box) {
  activeBoxId.value = box.id
  router.push(`/chat/${box.id}`)
}

async function handleCreateSelf() {
  const name = authStore.nickname || authStore.username || '张三'
  try {
    const res = await createChatBox(authStore.userId, { name, type: 'SELF' })
    // 刷新列表并跳转
    const listRes = await getChatBoxes(authStore.userId)
    chatBoxes.value = listRes.data
    if (listRes.data.length > 0) {
      goToChat(listRes.data[0])
    }
  } catch (e) {
    alert('创建失败：' + (e.response?.data?.error || e.message))
  }
}
</script>

<style scoped>
.app-layout {
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
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: var(--font-serif);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-primary);
  margin-bottom: 28px;
  padding-left: 8px;
}

.logo-icon {
  font-size: 28px;
  color: var(--color-primary);
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

.badge {
  background: var(--color-primary-bg);
  color: var(--color-primary);
  font-size: 10px;
  padding: 2px 10px;
  border-radius: 30px;
  margin-left: auto;
  font-weight: 600;
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
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
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

.btn-outline {
  display: flex;
  align-items: center;
  gap: 8px;
  background: transparent;
  border: 1.5px solid var(--color-border-strong);
  padding: 8px 16px;
  border-radius: 30px;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-primary);
  cursor: pointer;
  transition: 0.2s ease;
  text-decoration: none;
}

.btn-outline:hover {
  background: var(--color-primary-bg);
}

/* 主内容区 */
.main-content {
  flex: 1;
  overflow: auto;
  background: var(--color-bg-card);
}
</style>
