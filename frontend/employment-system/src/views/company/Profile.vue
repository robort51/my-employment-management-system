<template>
  <el-card>
    <template #header><span>企业档案</span></template>
    <el-alert v-if="auditStatus !== null" :title="auditTip" :type="auditType" :closable="false" style="margin-bottom:16px" />
    <el-form ref="formRef" :model="form" label-width="100px" style="max-width:600px">
      <el-form-item label="企业名称" prop="companyName"><el-input v-model="form.companyName" /></el-form-item>
      <el-form-item label="行业领域" prop="industry"><el-input v-model="form.industry" /></el-form-item>
      <el-form-item label="企业规模" prop="scale">
        <el-select v-model="form.scale" placeholder="请选择">
          <el-option label="1-50人" value="1-50人" />
          <el-option label="50-200人" value="50-200人" />
          <el-option label="200-500人" value="200-500人" />
          <el-option label="500-1000人" value="500-1000人" />
          <el-option label="1000人以上" value="1000人以上" />
        </el-select>
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson"><el-input v-model="form.contactPerson" /></el-form-item>
      <el-form-item label="联系电话" prop="contactPhone"><el-input v-model="form.contactPhone" /></el-form-item>
      <el-form-item label="联系邮箱" prop="contactEmail"><el-input v-model="form.contactEmail" /></el-form-item>
      <el-form-item label="企业地址" prop="address"><el-input v-model="form.address" /></el-form-item>
      <el-form-item label="企业简介" prop="description"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSave">保存并提交审核</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCompanyProfile, saveCompanyProfile } from '../../api'

const formRef = ref()
const loading = ref(false)
const auditStatus = ref(null)

const form = reactive({
  companyName: '', industry: '', scale: '', contactPerson: '',
  contactPhone: '', contactEmail: '', address: '', description: ''
})

const auditTip = computed(() => ({ 0: '审核中，请等待管理员审核', 1: '已通过审核', 2: '审核未通过，请修改后重新提交' }[auditStatus.value] || ''))
const auditType = computed(() => ({ 0: 'warning', 1: 'success', 2: 'error' }[auditStatus.value] || 'info'))

onMounted(async () => {
  try {
    const res = await getCompanyProfile()
    if (res.data) {
      Object.assign(form, res.data)
      auditStatus.value = res.data.auditStatus
    }
  } catch (e) {}
})

const handleSave = async () => {
  loading.value = true
  try {
    await saveCompanyProfile(form)
    ElMessage.success('保存成功，等待管理员审核')
    auditStatus.value = 0
  } finally {
    loading.value = false
  }
}
</script>
