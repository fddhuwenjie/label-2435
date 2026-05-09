<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="header-content">
        <div class="logo-section">
          <span class="logo-icon">✦</span>
          <div class="logo-text">
            <span class="logo-name">GRAND MAJESTIC</span>
            <span class="logo-sub">HOTEL & RESORTS</span>
          </div>
        </div>
        
        <nav class="nav-menu">
          <router-link to="/rooms" :class="{ active: activeMenu === '/rooms' }">探索客房</router-link>
          <router-link to="/reservations" :class="{ active: activeMenu === '/reservations' }">我的预订</router-link>
          <router-link to="/profile" :class="{ active: activeMenu === '/profile' }">个人中心</router-link>
          <router-link v-if="userStore.isAdmin" to="/admin" class="admin-link">管理后台</router-link>
        </nav>
        
        <div class="user-section">
          <div class="user-info">
            <div class="user-avatar">
              {{ (userStore.userInfo.realName || userStore.userInfo.username || 'U').charAt(0).toUpperCase() }}
            </div>
            <span class="user-name">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
          </div>
          <button class="logout-btn" @click="handleLogout">退出</button>
        </div>
      </div>
    </el-header>
    
    <el-main class="main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
    
    <el-footer class="footer">
      <div class="footer-content">
        <span class="footer-brand">✦ GRAND MAJESTIC</span>
        <span class="footer-divider">|</span>
        <span class="footer-text">Where Elegance Meets Comfort</span>
        <span class="footer-divider">|</span>
        <span class="footer-copyright">© 2026</span>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const activeMenu = computed(() => route.path)
const handleLogout = () => { userStore.logout(); router.push('/login') }
</script>

<style scoped lang="scss">
$primary: #8b7355;
$primary-light: #a89078;
$bg: #faf9f7;
$bg-warm: #f5f3f0;
$text: #2c2c2c;
$text-secondary: #6b6b6b;
$border: #e8e5e1;

.layout-container {
  min-height: 100vh;
  background: $bg;
}

.header {
  height: auto;
  padding: 0;
  background: #fff;
  border-bottom: 1px solid $border;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .logo-icon {
    font-size: 24px;
    color: $primary;
  }
  
  .logo-text {
    display: flex;
    flex-direction: column;
    
    .logo-name {
      
      font-size: 14px;
      font-weight: 500;
      color: $primary;
      letter-spacing: 2px;
    }
    
    .logo-sub {
      font-size: 8px;
      color: $text-secondary;
      letter-spacing: 1.5px;
      text-transform: uppercase;
    }
  }
}

.nav-menu {
  display: flex;
  gap: 32px;
  
  a {
    color: $text-secondary;
    text-decoration: none;
    font-size: 13px;
    font-weight: 500;
    letter-spacing: 0.5px;
    padding: 8px 0;
    position: relative;
    transition: color 0.3s ease;
    
    &:hover {
      color: $primary;
    }
    
    &.active {
      color: $primary;
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 2px;
        background: $primary;
        border-radius: 1px;
      }
    }
    
    &.admin-link {
      color: $primary-light;
    }
  }
}

.user-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.user-name {
  font-size: 13px;
  color: $text;
  font-weight: 500;
}

.logout-btn {
  padding: 8px 16px;
  background: transparent;
  border: 1px solid $border;
  border-radius: 4px;
  color: $text-secondary;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    border-color: $primary;
    color: $primary;
  }
}

.main {
  flex: 1;
  background: $bg;
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.footer {
  height: auto;
  background: #fff;
  border-top: 1px solid $border;
  padding: 20px 24px;
}

.footer-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  font-size: 12px;
  color: $text-secondary;
  
  .footer-brand {
    
    color: $primary;
    letter-spacing: 2px;
  }
  
  .footer-divider {
    color: $border;
  }
  
  .footer-text {
    font-style: italic;
  }
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

@media (max-width: 768px) {
  .header-content { flex-wrap: wrap; gap: 16px; }
  .nav-menu { order: 3; width: 100%; justify-content: center; gap: 20px; padding-top: 12px; border-top: 1px solid $border; }
  .user-name { display: none; }
}
</style>
