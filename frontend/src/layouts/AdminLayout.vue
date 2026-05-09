<template>
  <el-container class="admin-layout">
    <el-aside width="240px" class="aside">
      <div class="logo-section">
        <span class="logo-icon">✦</span>
        <div class="logo-text">
          <span class="logo-name">GRAND MAJESTIC</span>
          <span class="logo-sub">Management</span>
        </div>
      </div>
      
      <div class="menu-section">
        <el-menu :default-active="activeMenu" router background-color="transparent" text-color="#6b6b6b" active-text-color="#8b7355">
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/rooms">
            <el-icon><House /></el-icon>
            <span>房间管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/reservations">
            <el-icon><Tickets /></el-icon>
            <span>预订管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/deposits">
            <el-icon><Money /></el-icon>
            <span>定金管理</span>
          </el-menu-item>
        </el-menu>
      </div>
      
      <div class="aside-footer">
        <router-link to="/rooms" class="back-link">
          <el-icon><Back /></el-icon>
          <span>返回前台</span>
        </router-link>
      </div>
    </el-aside>
    
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin' }">控制台</el-breadcrumb-item>
            <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <div class="user-info">
            <div class="user-avatar">
              {{ (userStore.userInfo.realName || userStore.userInfo.username || 'U').charAt(0).toUpperCase() }}
            </div>
            <div class="user-detail">
              <span class="user-name">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
              <span class="user-role">管理员</span>
            </div>
          </div>
          <el-button class="logout-btn" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
          </el-button>
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { Back, SwitchButton, User, House, Tickets, Money } from '@element-plus/icons-vue'

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

.admin-layout {
  height: 100vh;
  background: $bg;
}

.aside {
  background: #fff;
  border-right: 1px solid $border;
  display: flex;
  flex-direction: column;
}

.logo-section {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid $border;
  
  .logo-icon {
    font-size: 24px;
    color: $primary;
  }
  
  .logo-text {
    display: flex;
    flex-direction: column;
    
    .logo-name {
      
      font-size: 12px;
      font-weight: 500;
      color: $primary;
      letter-spacing: 1.5px;
    }
    
    .logo-sub {
      font-size: 10px;
      color: $text-secondary;
      letter-spacing: 1px;
    }
  }
}

.menu-section {
  flex: 1;
  padding: 16px 0;
  
  :deep(.el-menu) {
    border-right: none;
    
    .el-menu-item {
      height: 48px;
      line-height: 48px;
      margin: 4px 12px;
      border-radius: 6px;
      font-size: 13px;
      
      .el-icon {
        font-size: 18px;
        margin-right: 10px;
      }
      
      &:hover {
        background: $bg-warm;
      }
      
      &.is-active {
        background: rgba($primary, 0.08);
        font-weight: 500;
      }
    }
  }
}

.aside-footer {
  padding: 16px 20px;
  border-top: 1px solid $border;
  
  .back-link {
    display: flex;
    align-items: center;
    gap: 8px;
    color: $text-secondary;
    text-decoration: none;
    font-size: 13px;
    padding: 10px 12px;
    border-radius: 6px;
    transition: all 0.3s ease;
    
    &:hover {
      background: $bg-warm;
      color: $primary;
    }
  }
}

.main-container {
  background: $bg;
}

.header {
  height: 64px;
  background: #fff;
  border-bottom: 1px solid $border;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 14px;
  background: $bg-warm;
  border-radius: 8px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  color: #fff;
}

.user-detail {
  display: flex;
  flex-direction: column;
  
  .user-name {
    font-size: 13px;
    color: $text;
    font-weight: 500;
  }
  
  .user-role {
    font-size: 11px;
    color: $primary;
  }
}

.logout-btn {
  width: 36px;
  height: 36px;
  padding: 0;
  background: transparent;
  border: 1px solid $border;
  border-radius: 6px;
  color: $text-secondary;
  
  &:hover {
    border-color: $primary;
    color: $primary;
    background: rgba($primary, 0.05);
  }
  
  .el-icon {
    font-size: 16px;
  }
}

.main {
  background: $bg;
  padding: 24px;
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
