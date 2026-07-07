<template>
  <div class="dashboard" role="main">
    <!-- 左侧边栏 -->
    <aside class="sidebar" aria-label="导航侧边栏">
      <div class="brand">
        <span class="logo-icon" aria-hidden="true">⟡</span>
        <span class="brand-name">ECHO</span>
      </div>

      <div class="sidebar-section">我的实体</div>
      <div class="entity-list" role="list">
        <div
          v-for="box in chatBoxes"
          :key="box.id"
          class="entity-item"
          role="listitem"
          :class="{ active: activeBoxId === box.id }"
          @click="selectBox(box)"
          tabindex="0"
          :aria-label="box.name + ' - ' + (box.type === 'SELF' ? '自我对话' : box.scenario || '角色对话')"
          @keydown.enter="selectBox(box)"
          @keydown.space.prevent="selectBox(box)"
        >
          <div class="entity-avatar" :class="box.type.toLowerCase()" aria-hidden="true">
            {{ box.type === 'SELF' ? '我' : box.name.charAt(0) }}
          </div>
          <div class="entity-meta">
            <span class="entity-name">{{ box.name }}</span>
            <span class="entity-relation">{{ box.type === 'SELF' ? '核心画像 · 主视角' : box.scenario || '角色对话' }}</span>
          </div>
          <span v-if="box.type === 'SELF'" class="badge" aria-label="活跃">活跃</span>
        </div>
      </div>

      <button class="add-entity" @click="showCreateModal = true" aria-label="创建新实体">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        创建新实体
      </button>

      <div class="sidebar-footer">
        <button class="btn-outline" @click="showKnowledgeModal = true" aria-label="打开知识库管理">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
          </svg>
          知识库管理
        </button>
      </div>
    </aside>

    <!-- 右侧主区域 -->
    <main class="main">
      <!-- 头部 -->
      <header class="chat-header">
        <div class="chat-header-left">
          <h2 id="chatTitle">{{ activeBox?.name || '选择对话' }}</h2>
          <span class="sub" id="chatSub">{{ activeBox?.type === 'SELF' ? '与你的核心画像交流' : activeBox?.scenario || '' }}</span>
        </div>
        <div class="header-actions">
          <button class="btn-outline" @click="showKnowledgeModal = true" aria-label="查看画像详情">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><line x1="3" y1="9" x2="21" y2="9"/><line x1="9" y1="21" x2="9" y2="9"/>
            </svg>
            画像详情
          </button>
          <button class="btn-secondary" @click="handleLogout" aria-label="退出登录">退出</button>
        </div>
      </header>

      <!-- 消息区域 -->
      <div class="chat-messages" ref="messageContainer" role="log" aria-live="polite" aria-label="对话消息">
        <!-- 空状态 -->
        <div v-if="messages.length === 0" class="messages-empty">
          <div class="empty-icon" aria-hidden="true">⟡</div>
          <h3>开始你的第一次对话吧</h3>
          <p>选择左侧实体，或创建新实体开始对话</p>
        </div>

        <!-- 消息列表 -->
        <div v-else class="messages-list">
          <div
            v-for="(msg, idx) in messages"
            :key="idx"
            class="message"
            :class="msg.messageType.toLowerCase()"
            role="article"
            :aria-label="msg.messageType === 'USER' ? '你的消息' : 'AI 回复'"
          >
            <div class="avatar" aria-hidden="true">{{ msg.messageType === 'USER' ? '张' : 'E' }}</div>
            <div class="content">
              <div class="bubble">{{ msg.content }}</div>
              <div class="meta">{{ msg.messageType === 'USER' ? '张三' : 'ECHO'} · {{ formatTime(msg.createdAt) }}</div>
            </div>
          </div>

          <!-- 系统提示卡片 -->
          <div v-if="systemCard" class="system-card" id="systemCard" role="alert" aria-live="assertive">
            <div class="label">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <path d="M12 2a10 10 0 1 0 10 10H12V2z"/><path d="M12 2a10 10 0 1 0 10 10"/>
              </svg>
              ECHO 观察
            </div>
            <div class="desc">{{ systemCard.text }}</div>
            <div class="actions">
              <button class="confirm" @click="systemCard = null" aria-label="确认更新画像">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                确认
              </button>
              <button class="ignore" @click="systemCard = null" aria-label="忽略此提示">忽略</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <footer class="chat-input" role="form" aria-label="发送消息">
        <input
          v-model="inputText"
          type="text"
          placeholder="输入你想说的话..."
          @keydown.enter="sendMessage"
          :disabled="sending"
          aria-label="消息输入框"
          :aria-busy="sending"
        />
        <button @click="sendMessage" :disabled="!inputText.trim() || sending" aria-label="发送消息">
          <svg v-if="!sending" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
          </svg>
          <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
            <circle cx="12" cy="12" r="10" stroke-dasharray="30" stroke-dashoffset="8">
              <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="0.8s" repeatCount="indefinite"/>
            </circle>
          </svg>
        </button>
      </footer>
    </main>

    <!-- 创建聊天框弹窗 -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false" role="dialog" aria-modal="true" aria-labelledby="create-title">
      <div class="modal">
        <h3 id="create-title">新建对话</h3>
        <p class="modal-sub">创建一个新的聊天框，开始对话</p>

        <div class="field">
          <label for="box-name">对话名称</label>
          <input id="box-name" v-model="createForm.name" type="text" placeholder="例如：张三、李四（同事）" />
        </div>

        <div class="field">
          <label for="box-type">类型</label>
          <select id="box-type" v-model="createForm.type" class="form-input">
            <option value="SELF">跟我自己聊</option>
            <option value="ROLE">跟角色聊</option>
          </select>
        </div>

        <div v-if="createForm.type === 'ROLE'" class="field">
          <label for="box-desc">角色描述</label>
          <textarea id="box-desc" v-model="createForm.roleDescription" rows="2" placeholder="描述这个角色的特点"></textarea>
        </div>

        <div class="field">
          <label for="box-scenario">场景标签</label>
          <input id="box-scenario" v-model="createForm.scenario" type="text" placeholder="例如：绩效面谈、日常闲聊" />
        </div>

        <div class="actions">
          <button class="btn-secondary" @click="showCreateModal = false">取消</button>
          <button class="btn-primary" @click="handleCreate" :disabled="creating">创建</button>
        </div>
      </div>
    </div>

    <!-- 知识库弹窗 -->
    <div v-if="showKnowledgeModal" class="modal-overlay" @click.self="showKnowledgeModal = false" role="dialog" aria-modal="true" aria-labelledby="knowledge-title">
      <div class="modal">
        <h3 id="knowledge-title">📚 官方性格 · 种子数据库</h3>
        <p class="modal-sub">作为「首席人格官」，你录入的观念将成为 ECHO 的底层价值观锚点。</p>

        <div class="field">
          <label for="seed-category">观念分类</label>
          <input id="seed-category" v-model="seedForm.category" type="text" placeholder="如：职场、情感、自我成长" value="处世哲学" />
        </div>

        <div class="field">
          <label for="seed-content">核心观念 / 准则</label>
          <textarea id="seed-content" v-model="seedForm.content" rows="3" placeholder="例如：『真正的成熟不是消除冲突，而是学会在冲突中保持清醒。』">『真正的成熟不是消除冲突，而是学会在冲突中保持清醒。』</textarea>
        </div>

        <div class="actions">
          <button class="btn-secondary" @click="showKnowledgeModal = false">关闭</button>
          <button class="btn-primary" @click="addSeed">录入种子</button>
        </div>

        <div style="margin-top:16px;font-size:13px;color:#8B5CF6;">已录入的种子观念：</div>
        <div class="seed-list" role="list" aria-label="种子观念列表">
          <div v-for="(seed, idx) in seeds" :key="idx" class="seed-item" role="listitem">
            <span>{{ seed.content }}</span>
            <span class="tag">{{ seed.category }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getChatBoxes, createChatBox } from '../api/services'

const router = useRouter()
const authStore = useAuthStore()

const chatBoxes = ref([])
const activeBoxId = ref(null)
const activeBox = ref(null)
const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const showCreateModal = ref(false)
const showKnowledgeModal = ref(false)
const creating = ref(false)
const systemCard = ref(null)
const messageContainer = ref(null)

const createForm = reactive({ name: '', type: 'SELF', roleDescription: '', scenario: '' })

const seedForm = reactive({ category: '处世哲学', content: '' })
const seeds = ref([
  { category: '职场', content: '『真诚是最高级的策略，但搭配智慧使用。』' },
  { category: '自我', content: '『情绪是信号，不是敌人。先接纳，再行动。』' },
])

onMounted(async () => {
  if (!authStore.userId) {
    router.push('/')
    return
  }

  try {
    const res = await getChatBoxes(authStore.userId)
    chatBoxes.value = res.data

    // 默认选中第一个 SELF 类型
    const selfBox = chatBoxes.value.find(b => b.type === 'SELF')
    if (selfBox) {
      selectBox(selfBox)
    }
  } catch (e) {
    console.error('Failed to load chat boxes:', e)
  }
})

function selectBox(box) {
  activeBoxId.value = box.id
  activeBox.value = box
  messages.value = []
  systemCard.value = null

  // 更新标题
  const title = document.getElementById('chatTitle')
  const sub = document.getElementById('chatSub')
  if (title) title.textContent = box.name
  if (sub) sub.textContent = box.type === 'SELF' ? '与你的核心画像交流' : box.scenario || ''
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

  // TODO: 调用真实 AI 接口
  // 先用模拟回复演示 UI 效果
  setTimeout(() => {
    const aiMsg = {
      messageType: 'ASSISTANT',
      content: generateMockReply(userText),
      createdAt: new Date().toISOString(),
    }
    messages.value.push(aiMsg)
    sending.value = false

    // 模拟触发画像更新提示
    if (['累', '焦虑', '压力'].some(w => userText.includes(w))) {
      systemCard.value = { text: '检测到你近期表达出「焦虑」倾向，建议在「情绪应对」维度增加「接纳-分析-行动」权重。是否更新画像？' }
    }
  }, 1000 + Math.random() * 500)
}

function generateMockReply(input) {
  const replies = [
    `我听到了你说的"${input}"。作为一个关心你的人，我想告诉你，你的感受是值得被重视的。`,
    `谢谢你愿意和我分享这些。从你的角度来看，我能感受到你内心的力量。`,
    `这是一个很好的话题。让我从另一个角度帮你思考一下……`,
    `我能理解你的心情。有时候我们需要停下来，好好听听自己内心的声音。`,
  ]
  return replies[Math.floor(Math.random() * replies.length)]
}

function handleCreate() {
  creating.value = true
  // TODO: 调用后端 API 创建聊天框
  setTimeout(() => {
    const newBox = {
      id: Date.now().toString(),
      name: createForm.name,
      type: createForm.type,
      roleDescription: createForm.roleDescription,
      scenario: createForm.scenario,
      createdAt: new Date().toISOString(),
    }
    chatBoxes.value.unshift(newBox)
    selectBox(newBox)
    showCreateModal.value = false
    creating.value = false
    // 重置表单
    createForm.name = ''
    createForm.type = 'SELF'
    createForm.roleDescription = ''
    createForm.scenario = ''
  }, 500)
}

function addSeed() {
  if (!seedForm.content.trim()) return
  seeds.value.push({ category: seedForm.category || '未分类', content: seedForm.content })
  seedForm.content = ''
}

function handleLogout() {
  authStore.clearUser()
  router.push('/')
}

function formatTime(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}
</script>

<style scoped>
.dashboard {
  display: flex;
  height: 100vh;
  background: var(--color-bg);
}

/* 左侧边栏 */
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
}

