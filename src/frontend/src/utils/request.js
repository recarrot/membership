import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 5000,
  withCredentials: true  // 添加这个配置
})

// 在请求拦截器中添加调试日志
service.interceptors.request.use(
  config => {
    console.log('Sending Request:', config.method, config.url, config);
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    config.headers['Content-Type'] = 'application/json'
    return config
  },
  error => {
    console.error('Request Error:', error);
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    // 检查是否是 401 未授权错误
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }

    // 解析错误信息
    let errorMessage = '请求失败'
    if (error.response?.data) {
      // 如果返回的是对象，尝试提取第一个错误信息
      if (typeof error.response.data === 'object') {
        errorMessage = Object.values(error.response.data)[0] || '未知错误'
      } else {
        errorMessage = error.response.data || '未知错误'
      }
    }

    // 显示错误信息
    ElMessage.error(errorMessage)
    return Promise.reject(error)
  }
)

export default service