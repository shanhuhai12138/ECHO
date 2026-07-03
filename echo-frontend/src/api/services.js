import api from '../api'

/**
 * 用户注册
 */
export function register(username, password, nickname) {
  return api.post('/auth/register', { username, password, nickname })
}

/**
 * 获取用户的聊天框列表
 */
export function getChatBoxes(userId) {
  return api.get('/chatboxes', { params: { userId } })
}

/**
 * 创建聊天框
 */
export function createChatBox(userId, data) {
  return api.post(`/chatboxes/${userId}`, data)
}
