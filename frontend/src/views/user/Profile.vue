<template>
  <div class="profile-page">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
      <p class="page-subtitle">管理您的账户信息</p>
      <div class="header-decoration"><span class="line"></span><span class="diamond">◇</span><span class="line"></span></div>
    </div>

    <div class="profile-content">
      <div class="user-header">
        <div class="avatar">{{ (form.realName || form.username || 'U').charAt(0).toUpperCase() }}</div>
        <div class="user-meta">
          <h3>{{ form.realName || form.username }}</h3>
          <p>尊贵会员</p>
        </div>
      </div>
      
      <div class="section-divider"><span class="text">账户信息</span></div>
      
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="profile-form">
        <div class="form-grid">
          <el-form-item label="用户名">
            <el-input v-model="form.username" disabled />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="form.idCard" placeholder="请输入身份证号" />
          </el-form-item>
          <el-form-item label="地址" prop="address" class="full-width">
            <el-input v-model="form.address" placeholder="请输入地址" />
          </el-form-item>
        </div>
        
        <div class="section-divider"><span class="text">安全设置</span></div>
        
        <div class="form-grid">
          <el-form-item label="新密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="不修改请留空" show-password />
          </el-form-item>
        </div>
        
        <el-form-item class="form-actions">
          <el-button type="primary" :loading="loading" @click="handleSave">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUserInfo } from '../../api/user'

const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', realName: '', phone: '', idCard: '', address: '', password: '' })

const rules = {
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  password: [{ min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }]
}

const loadUserInfo = async () => {
  const res = await getUserInfo()
  Object.assign(form, res.data)
  form.password = ''
}

const handleSave = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const data = { ...form }
    if (!data.password) delete data.password
    delete data.username
    await updateUserInfo(data)
    ElMessage.success('保存成功')
    loadUserInfo()
  } finally { loading.value = false }
}

onMounted(() => { loadUserInfo() })
</script>

<style scoped lang="scss">
$primary: #8b7355;
$primary-light: #a89078;
$bg: #faf9f7;
$bg-warm: #f5f3f0;
$text: #2c2c2c;
$text-secondary: #6b6b6b;
$border: #e8e5e1;

.profile-page { min-height: 100%; }

.page-header {
  text-align: center;
  padding: 40px 20px 32px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  
  .page-title {  font-size: 32px; font-weight: 500; color: $text; margin-bottom: 8px; }
  .page-subtitle { font-size: 14px; color: $text-secondary; }
  .header-decoration { display: flex; align-items: center; justify-content: center; gap: 12px; margin-top: 20px;
    .line { width: 60px; height: 1px; background: $border; }
    .diamond { color: $primary; font-size: 10px; }
  }
}

.profile-content {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.user-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 32px;
  
  .avatar {
    width: 72px;
    height: 72px;
    border-radius: 50%;
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
    font-weight: 600;
    color: #fff;
  }
  
  .user-meta {
    h3 {  font-size: 22px; color: $text; margin-bottom: 4px; }
    p { font-size: 13px; color: $primary; }
  }
}

.section-divider {
  display: flex;
  align-items: center;
  margin: 24px 0;
  
  &::before, &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: $border;
  }
  
  .text {
    padding: 0 16px;
    font-size: 12px;
    color: $text-secondary;
    text-transform: uppercase;
    letter-spacing: 1px;
  }
}

.profile-form {
  .form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 0 24px;
    
    .full-width { grid-column: 1 / -1; }
  }
  
  :deep(.el-form-item) { 
    margin-bottom: 20px;
    
    .el-input {
      width: 100%;
    }
  }
  
  .form-actions {
    margin-top: 32px;
    margin-bottom: 0;
    :deep(.el-form-item__content) { justify-content: center; }
    .el-button { min-width: 160px; }
  }
}

@media (max-width: 768px) {
  .profile-form .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
