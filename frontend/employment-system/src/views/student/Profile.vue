<template>
  <el-card>
    <template #header><span>个人档案</span></template>
    <el-form ref="formRef" :model="form" label-width="100px" style="max-width:600px">
      <el-form-item label="真实姓名" prop="realName"><el-input v-model="form.realName" /></el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="form.gender">
          <el-radio value="男">男</el-radio>
          <el-radio value="女">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="学校" prop="school"><el-input v-model="form.school" /></el-form-item>
      <el-form-item label="专业" prop="major"><el-input v-model="form.major" /></el-form-item>
      <el-form-item label="学历" prop="education">
        <el-select v-model="form.education" placeholder="请选择">
          <el-option label="大专" value="大专" />
          <el-option label="本科" value="本科" />
          <el-option label="硕士" value="硕士" />
          <el-option label="博士" value="博士" />
        </el-select>
      </el-form-item>
      <el-form-item label="毕业年份" prop="graduationYear"><el-input v-model="form.graduationYear" /></el-form-item>
      <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
      <el-form-item label="邮箱" prop="email"><el-input v-model="form.email" /></el-form-item>
      <el-form-item label="技能特长" prop="skills"><el-input v-model="form.skills" type="textarea" :rows="3" /></el-form-item>
      <el-form-item label="求职意向" prop="intention"><el-input v-model="form.intention" type="textarea" :rows="2" /></el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSave">保存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentProfile, saveStudentProfile } from '../../api'

const formRef = ref()
const loading = ref(false)
const form = reactive({
  realName: '', gender: '', school: '', major: '', education: '',
  graduationYear: '', phone: '', email: '', skills: '', intention: ''
})

onMounted(async () => {
  try {
    const res = await getStudentProfile()
    if (res.data) Object.assign(form, res.data)
  } catch (e) {}
})

const handleSave = async () => {
  loading.value = true
  try {
    await saveStudentProfile(form)
    ElMessage.success('保存成功')
  } finally {
    loading.value = false
  }
}
</script>
