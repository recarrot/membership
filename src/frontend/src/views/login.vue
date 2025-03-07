<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>会员管理系统登录</h2>
      </template>
      <el-form :model="loginForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"/>
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/user'
import { ElMessage } from 'element-plus'
import { setToken } from '@/utils/auth'

const router = useRouter()
const loginForm = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  try {
    const response = await login(loginForm.value);
    
    if (!response.token) {
      throw new Error('服务器返回的数据中没有token');
    }
    
    // 保存token和用户信息
    setToken(response.token);
    localStorage.setItem('username', response.username);
    
    ElMessage.success('登录成功');
    router.push('/members');
  } catch (error) {
    ElMessage.error('登录失败：' + (error.response?.data?.message || error.message || '未知错误'));
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
}

.el-card__header {
  text-align: center;
  padding: 15px;
}

h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}
</style>