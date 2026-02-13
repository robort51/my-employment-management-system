<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span>职位搜索</span>
        <el-input v-model="keyword" placeholder="搜索职位/公司" style="width:300px" clearable @clear="loadData" @keyup.enter="loadData">
          <template #append><el-button icon="Search" @click="loadData" /></template>
        </el-input>
      </div>
    </template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="title" label="职位名称" width="180" />
      <el-table-column prop="companyName" label="公司名称" width="160" />
      <el-table-column prop="city" label="城市" width="100" />
      <el-table-column prop="salary" label="薪资" width="120" />
      <el-table-column prop="education" label="学历要求" width="100" />
      <el-table-column prop="description" label="职位描述" show-overflow-tooltip />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleApply(row)">投递</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="currentPage" @current-change="loadData" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getJobList, applyJob } from '../../api'

const list = ref([])
const loading = ref(false)
const keyword = ref('')
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getJobList({ pageNum: currentPage.value, pageSize: pageSize, keyword: keyword.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleApply = async (row) => {
  await ElMessageBox.confirm(`确定投递「${row.title}」吗？`, '确认投递')
  try {
    await applyJob(row.id)
    ElMessage.success('投递成功')
  } catch (e) {}
}

onMounted(loadData)
</script>
