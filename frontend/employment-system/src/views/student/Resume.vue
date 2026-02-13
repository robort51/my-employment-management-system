<template>
  <el-card>
    <template #header><span>我的简历</span></template>
    <el-form ref="formRef" :model="form" label-width="100px" style="max-width:700px">
      <el-form-item label="简历名称" prop="title"><el-input v-model="form.title" placeholder="如: 张三-Java开发-简历" /></el-form-item>
      <el-form-item label="简历内容" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="15" placeholder="请输入简历内容（教育经历、项目经验、技能特长等）" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSave">保存简历</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getResume, saveResume } from '../../api'

const formRef = ref()
const loading = ref(false)
const form = reactive({ title: '', content: '' })

onMounted(async () => {
  try {
    const res = await getResume()
    if (res.data) Object.assign(form, res.data)
  } catch (e) {}
})

const handleSave = async () => {
  loading.value = true
  try {
    await saveResume(form)
    ElMessage.success('保存成功')
  } finally {
    loading.value = false
  }
}
</script>
