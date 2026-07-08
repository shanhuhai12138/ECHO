<template>
  <div class="quiz-page">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="brand">⟡ ECHO</div>
      <router-link to="/" class="back-btn">← 返回首页</router-link>
      <div class="sidebar-section">题库分类</div>
      <div class="category-list">
        <div
          v-for="cat in categories"
          :key="cat.id"
          class="category-item"
          :class="{ active: activeCategoryId === cat.id }"
          @click="selectCategory(cat)"
        >
          <div class="category-info">
            <span class="category-name">{{ cat.name }}</span>
            <span class="category-desc">{{ cat.description }}</span>
          </div>
          <div class="category-progress" v-if="cat.progress !== undefined">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: cat.progress + '%' }"></div>
            </div>
            <span class="progress-text">{{ cat.progress }}%</span>
          </div>
        </div>
      </div>
    </aside>

    <!-- 主区域 -->
    <main class="quiz-main">
      <header class="quiz-header">
        <h2>{{ activeCategory?.name || '选择测评' }}</h2>
        <p class="quiz-sub">{{ activeCategory?.description || '' }}</p>
      </header>

      <div class="quiz-content" v-if="activeCategory">
        <!-- 进度条 -->
        <div class="quiz-progress">
          <div class="progress-info">
            <span>题目 {{ currentQuestionIndex + 1 }} / {{ questions.length }}</span>
            <span>{{ Math.round((currentQuestionIndex + 1) / questions.length * 100) }}%</span>
          </div>
          <div class="progress-bar-large">
            <div class="progress-fill-large" :style="{ width: ((currentQuestionIndex + 1) / questions.length * 100) + '%' }"></div>
          </div>
        </div>

        <!-- 题目 -->
        <div class="question-container" v-if="questions.length > 0">
          <h3 class="question-text">{{ currentQuestion?.questionText }}</h3>

          <div class="options-list">
            <div
              v-for="(option, idx) in currentQuestion?.options"
              :key="option.id"
              class="option-item"
              :class="{ selected: selectedAnswer === idx }"
              @click="selectAnswer(idx)"
            >
              <div class="option-radio" :class="{ checked: selectedAnswer === idx }"></div>
              <div class="option-text">{{ option.text }}</div>
            </div>
          </div>

          <div class="question-actions">
            <button
              class="btn btn-secondary"
              @click="prevQuestion"
              :disabled="currentQuestionIndex === 0"
            >
              ← 上一题
            </button>
            <button
              v-if="currentQuestionIndex < questions.length - 1"
              class="btn btn-primary"
              @click="nextQuestion"
              :disabled="selectedAnswer === null"
            >
              下一题 →
            </button>
            <button
              v-else
              class="btn btn-primary"
              @click="submitQuiz"
              :disabled="selectedAnswer === null"
            >
              提交测评 ✓
            </button>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state">
          <p>暂无题目，请联系管理员添加。</p>
        </div>
      </div>

      <!-- 测评完成 -->
      <div v-if="showResult" class="result-container">
        <div class="result-card">
          <h3>🎉 测评完成！</h3>
          <p class="result-sub">你的画像已更新，快去和 ECHO 聊聊吧</p>
          <div class="result-stats">
            <div class="stat-item">
              <span class="stat-value">{{ resultStats.completed }}</span>
              <span class="stat-label">完成题目</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ resultStats.dimensions }}</span>
              <span class="stat-label">影响维度</span>
            </div>
          </div>
          <div class="result-actions">
            <router-link to="/" class="btn btn-primary">返回主页</router-link>
            <button class="btn btn-secondary" @click="showResult = false">再做一次</button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import api from '../api'

const router = useRouter()
const authStore = useAuthStore()

const categories = ref([])
const activeCategoryId = ref(null)
const activeCategory = ref(null)
const questions = ref([])
const currentQuestionIndex = ref(0)
const selectedAnswer = ref(null)
const answers = ref({})  // 当前分类的答案
const showResult = ref(false)
const resultStats = ref({ completed: 0, dimensions: 0 })

// 按分类暂存答案到 localStorage
function saveAnswersToStorage(categoryId, ans) {
  try {
    const saved = JSON.parse(localStorage.getItem('echo_quiz_answers') || '{}')
    saved[categoryId] = ans
    localStorage.setItem('echo_quiz_answers', JSON.stringify(saved))
  } catch (e) { /* ignore */ }
}

function loadAnswersFromStorage(categoryId) {
  try {
    const saved = JSON.parse(localStorage.getItem('echo_quiz_answers') || '{}')
    return saved[categoryId] || {}
  } catch (e) { return {} }
}

onMounted(async () => {
  await loadCategories()
  if (categories.value.length > 0) {
    selectCategory(categories.value[0])
  }
})

async function loadCategories() {
  try {
    const res = await api.get('/quiz/categories')
    categories.value = res.data
  } catch (e) {
    console.error('Failed to load categories:', e)
  }
}

async function selectCategory(cat) {
  // 保存当前分类的答案再切换
  if (activeCategoryId.value) {
    saveAnswersToStorage(activeCategoryId.value, answers.value)
  }

  activeCategoryId.value = cat.id
  activeCategory.value = cat
  currentQuestionIndex.value = 0
  selectedAnswer.value = null
  showResult.value = false

  // 恢复已保存的答案
  answers.value = loadAnswersFromStorage(cat.id)
  await loadQuestions(cat.id)
}

async function loadQuestions(categoryId) {
  try {
    const res = await api.get(`/quiz/${categoryId}/questions`)
    questions.value = res.data
  } catch (e) {
    console.error('Failed to load questions:', e)
  }
}

