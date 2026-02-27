<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span>职位管理</span>
        <el-button type="primary" @click="openDialog()">发布职位</el-button>
      </div>
    </template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="title" label="职位名称" width="180" />
      <el-table-column prop="city" label="城市" width="100" />
      <el-table-column label="薪资" width="150">
        <template #default="{ row }">{{ formatSalary(row) }}</template>
      </el-table-column>
      <el-table-column prop="educationReq" label="学历要求" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="{ 0:'warning',1:'success',2:'info',3:'danger' }[row.status]">
            {{ { 0:'待审核',1:'已上架',2:'已下架',3:'未通过' }[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button size="small" link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" link type="danger" @click="handleDelete(row)">下架</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" background layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="loadData" />

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑职位' : '发布职位'" width="550px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="职位名称"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="城市"><el-input v-model="form.city" /></el-form-item>
        <el-form-item label="最低薪资"><el-input-number v-model="form.salaryMin" :min="0" :controls="false" style="width:100%" /></el-form-item>
        <el-form-item label="最高薪资"><el-input-number v-model="form.salaryMax" :min="0" :controls="false" style="width:100%" /></el-form-item>
        <el-form-item label="学历要求">
          <el-select v-model="form.educationReq">
            <el-option label="不限" value="不限" />
            <el-option label="大专" value="大专" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
          </el-select>
        </el-form-item>
        <el-form-item label="职位描述"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="经验要求"><el-input v-model="form.experienceReq" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyJobs, publishJob, updateJob, offlineJob } from '../../api'

const list = ref([])
const loading = ref(false)
const saving = ref(false)
const currentPage = ref(1)
const total = ref(0)
const dialogVisible = ref(false)
const editId = ref(null)

const form = reactive({
  title: '',
  city: '',
  salaryMin: null,
  salaryMax: null,
  educationReq: '',
  description: '',
  experienceReq: ''
})

const resetForm = () => {
  Object.assign(form, {
    title: '',
    city: '',
    salaryMin: null,
    salaryMax: null,
    educationReq: '',
    description: '',
    experienceReq: ''
  })
  editId.value = null
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, {
      title: row.title || '',
      city: row.city || '',
      salaryMin: row.salaryMin ?? null,
      salaryMax: row.salaryMax ?? null,
      educationReq: row.educationReq || '',
      description: row.description || '',
      experienceReq: row.experienceReq || ''
    })
    editId.value = row.id
  } else {
    resetForm()
  }
  dialogVisible.value = true
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyJobs({ pageNum: currentPage.value, pageSize: 10 })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  if (form.salaryMin != null && form.salaryMax != null && form.salaryMin > form.salaryMax) {
    ElMessage.warning('最低薪资不能大于最高薪资')
    return
  }
  saving.value = true
  try {
    if (editId.value) {
      await updateJob(editId.value, form)
    } else {
      await publishJob(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定下架该职位吗？', '提示')
  try {
    await offlineJob(row.id)
    ElMessage.success('已下架')
    loadData()
  } catch (e) {}
}

const formatSalary = (row) => {
  if (row.salaryMin != null && row.salaryMax != null) return `${row.salaryMin}-${row.salaryMax}`
  if (row.salaryMin != null) return `${row.salaryMin}+`
  if (row.salaryMax != null) return `0-${row.salaryMax}`
  return '-'
}

onMounted(loadData)
</script>
