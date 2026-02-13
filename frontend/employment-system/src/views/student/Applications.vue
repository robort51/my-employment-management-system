<template>
  <el-card>
    <template #header><span>我的投递</span></template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="jobTitle" label="职位名称" width="180" />
      <el-table-column prop="companyName" label="公司名称" width="160" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="投递时间" width="180" />
      <el-table-column prop="remark" label="备注" show-overflow-tooltip />
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" background layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="loadData" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyApplications } from '../../api'

const list = ref([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)

const statusText = (s) => ({ 0: '待查看', 1: '已查看', 2: '通过', 3: '不合适' }[s] || '未知')
const statusType = (s) => ({ 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }[s] || 'info')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyApplications({ pageNum: currentPage.value, pageSize: 10 })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
