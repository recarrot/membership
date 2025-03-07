import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './assets/main.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
import { initAuth } from '@/utils/auth'

// 初始化认证状态
initAuth()

const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.use(ElementPlus, {
    locale: zhCn,
});
app.mount('#app')

