<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <h2 style="text-align:center;margin-bottom:24px;color:#303133">大学生智能职业规划与招聘管理系统</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" prefix-icon="User" placeholder="用户名" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" prefix-icon="Lock" type="password" placeholder="密码" size="large" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item prop="role">
          <el-radio-group v-model="form.role" size="large">
            <el-radio-button value="student">我是学生</el-radio-button>
            <el-radio-button value="company">我是企业</el-radio-button>
            <el-radio-button value="admin">我是管理员</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form-item>
        <div style="text-align:center">
          <el-link type="primary" @click="router.push('/register')">没有账号？去注册</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { login } from '../api'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '', role: 'student' })
const rules = {
  role: [{ required: true, message: '请选择身份', trigger: 'change' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login({ username: form.username, password: form.password })
    const { token, ...user } = res.data
    if (user.role !== form.role) {
      ElMessage.error(`账号身份是「${user.role === 'student' ? '学生' : user.role === 'company' ? '企业' : '管理员'}」，请切换后登录`)
      return
    }
    userStore.setLogin(token, user)
    ElMessage.success('登录成功')
    router.push(`/${user.role}`)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 420px;
  padding: 20px;
  border-radius: 12px;
}
</style>
