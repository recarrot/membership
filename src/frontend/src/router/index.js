import { createRouter, createWebHistory } from 'vue-router'
import { isTokenValid, clearAuth } from '@/utils/auth'
import Login from '@/views/login.vue'
import MemberList from '@/views/memberlist.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/members',
    name: 'Members',
    component: MemberList,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const isValid = isTokenValid()

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isValid) {
      clearAuth()
      next('/login')
      return
    }
  }

  next()
})

export default router