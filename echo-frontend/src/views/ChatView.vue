<template>
  <div class="chat-page">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="brand">⟡ ECHO</div>
      <router-link to="/" class="back-btn">← 返回首页</router-link>

      <div class="sidebar-section">我的对话</div>
      <div class="entity-list">
        <div
          v-for="box in chatBoxes"
          :key="box.id"
          class="entity-item"
          :class="{ active: activeBoxId === box.id }"
          @click="selectBox(box)"
        >
          <div class="entity-avatar" :class="box.type.toLowerCase()">
            {{ box.type === 'SELF' ? '我' : box.name.charAt(0) }}
          </div>
          <div class="entity-meta">
            <span class="entity-name">{{ box.name }}</span>
            <span class="entity-relation">{{ box.type === 'SELF' ? '自我' : box.scenario || '角色' }}</span>
          </div>
        </div>
      </div>
    </aside>

    <!-- 聊天区域 -->
    <main class="chat-main">
      <header class="chat-header">
        <h2>{{ activeBox?.name || '选择对话' }}</h2>
        <p v-if="activeBox" class="chat-sub">
          {{ activeBox.type === 'SELF' ? '与你的核心画像交流' : (activeBox.scenario || '') }}
        </p>
      </header>

      <!-- 消息列表 -->
      <div class="messages-container" ref="messagesEnd">
        <div v-if="messages.length === 0" class="messages-empty">
          <div class="empty-icon">⟡</div>
          <h3>开始你的第一次对话吧</h3>
          <p>在下方输入框中输入你想说的话</p>
        </div>

        <div v-else class="messages-list">
          <div
            v-for="(msg, idx) in messages"
            :key="idx"
            class="message"
            :class="msg.messageType.toLowerCase()"
          >
            <div class="avatar" :class="msg.messageType.toLowerCase()">
              {{ msg.messageType === 'USER' ? '张' : 'E' }}
            </div>
            <div class="message-content">
              <div class="bubble">{{ msg.content }}</div>
              <div class="meta">
                {{ msg.messageType === 'USER' ? authStore.nickname || authStore.username : 'ECHO' }} · {{ formatTime(msg.createdAt) }}
              </div>
            </div>
          </div>

          <!-- 加载中指示器 -->
          <div v-if="sending" class="message assistant">
            <div class="avatar assistant">E</div>
            <div class="message-content">
              <div class="bubble typing">
                <span class="dot"></span>
                <span class="dot"></span>
                <span class="dot"></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <footer class="chat-input">
        <textarea
          v-model="inputText"
          placeholder="输入你想说的话..."
          @keydown.enter.exact.prevent="sendMessage"
          :disabled="sending"
          rows="1"
        ></textarea>
        <button @click="sendMessage" :disabled="!inputText.trim() || sending" class="send-btn">
          <svg v-if="!sending" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
          </svg>
          <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" stroke-dasharray="30" stroke-dashoffset="8">
              <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="0.8s" repeatCount="indefinite"/>
            </circle>
          </svg>
        </button>
      </footer>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getChatBoxes, createChatBox } from '../api/services'
import api from '../api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const chatBoxes = ref([])
const activeBoxId = ref(route.params.id)
const activeBox = ref(null)
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const messagesEnd = ref(null)

onMounted(async () => {
  try {
    const res = await getChatBoxes(authStore.userId)
    chatBoxes.value = res.data

    // 加载当前聊天框的消息
    if (activeBoxId.value) {
      await loadMessages(activeBoxId.value)
      await loadBoxInfo(activeBoxId.value)
    }
  } catch (e) {
    console.error('Failed to load chat boxes:', e)
  }
})

// 监听路由变化，切换聊天框
watch(() => route.params.id, async (newId) => {
  if (newId) {
    activeBoxId.value = newId
    messages.value = []
    activeBox.value = null
    await loadMessages(newId)
    // 加载聊天框信息
    await loadBoxInfo(newId)
  }
})

async function loadBoxInfo(chatBoxId) {
  try {
    // 从 chatBoxes 列表中查找
    const box = chatBoxes.value.find(b => b.id === chatBoxId)
    if (box) {
      activeBox.value = box
    }
  } catch (e) {
    console.error('Failed to load box info:', e)
  }
}

async function loadMessages(chatBoxId) {
  try {
    const res = await api.get(`/chat/${chatBoxId}/messages`)
    messages.value = res.data
    scrollToBottom()
  } catch (e) {
    console.error('Failed to load messages:', e)
  }
}

function selectBox(box) {
  activeBoxId.value = box.id
  activeBox.value = box
  router.push(`/chat/${box.id}`)
}

