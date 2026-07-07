import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 认证状态管理
 * 单机版：硬编码用户信息
 */
export const useAuthStore = defineStore('auth', () => {
  const userId = ref('d85af2b2-cce8-4086-8668-6ea1c69d04a9') // 测试用用户 ID
  const username = ref('echo')
  const nickname = ref('Echo')

  function setUser(user) {
    userId.value = user.id
    username.value = user.username
    nickname.value = user.nickname
  }

  function clearUser() {
    // 单机版不退出
  }

  return { userId, username, nickname, setUser, clearUser }
})