/* 右侧主区域 */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: var(--color-bg-card);
}

.chat-header {
  padding: 20px 28px;
  border-bottom: 1px solid var(--color-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.chat-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-header-left h2 {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 600;
  color: var(--color-foreground);
  margin: 0;
}

.sub {
  font-size: 13px;
  color: var(--color-primary-light);
  font-weight: 400;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.btn-outline {
  background: transparent;
  border: 1.5px solid var(--color-border-strong);
  padding: 6px 18px;
  border-radius: 30px;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-primary);
  cursor: pointer;
  transition: 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-outline:hover {
  background: var(--color-primary-bg);
}

.btn-secondary {
  background: transparent;
  border: 1.5px solid var(--color-border);
  padding: 6px 18px;
  border-radius: 30px;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text);
  cursor: pointer;
  transition: 0.2s ease;
}

.btn-secondary:hover {
  background: var(--color-bg-muted);
}

/* 消息区域 */
.chat-messages {
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

.empty-icon {
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

.messages-empty p {
  font-size: 0.875rem;
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

.message .content {
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

/* 系统提示卡片 */
.system-card {
  background: linear-gradient(135deg, var(--color-primary-bg), var(--color-accent-bg));
  border-left: 4px solid var(--color-primary);
  border-radius: 14px;
  padding: 16px 20px;
  margin: 8px 0;
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  align-self: center;
  width: 90%;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.08);
}

.system-card .label {
  font-weight: 600;
  color: var(--color-foreground);
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.system-card .desc {
  flex: 1;
  font-size: 14px;
  color: var(--color-foreground);
}

.system-card .actions {
  display: flex;
  gap: 10px;
}

.system-card .actions button {
  padding: 4px 18px;
  border-radius: 30px;
  border: none;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.system-card .actions .confirm {
  background: var(--color-primary);
  color: white;
}

.system-card .actions .confirm:hover {
  background: var(--color-primary-dark);
}

.system-card .actions .ignore {
  background: transparent;
  color: var(--color-primary-light);
  border: 1.5px solid var(--color-border-strong);
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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

.chat-input input {
  flex: 1;
  padding: 12px 18px;
  border-radius: 40px;
  border: 1.5px solid var(--color-border);
  font-size: 15px;
  outline: none;
  transition: 0.2s ease;
  background: var(--color-bg-muted);
  font-family: inherit;
}

.chat-input input:focus {
  border-color: var(--color-primary);
  background: white;
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.08);
}

.chat-input button {
  background: var(--color-primary);
  border: none;
  padding: 0 28px;
  border-radius: 40px;
  color: white;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: 0.2s ease;
  white-space: nowrap;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-input button:hover {
  background: var(--color-primary-dark);
}

.chat-input button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
  padding: 32px 36px;
  max-width: 560px;
  width: 100%;
  animation: fadeUp 0.3s ease;
}

.modal h3 {
  font-family: var(--font-serif);
  font-size: 22px;
  margin-bottom: 6px;
  color: var(--color-foreground);
}

.modal-sub {
  color: var(--color-primary-light);
  font-size: 14px;
  margin-bottom: 24px;
}

.field {
  margin-bottom: 18px;
}

.field label {
  font-weight: 600;
  font-size: 14px;
  color: var(--color-foreground);
  display: block;
  margin-bottom: 4px;
}

.field input,
.field textarea,
.field select {
  width: 100%;
  padding: 10px 14px;
  border-radius: 12px;
  border: 1.5px solid var(--color-border);
  font-size: 14px;
  outline: none;
  transition: 0.2s ease;
  font-family: inherit;
  background: var(--color-bg-muted);
}

.field input:focus,
.field textarea:focus,
.field select:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.06);
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}

.seed-list {
  margin: 16px 0 12px;
  background: var(--color-bg-muted);
  border-radius: 14px;
  padding: 12px 16px;
  font-size: 14px;
  color: var(--color-foreground);
  max-height: 120px;
  overflow-y: auto;
}

.seed-item {
  padding: 6px 0;
  border-bottom: 1px solid var(--color-border);
  display: flex;
  justify-content: space-between;
}

.seed-item:last-child {
  border-bottom: none;
}

.tag {
  background: var(--color-primary-bg);
  color: var(--color-primary);
  font-size: 11px;
  padding: 2px 12px;
  border-radius: 30px;
  font-weight: 600;
}

/* 滚动条 */
.chat-messages::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: var(--color-border-strong);
  border-radius: 20px;
}

/* 响应式 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px;
    padding: 16px 10px;
  }
}
</style>
