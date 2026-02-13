<template>
  <el-card>
    <template #header>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span>通知中心</span>
        <el-button size="small" @click="handleMarkAll">全部已读</el-button>
      </div>
    </template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="title" label="标题" width="200">
        <template #default="{ row }">
          <el-badge is-dot :hidden="row.isRead === 1" style="margin-right:8px" />
          {{ row.title }}
        </template>
      </el-table-column>
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column label="操作" width="80">
        <template #default="{ row }">
          <el-button v-if="row.isRead === 0" size="small" link type="primary" @click="handleRead(row)">已读</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" background layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="loadData" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotifications, markRead, markAllRead } from '../../api'

const list = ref([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNotifications({ pageNum: currentPage.value, pageSize: 10 })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleRead = async (row) => {
  await markRead(row.id)
  row.isRead = 1
}

const handleMarkAll = async () => {
  await markAllRead()
  ElMessage.success('已全部标记为已读')
  loadData()
}

onMounted(loadData)
</script>
