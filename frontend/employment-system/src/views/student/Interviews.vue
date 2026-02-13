<template>
  <el-card>
    <template #header><span>面试邀请</span></template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="companyName" label="公司" width="160" />
      <el-table-column prop="jobTitle" label="职位" width="160" />
      <el-table-column prop="interviewTime" label="面试时间" width="180" />
      <el-table-column prop="location" label="面试地点" width="160" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="result" label="面试结果" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.result" :type="row.result === 'pass' ? 'success' : 'danger'">{{ row.result === 'pass' ? '通过' : '未通过' }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button type="success" size="small" @click="handleRespond(row, 1)">接受</el-button>
            <el-button type="danger" size="small" @click="handleRespond(row, 2)">拒绝</el-button>
          </template>
          <span v-else>-</span>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" background layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="loadData" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyInterviews, acceptInterview, rejectInterview } from '../../api'

const list = ref([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)

const statusText = (s) => ({ 0: '待回复', 1: '已接受', 2: '已拒绝' }[s] || '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyInterviews({ pageNum: currentPage.value, pageSize: 10 })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleRespond = async (row, status) => {
  try {
    if (status === 1) {
      await acceptInterview(row.id)
    } else {
      await rejectInterview(row.id)
    }
    ElMessage.success(status === 1 ? '已接受' : '已拒绝')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>
