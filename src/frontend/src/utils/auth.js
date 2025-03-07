import router from '@/router'

export function getToken() {
  return localStorage.getItem('token')
}

export function setToken(token) {
  localStorage.setItem('token', token)
}

export function removeToken() {
  localStorage.removeItem('token')
}

export function clearAuth() {
  removeToken()
  localStorage.removeItem('username')
  // 只有在非登录页面时才重定向
  if (router.currentRoute.value.path !== '/login') {
    router.push('/login')
  }
}

export function isTokenValid() {
  const token = getToken()
  if (!token) {
    return false;
  }
  
  try {
    // 简单检查token格式
    const parts = token.split('.')
    if (parts.length !== 3) {
      return false;
    }
    
    // 检查token是否过期
    const payload = JSON.parse(atob(parts[1]))
    const exp = payload.exp * 1000 // 转换为毫秒
    const now = Date.now()
    
    if (now >= exp) {
      clearAuth()
      return false
    }
    
    return true
  } catch (error) {
    clearAuth()
    return false
  }
}

export function initAuth() {
  return isTokenValid(); // 只返回验证结果，不做额外处理
} 