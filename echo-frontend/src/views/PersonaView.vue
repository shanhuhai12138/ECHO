<template>
  <div class="persona-page">
    <header class="page-header">
      <router-link to="/" class="back-btn">← 返回首页</router-link>
      <h1>人格画像</h1>
    </header>

    <div class="persona-content">
      <div v-if="personaLoading" class="loading">加载中...</div>
      <div v-else class="dimensions-list">
        <div v-for="dim in personaDims" :key="dim.dimensionKey" class="dim-card">
          <div class="dim-header">
            <span class="dim-label">{{ dim.dimensionLabel }}</span>
            <span class="dim-score">{{ dim.score || 0 }}/100</span>
          </div>
          <div class="dim-bar">
            <div class="dim-fill" :style="{ width: (dim.score || 0) + '%' }"></div>
          </div>
          <p v-if="dim.textSummary" class="dim-summary">{{ dim.textSummary }}</p>
          <div class="dim-actions">
            <button class="btn-edit" @click="editDimension(dim)">编辑</button>
            <button class="btn-delete" @click="deleteDimension(dim.dimensionKey)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
      <div class="modal">
        <h3>编辑维度</h3>
        <div class="field">
          <label>维度名称</label>
          <input v-model="editForm.dimensionLabel" type="text" />
        </div>
        <div class="field">
          <label>分数 (0-100)</label>
          <input v-model.number="editForm.score" type="number" min="0" max="100" />
        </div>
        <div class="field">
          <label>描述</label>
          <textarea v-model="editForm.textSummary" rows="3"></textarea>
        </div>
        <div class="actions">
          <button class="btn-secondary" @click="showEditModal = false">取消</button>
          <button class="btn-primary" @click="saveDimension" :disabled="saving">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const personaDims = ref([])
const personaLoading = ref(true)
const showEditModal = ref(false)
const saving = ref(false)

const editForm = ref({
  dimensionKey: '',
  dimensionLabel: '',
  score: 0,
  textSummary: '',
  source: 'manual',
})

onMounted(() => {
  loadPersona()
})

async function loadPersona() {
  personaLoading.value = true
  try {
    const res = await api.get(`/persona/${authStore.userId}`)
    personaDims.value = res.data.dimensions || []
  } catch (e) {
    console.error('Failed to load persona:', e)
  } finally {
    personaLoading.value = false
  }
}

function editDimension(dim) {
  editForm.value = {
    dimensionKey: dim.dimensionKey,
    dimensionLabel: dim.dimensionLabel,
    score: dim.score || 0,
    textSummary: dim.textSummary || '',
    source: dim.source || 'manual',
  }
  showEditModal.value = true
}

async function saveDimension() {
  saving.value = true
  try {
    await api.put(`/persona/${authStore.userId}/dimensions`, editForm.value)
    showEditModal.value = false
    loadPersona()
  } catch (e) {
    alert('保存失败：' + (e.response?.data?.error || e.message))
  } finally {
    saving.value = false
  }
}

async function deleteDimension(key) {
  if (!confirm('确定删除这个维度？')) return
  try {
    await api.delete(`/persona/${authStore.userId}/dimensions/${key}`)
    loadPersona()
  } catch (e) {
    alert('删除失败')
  }
}
</script>

<style scoped>
.persona-page {
  min-height: 100vh;
  background: var(--color-bg);
  padding: var(--space-xl);
}

.page-header {
  max-width: 800px;
  margin: 0 auto var(--space-xl);
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.back-btn {
  padding: 8px 16px;
  background: var(--color-bg-card);
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-md);
  color: var(--color-primary);
  font-size: 0.875rem;
  font-weight: 500;
  text-decoration: none;
  transition: all var(--transition-base);
}

.back-btn:hover {
  background: var(--color-primary-bg);
  border-color: var(--color-primary);
}

.page-header h1 {
  font-family: var(--font-serif);
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-text-heading);
  margin: 0;
}

.persona-content {
  max-width: 800px;
  margin: 0 auto;
}

.loading {
  text-align: center;
  padding: var(--space-2xl);
  color: var(--color-text-light);
}

.dimensions-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.dim-card {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  padding: var(--space-lg);
  box-shadow: var(--shadow-sm);
}

.dim-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--space-sm);
}

.dim-label {
  font-weight: 600;
  font-size: 1rem;
  color: var(--color-foreground);
}

.dim-score {
  font-weight: 600;
  font-size: 0.875rem;
  color: var(--color-primary);
}

.dim-bar {
  height: 8px;
  background: var(--color-bg-muted);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: var(--space-sm);
}

.dim-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
  border-radius: 4px;
  transition: width 0.5s ease;
}

.dim-summary {
  font-size: 0.875rem;
  color: var(--color-text);
  margin: 0 0 var(--space-md);
  font-style: italic;
}

.dim-actions {
  display: flex;
  gap: var(--space-sm);
}

.btn-edit {
  padding: 6px 16px;
  background: var(--color-primary-bg);
  border: 1.5px solid var(--color-border-strong);
  border-radius: var(--radius-md);
  color: var(--color-primary);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-edit:hover {
  background: var(--color-primary);
  color: white;
}

.btn-delete {
  padding: 6px 16px;
  background: var(--color-destructive-bg);
  border: 1.5px solid #FECACA;
  border-radius: var(--radius-md);
  color: var(--color-destructive);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-delete:hover {
  background: var(--color-destructive);
  color: white;
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
.field textarea {
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
.field textarea:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.12);
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-sm);
  margin-top: var(--space-lg);
}
</style>
