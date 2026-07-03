<template>
  <div class="chat-page">
    <!-- Top bar -->
    <header class="topbar">
      <div class="topbar-inner container">
        <button class="btn btn-secondary btn-icon" @click="$router.push('/dashboard')" title="返回">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
          </svg>
        </button>
        <div class="chat-title">
          <h3>{{ chatBox?.name || '对话中...' }}</h3>
          <span v-if="chatBox?.scenario" class="chat-scenario">{{ chatBox.scenario }}</span>
        </div>
        <router-link to="/dashboard" class="btn btn-secondary btn-sm">退出</router-link>
      </div>
    </header>

    <!-- Messages area -->
    <main class="messages-container container">
      <div ref="messagesEnd" class="messages-list">
        <!-- Empty state -->
        <div v-if="messages.length === 0" class="messages-empty">
          <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          </svg>
          <p>开始你的第一次对话吧</p>
        </div>

        <!-- Message bubbles -->
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-row"
          :class="msg.messageType.toLowerCase()"
        >
          <div class="message-bubble">
            <p>{{ msg.content }}</p>
            <span class="message-time">{{ formatTime(msg.createdAt) }}</span>
          </div>
        </div>

        <!-- Typing indicator -->
        <div v-if="sending" class="message-row assistant">
          <div class="message-bubble typing">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>
      </div>
    </main>

    <!-- Input area -->
    <footer class="input-area">
      <div class="container">
        <form @submit.prevent="sendMessage" class="input-wrapper">
          <textarea
            v-model="inputText"
            class="chat-input"
            placeholder="输入你想说的话..."
            rows="1"
            @keydown.enter.exact.prevent="sendMessage"
            :disabled="sending"
          />
          <button type="submit" class="btn btn-primary btn-send" :disabled="!inputText.trim() || sending">
            <svg v-if="!sending" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
            </svg>
            <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" stroke-dasharray="30" stroke-dashoffset="8">
                <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="0.8s" repeatCount="indefinite"/>
              </circle>
            </svg>
          </button>
        </form>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const authStore = useAuthStore()

const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const messagesEnd = ref(null)

// 模拟的聊天框信息（后续从 API 获取）
const chatBox = computed(() => ({
  id: route.params.id,
  name: '我的对话',
  scenario: '',
}))

function scrollToBottom() {
  nextTick(() => {
    if (messagesEnd.value) {
      messagesEnd.value.scrollTop = messagesEnd.value.scrollHeight
    }
  })
}

watch(messages, () => scrollToBottom(), { deep: true })

function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

async function sendMessage() {
  if (!inputText.value.trim() || sending.value) return

  const userMsg = {
    id: Date.now().toString(),
    messageType: 'USER',
    content: inputText.value.trim(),
    createdAt: new Date().toISOString(),
  }

  messages.value.push(userMsg)
  const userText = inputText.value
  inputText.value = ''
  sending.value = true

  // TODO: 调用 AI 接口
  // 这里先用模拟回复演示 UI 效果
  setTimeout(() => {
    const aiMsg = {
      id: (Date.now() + 1).toString(),
      messageType: 'ASSISTANT',
      content: generateMockReply(userText),
      createdAt: new Date().toISOString(),
    }
    messages.value.push(aiMsg)
    sending.value = false
  }, 1000 + Math.random() * 1000)
}

// 模拟 AI 回复（后续替换为真实 AI 调用）
function generateMockReply(input) {
  const replies = [
    `我听到了你说的"${input}"。作为一个关心你的人，我想告诉你，你的感受是值得被重视的。`,
    `谢谢你愿意和我分享这些。从你的角度来看，我能感受到你内心的力量。`,
    `这是一个很好的话题。让我从另一个角度帮你思考一下……`,
    `我能理解你的心情。有时候我们需要停下来，好好听听自己内心的声音。`,
    `你说得很有道理。不过我也在想，也许我们可以换个角度来看这件事。`,
  ]
  return replies[Math.floor(Math.random() * replies.length)]
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--color-bg);
}

.topbar {
  background: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border);
  flex-shrink: 0;
}

.topbar-inner {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  height: 60px;
}

.btn-icon {
  padding: var(--space-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-title h3 {
  font-size: 1.125rem;
  font-weight: 600;
}

.chat-scenario {
  font-size: 0.75rem;
  color: var(--color-text-light);
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-lg) 0;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.messages-empty {
  text-align: center;
  padding: var(--space-2xl) 0;
  color: var(--color-text-light);
}

.empty-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto var(--space-md);
  color: var(--color-border-strong);
}

.message-row {
  display: flex;
  animation: fadeIn 0.3s ease-out;
}

.message-row.user {
  justify-content: flex-end;
}

.message-row.assistant {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 70%;
  padding: var(--space-md) var(--space-lg);
  border-radius: var(--radius-lg);
  line-height: 1.6;
  font-size: 0.9375rem;
}

.message-row.user .message-bubble {
  background: var(--color-primary);
  color: white;
  border-bottom-right-radius: var(--radius-sm);
}

.message-row.assistant .message-bubble {
  background: var(--color-bg-card);
  color: var(--color-foreground);
  border: 1px solid var(--color-border);
  border-bottom-left-radius: var(--radius-sm);
  box-shadow: var(--shadow-sm);
}

.message-time {
  display: block;
  font-size: 0.6875rem;
  opacity: 0.6;
  margin-top: var(--space-xs);
}

/* Typing indicator */
.typing {
  display: flex;
  gap: 4px;
  padding: var(--space-md) var(--space-lg);
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

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Input area */
.input-area {
  flex-shrink: 0;
  background: var(--color-bg-card);
  border-top: 1px solid var(--color-border);
  padding: var(--space-md) 0;
}

.input-wrapper {
  display: flex;
  gap: var(--space-sm);
  align-items: flex-end;
}

.chat-input {
  flex: 1;
  padding: var(--space-sm) var(--space-md);
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-lg);
  font-family: var(--font-sans);
  font-size: 0.9375rem;
  color: var(--color-foreground);
  background: var(--color-bg);
  resize: none;
  line-height: 1.5;
  min-height: 44px;
  max-height: 120px;
  transition: border-color var(--transition-fast);
}

.chat-input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.12);
}

.chat-input::placeholder {
  color: var(--color-text-light);
}

.btn-send {
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-lg);
  flex-shrink: 0;
}
</style>