async function sendMessage() {
  if (!inputText.value.trim() || sending.value || !activeBoxId.value) return

  const userMsg = {
    messageType: 'USER',
    content: inputText.value.trim(),
    createdAt: new Date().toISOString(),
  }

  messages.value.push(userMsg)
  const userText = inputText.value
  inputText.value = ''
  sending.value = true
  scrollToBottom()

  try {
    // 调用后端 AI 接口
    const res = await api.post(`/chat/${activeBoxId.value}`, { content: userText })

    const aiMsg = {
      id: res.data.messageId,
      messageType: 'ASSISTANT',
      content: res.data.content,
      createdAt: new Date().toISOString(),
    }
    messages.value.push(aiMsg)
  } catch (e) {
    console.error('AI 调用失败:', e)
    // 降级：显示友好提示
    const errorMsg = {
      messageType: 'ASSISTANT',
      content: '抱歉，AI 服务暂时不可用，请稍后再试。',
      createdAt: new Date().toISOString(),
    }
    messages.value.push(errorMsg)
  } finally {
    sending.value = false
    scrollToBottom()
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesEnd.value) {
      messagesEnd.value.scrollTop = messagesEnd.value.scrollHeight
    }
  })
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}
</script>

<style scoped>
.chat-page {
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
  margin-bottom: 20px;
  padding-left: 8px;
}

.back-btn {
  display: inline-block;
  padding: 8px 16px;
  background: var(--color-bg-card);
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-md);
  color: var(--color-primary);
  font-size: 0.875rem;
  font-weight: 500;
  text-decoration: none;
  transition: all var(--transition-base);
  margin-bottom: 20px;
}

.back-btn:hover {
  background: var(--color-primary-bg);
  border-color: var(--color-primary);
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

/* 聊天主区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: var(--color-bg-card);
}

.chat-header {
  padding: 20px 28px;
  border-bottom: 1px solid var(--color-border);
  flex-shrink: 0;
}

.chat-header h2 {
  font-family: var(--font-serif);
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-foreground);
  margin: 0 0 4px;
}

.chat-sub {
  font-size: 0.875rem;
  color: var(--color-text-light);
  margin: 0;
}

/* 消息区域 */
.messages-container {
  flex: 1;
  padding: 28px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 18px;
  background: linear-gradient(180deg, var(--color-primary-bg) 0%, var(--color-bg-card) 100%);
}

.messages-empty {
  text-align: center;
  padding: var(--space-2xl) 0;
  color: var(--color-text-light);
}

.messages-empty .empty-icon {
  font-size: 48px;
  margin-bottom: var(--space-md);
  color: var(--color-border-strong);
}

.messages-empty h3 {
  font-family: var(--font-serif);
  font-size: 1.25rem;
  margin-bottom: var(--space-sm);
  color: var(--color-text);
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 80%;
  animation: fadeUp 0.3s ease;
}

.message.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message.assistant {
  align-self: flex-start;
}

.message .avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--color-border);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 13px;
  color: var(--color-primary);
}

.message.user .avatar {
  background: var(--color-primary);
  color: white;
}

.message .message-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message .bubble {
  background: var(--color-bg-card);
  padding: 14px 20px;
  border-radius: 18px;
  border-bottom-left-radius: 6px;
  box-shadow: 0 1px 4px rgba(139, 92, 246, 0.04);
  border: 1px solid var(--color-border);
  font-size: 15px;
  line-height: 1.6;
  color: var(--color-foreground);
}

.message.user .bubble {
  background: var(--color-primary);
  color: white;
  border: none;
  border-bottom-right-radius: 6px;
  border-bottom-left-radius: 18px;
}

.message .meta {
  font-size: 12px;
  color: var(--color-text-light);
  padding-left: 4px;
}

.message.user .meta {
  text-align: right;
  padding-right: 4px;
}

/* 打字指示器 */
.typing {
  display: flex;
  gap: 4px;
  padding: 14px 20px;
  min-width: 60px;
}

.dot {
  width: 8px;
  height: 8px;
  background: var(--color-text-light);
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out;
}

.dot:nth-child(1) { animation-delay: -0.32s; }
.dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.4; }
  40% { transform: scale(1); opacity: 1; }
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 输入框 */
.chat-input {
  padding: 16px 28px 24px;
  border-top: 1px solid var(--color-border);
  display: flex;
  gap: 12px;
  background: var(--color-bg-card);
  flex-shrink: 0;
}

.chat-input textarea {
  flex: 1;
  padding: 12px 18px;
  border-radius: 40px;
  border: 1.5px solid var(--color-border);
  font-size: 15px;
  outline: none;
  transition: 0.2s ease;
  background: var(--color-bg-muted);
  font-family: inherit;
  resize: none;
  min-height: 44px;
  max-height: 120px;
}

.chat-input textarea:focus {
  border-color: var(--color-primary);
  background: white;
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.08);
}

.chat-input textarea::placeholder {
  color: var(--color-text-light);
}

.send-btn {
  background: var(--color-primary);
  border: none;
  padding: 0 28px;
  border-radius: 40px;
  color: white;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
  min-width: 44px;
}

.send-btn:hover:not(:disabled) {
  background: var(--color-primary-dark);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 滚动条 */
.messages-container::-webkit-scrollbar {
  width: 4px;
}

.messages-container::-webkit-scrollbar-thumb {
  background: var(--color-border-strong);
  border-radius: 20px;
}

/* 响应式 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }
}
</style>
