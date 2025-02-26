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
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),  // 替换路径中的 `/api`
        configure: (proxy, options) => {
          // 用于调试代理
          proxy.on('error', (err, req, res) => {
            console.log('proxy error', err);
          });
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('Sending Request to the Target:', req.method, req.url);
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('Received Response from the Target:', proxyRes.statusCode);
          });
        }
      }
    }
  },
  build: {
    outDir: '../main/resources/static',  // 将前端构建输出到 Spring Boot 的静态资源目录
    assetsDir: 'static'  // 静态文件资源路径（例如图片、字体等）
  },
  base: '/'  // 设置应用的公共基础路径
});
