import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, ''),
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
  },
  base: '/'
}
})