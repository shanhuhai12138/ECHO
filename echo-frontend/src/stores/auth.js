import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 认证状态管理
 * 初期简化版：只存 userId 和用户名
 */
export const useAuthStore = defineStore('auth', () => {
  const userId = ref(null)
  const username = ref(null)
  const nickname = ref(null)

  function setUser(user) {
    userId.value = user.id
    username.value = user.username
    nickname.value = user.nickname
    localStorage.setItem('echo_userId', user.id)
    localStorage.setItem('echo_username', user.username)
  }

  function clearUser() {
    userId.value = null
    username.value = null
    nickname.value = null
    localStorage.removeItem('echo_userId')
    localStorage.removeItem('echo_username')
  }

  // 页面刷新时恢复状态
  function hydrate() {
    const storedId = localStorage.getItem('echo_userId')
    const storedName = localStorage.getItem('echo_username')
    if (storedId) userId.value = storedId
    if (storedName) username.value = storedName
  }

  hydrate()

  return { userId, username, nickname, setUser, clearUser }
})
