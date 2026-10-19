import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Em desenvolvimento, /api e /uploads são encaminhados para o back-end
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Permite abrir pelo endereço do GitHub Codespaces (*.app.github.dev)
    allowedHosts: ['.app.github.dev'],
    proxy: {
      '/api': 'http://localhost:3333',
      '/uploads': 'http://localhost:3333',
    },
  },
});
