<template>
  <el-card>
    <template #header><span>面试管理</span></template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="studentName" label="学生" width="120" />
      <el-table-column prop="jobTitle" label="职位" width="160" />
      <el-table-column prop="interviewTime" label="面试时间" width="180" />
      <el-table-column prop="location" label="地点" width="160" />
      <el-table-column prop="status" label="学生回复" width="100">
        <template #default="{ row }">
          <el-tag :type="{ 0:'warning',1:'success',2:'danger' }[row.status]">
            {{ { 0:'待回复',1:'已接受',2:'已拒绝' }[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="result" label="面试结果" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.result" :type="row.result === 'pass' ? 'success' : 'danger'">
            {{ row.result === 'pass' ? '通过' : '未通过' }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <template v-if="row.status === 1 && !row.result">
            <el-button size="small" type="success" @click="handleResult(row, 'pass')">通过</el-button>
            <el-button size="small" type="danger" @click="handleResult(row, 'fail')">未通过</el-button>
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
import { getSentInterviews, fillInterviewResult } from '../../api'

const list = ref([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSentInterviews({ pageNum: currentPage.value, pageSize: 10 })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleResult = async (row, result) => {
  try {
    await fillInterviewResult(row.id, { result })
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>
