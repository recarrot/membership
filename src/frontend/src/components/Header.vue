<template>
  <div class="header">
    <div class="header-right">
      <span class="username">{{ username }}</span>
      <el-button type="text" @click="handleLogout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { clearAuth } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '')

const handleLogout = () => {
  clearAuth()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.header {
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-size: 14px;
  color: #606266;
}
</style> 