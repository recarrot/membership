import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')  // 设置别名
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 后端服务地址
        changeOrigin: true
      },
      '/members': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => '/'  // 重写路径，将/members请求重定向到前端根路径
      }
    }
  },
  build: {
    outDir: '../main/resources/static',  // 将前端构建输出到 Spring Boot 的静态资源目录
    assetsDir: 'static'  // 静态文件资源路径（例如图片、字体等）
  },
  base: '/'  // 设置应用的公共基础路径
});
