import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8099/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器 — 自动携带 token（后续 JWT 加这里）
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('echo_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器 — 统一错误处理
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const msg = error.response?.data?.error || '网络请求失败'
    console.error('API Error:', msg)
    return Promise.reject(error)
  }
)

export default api
