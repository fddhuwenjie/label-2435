<template>
  <div class="register-page">
    <div class="register-container">
      <div class="brand-header">
        <div class="brand-logo">✦</div>
        <h1 class="brand-name">GRAND MAJESTIC</h1>
        <p class="brand-tagline">HOTEL & RESORTS</p>
      </div>

      <div class="register-card">
        <div class="card-header">
          <h2>创建账户</h2>
          <p>加入我们，享受专属尊贵礼遇</p>
        </div>
        
        <el-form ref="formRef" :model="form" :rules="rules" class="register-form" label-position="top">
          <div class="form-row">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
          </div>
          
          <div class="form-row">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </div>
          
          <div class="form-row">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" />
            </el-form-item>
            <el-form-item label="联系地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入地址" />
            </el-form-item>
          </div>
          
          <el-form-item>
            <el-button type="primary" :loading="loading" class="register-btn" @click="handleRegister">
              {{ loading ? '正在注册...' : '立即注册' }}
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="card-footer">
          <router-link to="/login">已有账户？<span>立即登录</span></router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '', realName: '', phone: '', idCard: '', address: '' })

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度为3-50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
$primary: #8b7355;
$primary-light: #a89078;
$bg: #faf9f7;
$bg-warm: #f5f3f0;
$text: #2c2c2c;
$text-secondary: #6b6b6b;
$border: #e8e5e1;

.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #d4c4a8 0%, #e8dcc8 30%, #f0e6d8 60%, #f5f0e8 100%);
  background-size: 400% 400%;
  animation: gradientBG 20s ease infinite;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
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

.register-container {
  width: 100%;
  max-width: 640px;
  position: relative;
  z-index: 1;
}

.brand-header {
  text-align: center;
  margin-bottom: 32px;
  
  .brand-logo {
    font-size: 36px;
    color: $primary;
    margin-bottom: 12px;
  }
  
  .brand-name {
    
    font-size: 22px;
    font-weight: 500;
    color: $primary;
    letter-spacing: 5px;
    margin-bottom: 4px;
  }
  
  .brand-tagline {
    font-size: 10px;
    letter-spacing: 3px;
    color: $text-secondary;
    text-transform: uppercase;
  }
}

.register-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.06);
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
  
  h2 {
    
    font-size: 24px;
    font-weight: 500;
    color: $text;
    margin-bottom: 6px;
  }
  
  p {
    font-size: 13px;
    color: $text-secondary;
  }
}

.register-form {
  .form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
  
  :deep(.el-form-item) {
    margin-bottom: 20px;
    
    .el-form-item__label {
      color: $text-secondary;
      font-size: 12px;
      letter-spacing: 0.5px;
      text-transform: uppercase;
      padding-bottom: 6px;
    }
  }
  
  :deep(.el-input) {
    width: 100%;
  }
}

.register-btn {
  width: 100%;
  height: 46px;
  margin-top: 8px;
  font-size: 14px;
  letter-spacing: 2px;
}

.card-footer {
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

@media (max-width: 640px) {
  .register-card {
    padding: 30px 24px;
  }
  
  .register-form .form-row {
    grid-template-columns: 1fr;
    gap: 0;
  }
}
</style>
