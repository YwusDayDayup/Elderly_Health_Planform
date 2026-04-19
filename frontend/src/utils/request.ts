import axios from 'axios'
import type { AxiosInstance, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'

interface Result<T = any> {
  success: boolean
  code: string
  message: string
  data: T
}

const instance: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    const res = response.data
    // 如果 success 为 false，说明是业务错误
    if (!res.success) {
      ElMessage.error(res.message || 'Error')
      
      // 特殊处理 401 未登录
      if (res.code === '401' || response.status === 401) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res as any
  },
  (error) => {
    console.error('Network Error:', error)
    const status = error.response?.status
    const message = error.response?.data?.message || error.message || '网络错误'
    
    if (status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default instance
