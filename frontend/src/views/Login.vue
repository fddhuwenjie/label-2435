<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧品牌区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="brand-logo">✦</div>
          <h1 class="brand-name">GRAND MAJESTIC</h1>
          <p class="brand-tagline">HOTEL & RESORTS</p>
          <div class="brand-divider">
            <span class="line"></span>
            <span class="diamond">◇</span>
            <span class="line"></span>
          </div>
          <p class="brand-slogan">Where Elegance Meets Comfort</p>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-section">
        <div class="form-card">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>登录您的账户，开启尊贵体验</p>
          </div>
          
          <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="用户名" 
                prefix-icon="User"
                size="large"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="密码" 
                prefix-icon="Lock" 
                show-password
                size="large"
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                :loading="loading" 
                size="large"
                class="login-btn"
                @click="handleLogin"
              >
                {{ loading ? '正在登录...' : '登 录' }}
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="form-footer">
            <router-link to="/register">
              还没有账户？<span>立即注册</span>
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data)
    ElMessage.success('登录成功')
    router.push(res.data.role === 1 ? '/admin' : '/rooms')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
$primary: #8b7355;
$primary-light: #a89078;
$accent: #c4a77d;
$bg: #faf9f7;
$bg-warm: #f5f3f0;
$text: #2c2c2c;
$text-secondary: #6b6b6b;
$border: #e8e5e1;

.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #d4c4a8 0%, #e8dcc8 30%, #f0e6d8 60%, #f5f0e8 100%);
  background-size: 400% 400%;
  animation: gradientBG 20s ease infinite;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: 
      radial-gradient(ellipse at 20% 80%, rgba(139,115,85,0.08) 0%, transparent 50%),
      radial-gradient(ellipse at 80% 20%, rgba(139,115,85,0.06) 0%, transparent 50%),
      radial-gradient(ellipse at 50% 50%, rgba(255,255,255,0.3) 0%, transparent 70%);
    pointer-events: none;
  }
  
  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background-image: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Ccircle cx='50' cy='50' r='1.5' fill='rgba(139,115,85,0.1)'/%3E%3C/svg%3E");
    background-size: 60px 60px;
    opacity: 0.6;
    pointer-events: none;
  }
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.login-container {
  display: flex;
  max-width: 1000px;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 
    0 25px 50px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
}

.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #8b7355 0%, #a89078 50%, #c4a77d 100%);
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background-image: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M30 0L30 60M0 30L60 30' stroke='%23ffffff' stroke-width='0.5' opacity='0.1'/%3E%3C/svg%3E");
    background-size: 60px 60px;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: -50%;
    right: -50%;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
    pointer-events: none;
  }
}

.brand-content {
  position: relative;
  text-align: center;
  z-index: 1;
}

.brand-logo {
  font-size: 56px;
  color: #fff;
  margin-bottom: 24px;
  text-shadow: 0 4px 20px rgba(0,0,0,0.2);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.brand-name {
  
  font-size: 28px;
  font-weight: 500;
  color: #fff;
  letter-spacing: 6px;
  margin-bottom: 8px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.2);
}

.brand-tagline {
  font-size: 11px;
  letter-spacing: 4px;
  color: rgba(255,255,255,0.8);
  text-transform: uppercase;
}

.brand-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin: 30px 0;
  
  .line {
    width: 50px;
    height: 1px;
    background: rgba(255,255,255,0.4);
  }
  
  .diamond {
    color: #fff;
    font-size: 10px;
  }
}

.brand-slogan {
  
  font-style: italic;
  font-size: 14px;
  color: rgba(255,255,255,0.9);
  letter-spacing: 1px;
}

.form-section {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.form-card {
  width: 100%;
  max-width: 360px;
}

.form-header {
  text-align: center;
  margin-bottom: 36px;
  
  h2 {
    
    font-size: 26px;
    font-weight: 500;
    color: $text;
    margin-bottom: 8px;
  }
  
  p {
    font-size: 13px;
    color: $text-secondary;
  }
}

.login-form {
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }
  
  :deep(.el-input) {
    width: 100%;
    
    .el-input__wrapper {
      padding: 4px 12px;
      border-radius: 8px;
      transition: all 0.3s ease;
      
      &:hover {
        box-shadow: 0 0 0 1px $primary-light;
      }
    }
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 14px;
  letter-spacing: 2px;
  border-radius: 8px;
  background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
  border: none;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba($primary, 0.4);
  }
}

.form-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid $border;
  
  a {
    font-size: 13px;
    color: $text-secondary;
    text-decoration: none;
    
    span {
      color: $primary;
      font-weight: 500;
      margin-left: 4px;
      
      &:hover {
        text-decoration: underline;
      }
    }
  }
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    border-radius: 16px;
  }
  
  .brand-section {
    padding: 40px 30px;
  }
  
  .brand-name {
    font-size: 22px;
    letter-spacing: 4px;
  }
  
  .brand-logo {
    font-size: 42px;
  }
  
  .form-section {
    padding: 40px 30px;
  }
}
</style>
