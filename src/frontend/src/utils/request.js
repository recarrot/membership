import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearAuth, isTokenValid } from '@/utils/auth'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
    baseURL: import.meta.env.DEV ? '' : '/',
    timeout: 5000,
    withCredentials: true
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 跳过登录请求的token验证
    if (config.url === '/api/auth/login') {
      return config;
    }

    // 其他请求验证token
    const token = getToken()
    if (!token) {
      clearAuth()
      return Promise.reject(new Error('请先登录'))
    }

    // 验证token有效性
    if (!isTokenValid()) {
      clearAuth()
      return Promise.reject(new Error('登录已过期，请重新登录'))
    }

    // 添加token到请求头
    config.headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
    config.headers['Content-Type'] = 'application/json';
    return config;
  },
  error => {
    return Promise.reject(error);
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      clearAuth()
    }

    // 解析错误信息
    let errorMessage = '请求失败'
    if (error.response?.data) {
      if (typeof error.response.data === 'object') {
        errorMessage = error.response.data.message || Object.values(error.response.data)[0] || '未知错误'
      } else {
        errorMessage = error.response.data
      }
    }

    // 显示错误信息
    ElMessage.error(errorMessage)
    return Promise.reject(error)
  }
)

export default service

// 更新会员手机号
export function updateMemberPhoneNumber(memberId, phoneNumber) {
  return request({
    url: `/api/members/${memberId}/phone`,  // 使用 /phone
    method: 'put',
    data: { phoneNumber }
  })
}