import api from './index'

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