const currentQuestion = computed(() => {
  if (questions.value.length === 0) return null
  return questions.value[currentQuestionIndex.value]
})

function selectAnswer(idx) {
  selectedAnswer.value = idx
}

function nextQuestion() {
  if (selectedAnswer.value !== null && currentQuestion.value) {
    answers.value[currentQuestion.value.id] = selectedAnswer.value
  }
  currentQuestionIndex.value++
  selectedAnswer.value = null
}

function prevQuestion() {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
    selectedAnswer.value = answers.value[questions.value[currentQuestionIndex.value].id] ?? null
  }
}

async function submitQuiz() {
  // 保存最后一题答案
  if (selectedAnswer.value !== null && currentQuestion.value) {
    answers.value[currentQuestion.value.id] = selectedAnswer.value
  }

  try {
    const res = await api.post(`/quiz/${activeCategory.value.id}/submit?userId=${authStore.userId}`, { answers: answers.value })
    showResult.value = true
    resultStats.value = {
      completed: Object.keys(answers.value).length,
      dimensions: Object.keys(JSON.parse(res.data.profileDelta || '{}')).length,
    }
  } catch (e) {
    alert('提交失败：' + (e.response?.data?.error || e.message))
  }
}
</script>

<style scoped>
.quiz-page {
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

.category-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.category-item {
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

.category-item:hover {
  background: var(--color-primary-bg);
}

.category-item.active {
  background: var(--color-bg-card);
  border-color: var(--color-border-strong);
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.08);
}

.category-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.category-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-foreground);
}

.category-desc {
  font-size: 11px;
  color: var(--color-primary-light);
}

.category-progress {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-bar {
  width: 60px;
  height: 6px;
  background: var(--color-bg-muted);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: var(--color-primary);
  border-radius: 3px;
}

.progress-text {
  font-size: 11px;
  color: var(--color-primary);
  font-weight: 600;
}

/* 主区域 */
.quiz-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  padding: var(--space-xl);
  overflow-y: auto;
}

.quiz-header {
  margin-bottom: var(--space-xl);
}

.quiz-header h2 {
  font-family: var(--font-serif);
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-text-heading);
  margin: 0 0 var(--space-xs);
}

.quiz-sub {
  font-size: 1rem;
  color: var(--color-text-light);
  margin: 0;
}

/* 进度条 */
.quiz-progress {
  margin-bottom: var(--space-xl);
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--space-sm);
  font-size: 0.875rem;
  color: var(--color-text);
  font-weight: 500;
}

.progress-bar-large {
  height: 8px;
  background: var(--color-bg-muted);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill-large {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
  border-radius: 4px;
  transition: width 0.3s ease;
}

/* 题目容器 */
.question-container {
  max-width: 700px;
  margin: 0 auto;
  width: 100%;
}

.question-text {
  font-family: var(--font-serif);
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-foreground);
  margin: 0 0 var(--space-xl);
  line-height: 1.5;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
  margin-bottom: var(--space-xl);
}

.option-item {
  display: flex;
  align-items: flex-start;
  gap: var(--space-md);
  padding: var(--space-md) var(--space-lg);
  background: var(--color-bg-card);
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-base);
}

.option-item:hover {
  border-color: var(--color-primary-light);
  background: var(--color-primary-bg);
}

.option-item.selected {
  border-color: var(--color-primary);
  background: var(--color-primary-bg);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.12);
}

.option-radio {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid var(--color-border);
  flex-shrink: 0;
  margin-top: 2px;
  transition: all var(--transition-fast);
}

.option-radio.checked {
  border-color: var(--color-primary);
  background: var(--color-primary);
  position: relative;
}

.option-radio.checked::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 8px;
  height: 8px;
  background: white;
  border-radius: 50%;
}

.option-text {
  font-size: 0.9375rem;
  color: var(--color-foreground);
  line-height: 1.5;
}

.question-actions {
  display: flex;
  justify-content: space-between;
  gap: var(--space-md);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: var(--space-2xl) 0;
  color: var(--color-text-light);
}

/* 结果卡片 */
.result-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.result-card {
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  padding: var(--space-2xl);
  text-align: center;
  max-width: 500px;
  box-shadow: var(--shadow-lg);
}

.result-card h3 {
  font-family: var(--font-serif);
  font-size: 1.75rem;
  margin: 0 0 var(--space-md);
  color: var(--color-foreground);
}

.result-sub {
  font-size: 1rem;
  color: var(--color-text);
  margin: 0 0 var(--space-xl);
}

.result-stats {
  display: flex;
  justify-content: center;
  gap: var(--space-xl);
  margin-bottom: var(--space-xl);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-primary);
}

.stat-label {
  font-size: 0.875rem;
  color: var(--color-text-light);
}

.result-actions {
  display: flex;
  gap: var(--space-md);
  justify-content: center;
}

/* 按钮 */
.btn {
  padding: var(--space-sm) var(--space-lg);
  border-radius: var(--radius-md);
  font-size: 0.9375rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-base);
  border: none;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background: var(--color-primary);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: var(--color-primary-dark);
}

.btn-secondary {
  background: var(--color-bg-card);
  color: var(--color-primary);
  border: 1.5px solid var(--color-border-strong);
}

.btn-secondary:hover:not(:disabled) {
  background: var(--color-primary-bg);
  border-color: var(--color-primary);
}

/* 响应式 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }
  .question-actions {
    flex-direction: column;
  }
}
</style>
