<template>
  <div class="admin-users">
    <div class="page-title-section">
      <h2>用户管理</h2>
      <p>管理系统所有用户账户</p>
    </div>

    <div class="search-card">
      <div class="search-form-wrapper">
        <el-form :inline="true" :model="searchForm" class="search-form-left">
          <el-form-item label="用户名"><el-input v-model="searchForm.username" placeholder="请输入用户名" clearable /></el-form-item>
          <el-form-item label="手机号"><el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable /></el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="search-form-right">
          <el-button type="success" @click="handleAdd">新增用户</el-button>
        </div>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }"><el-tag :type="row.role === 1 ? 'warning' : 'info'" size="small">{{ row.role === 1 ? '管理员' : '普通用户' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <el-dialog v-model="editDialogVisible" title="编辑用户" width="500px">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="用户名"><span class="form-value">{{ editForm.username }}</span></el-form-item>
        <el-form-item label="真实姓名" prop="realName"><el-input v-model="editForm.realName" placeholder="请输入真实姓名" style="width: 100%" /></el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="editForm.phone" placeholder="请输入手机号" style="width: 100%" /></el-form-item>
        <el-form-item label="身份证号" prop="idCard"><el-input v-model="editForm.idCard" placeholder="请输入身份证号" style="width: 100%" /></el-form-item>
        <el-form-item label="地址" prop="address"><el-input v-model="editForm.address" placeholder="请输入地址" style="width: 100%" /></el-form-item>
        <el-form-item label="新密码" prop="password"><el-input v-model="editForm.password" type="password" placeholder="不修改请留空" show-password style="width: 100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmitEdit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="addDialogVisible" title="新增用户" width="500px">
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px">
        <el-form-item label="用户名" prop="username"><el-input v-model="addForm.username" placeholder="请输入用户名" style="width: 100%" /></el-form-item>
        <el-form-item label="密码" prop="password"><el-input v-model="addForm.password" type="password" placeholder="请输入密码" show-password style="width: 100%" /></el-form-item>
        <el-form-item label="真实姓名" prop="realName"><el-input v-model="addForm.realName" placeholder="请输入真实姓名" style="width: 100%" /></el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="addForm.phone" placeholder="请输入手机号" style="width: 100%" /></el-form-item>
        <el-form-item label="身份证号" prop="idCard"><el-input v-model="addForm.idCard" placeholder="请输入身份证号" style="width: 100%" /></el-form-item>
        <el-form-item label="地址" prop="address"><el-input v-model="addForm.address" placeholder="请输入地址" style="width: 100%" /></el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="addForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="普通用户" :value="0" />
            <el-option label="管理员" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmitAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, createUser, updateUser, deleteUser } from '../../api/admin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })
const searchForm = reactive({ username: '', phone: '' })
const editDialogVisible = ref(false)
const editForm = reactive({ id: null, username: '', realName: '', phone: '', idCard: '', address: '', password: '' })
const addDialogVisible = ref(false)
const addForm = reactive({ username: '', password: '', realName: '', phone: '', idCard: '', address: '', role: 0 })
const submitting = ref(false)
const addFormRef = ref(null)
const editFormRef = ref(null)

// 手机号验证
const validatePhone = (rule, value, callback) => {
  if (!value?.trim()) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value.trim())) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

// 身份证验证（有值时才校验格式）
const validateIdCard = (rule, value, callback) => {
  if (value?.trim() && !/^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/.test(value.trim())) {
    callback(new Error('请输入正确的身份证号'))
  } else {
    callback()
  }
}

// 新增表单验证规则
const addRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度为3-50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
  idCard: [{ validator: validateIdCard, trigger: 'blur' }]
}

// 编辑表单验证规则
const editRules = {
  phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
  idCard: [{ validator: validateIdCard, trigger: 'blur' }],
  password: [{ min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { current: pagination.current, size: pagination.size }
    if (searchForm.username) params.username = searchForm.username
    if (searchForm.phone) params.phone = searchForm.phone
    const res = await getUsers(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { searchForm.username = ''; searchForm.phone = ''; handleSearch() }
const handleEdit = (row) => { Object.assign(editForm, row); editForm.password = ''; editDialogVisible.value = true }

const handleAdd = () => {
  Object.assign(addForm, { username: '', password: '', realName: '', phone: '', idCard: '', address: '', role: 0 })
  addDialogVisible.value = true
}

const handleSubmitAdd = async () => {
  const valid = await addFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    await createUser(addForm, addForm.role)
    ElMessage.success('创建成功')
    addDialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

const handleSubmitEdit = async () => {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const data = { ...editForm }
    if (!data.password) delete data.password
    delete data.username
    await updateUser(editForm.id, data)
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
$text: #2c2c2c;
$text-secondary: #6b6b6b;

.admin-users { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

.page-title-section {
  margin-bottom: 24px;
  h2 {  font-size: 24px; color: $text; margin-bottom: 4px; }
  p { font-size: 13px; color: $text-secondary; }
}

.search-card, .table-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.search-card :deep(.el-form-item) { margin-bottom: 12px; margin-right: 16px; }
.search-form-wrapper { display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 12px; }
.search-form-left { flex: 1; min-width: 300px; }
.search-form-right { flex-shrink: 0; }
@media (max-width: 900px) {
  .search-form-wrapper { flex-direction: column; align-items: flex-start; }
  .search-form-right { width: 100%; }
}
.form-value { color: $text; font-weight: 500; }
.pagination-container { margin-top: 24px; display: flex; justify-content: flex-end; }
</style>
