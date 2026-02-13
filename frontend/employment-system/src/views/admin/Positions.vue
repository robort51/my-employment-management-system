<template>
  <el-card>
    <template #header><span>职位审核</span></template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="title" label="职位名称" width="180" />
      <el-table-column prop="companyName" label="发布企业" width="160" />
      <el-table-column prop="city" label="城市" width="100" />
      <el-table-column prop="salary" label="薪资" width="120" />
      <el-table-column prop="education" label="学历要求" width="100" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="auditStatus" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="{ 0:'warning',1:'success',2:'danger' }[row.auditStatus]">
            {{ { 0:'待审核',1:'已通过',2:'未通过' }[row.auditStatus] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.auditStatus === 0" size="small" type="success" @click="handleAudit(row, 1)">通过</el-button>
          <el-button v-if="row.auditStatus === 0" size="small" type="danger" @click="handleAudit(row, 2)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" background layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="loadData" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPendingJobs, auditJob } from '../../api'

const list = ref([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPendingJobs({ pageNum: currentPage.value, pageSize: 10 })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleAudit = async (row, status) => {
  try {
    await auditJob(row.id, { auditStatus: status })
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>
