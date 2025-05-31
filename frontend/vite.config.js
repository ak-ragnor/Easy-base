import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
    plugins: [react()],

    // Base public path
    base: '/easy-base/',

    // Build configuration
    build: {
        outDir: 'build',
        assetsDir: 'static',
        sourcemap: false,
        minify: 'esbuild',
        target: 'es2015',

        rollupOptions: {
            output: {
                assetFileNames: 'static/[ext]/[name]-[hash][extname]',
                chunkFileNames: 'static/js/[name]-[hash].js',
                entryFileNames: 'static/js/[name]-[hash].js'
            }
        }
    },

    // Development server
    server: {
        port: 3000,
        host: '0.0.0.0',
        open: false,
        cors: true,

        // Proxy API requests to backend
        proxy: {
            '/easy-base/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false
            }
        }
    },

    // Preview server
    preview: {
        port: 4173,
        host: '0.0.0.0',
        open: false
    },

    // Optimization
    optimizeDeps: {
        include: ['react', 'react-dom', 'react-router-dom']
    }
})