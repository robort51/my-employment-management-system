<template>
  <el-card>
    <template #header><span>AI职业规划</span></template>
    <el-alert title="AI将根据你的个人档案自动生成职业规划建议，请先完善个人档案。" type="info" :closable="false" style="margin-bottom:16px" />
    <el-button type="primary" :loading="loading" @click="handleGenerate">生成职业规划</el-button>
    <div v-if="result" class="result-box" style="margin-top:20px">
      <h4>职业规划建议：</h4>
      <div style="white-space:pre-wrap;line-height:1.8">{{ result }}</div>
    </div>
    <el-divider />
    <h4 style="margin-bottom:12px">历史记录</h4>
    <el-table :data="records" stripe size="small">
      <el-table-column prop="createTime" label="生成时间" width="180" />
      <el-table-column label="规划摘要">
        <template #default="{ row }">
          {{ previewText(row.planContent) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" link type="primary" @click="openDetail(row)">查看</el-button>
          <el-button size="small" link type="success" @click="copyText(row.planContent)">复制</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="职业规划详情" width="760px">
      <div style="white-space: pre-wrap; line-height: 1.8; max-height: 420px; overflow: auto">
        {{ detailText }}
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="copyText(detailText)">复制全文</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { aiCareerGenerate, aiCareerRecords } from '../../api'

const loading = ref(false)
const result = ref('')
const records = ref([])
const dialogVisible = ref(false)
const detailText = ref('')

const handleGenerate = async () => {
  loading.value = true
  try {
    const res = await aiCareerGenerate()
    result.value = res.data?.planContent || ''
    loadRecords()
  } finally {
    loading.value = false
  }
}

const loadRecords = async () => {
  try {
    const res = await aiCareerRecords({ pageNum: 1, pageSize: 10 })
    records.value = res.data.records || []
  } catch (e) {}
}

const previewText = (text) => {
  if (!text) return '无内容'
  return text.length > 40 ? `${text.slice(0, 40)}...` : text
}

const openDetail = (row) => {
  detailText.value = row?.planContent || '无内容'
  dialogVisible.value = true
}

const copyText = async (text) => {
  if (!text) {
    ElMessage.warning('没有可复制的内容')
    return
  }
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('复制成功')
  } catch (e) {
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.style.position = 'fixed'
    textarea.style.opacity = '0'
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    ElMessage.success('复制成功')
  }
}

onMounted(loadRecords)
</script>

<style scoped>
.result-box {
  background: #ecf5ff;
  border: 1px solid #d9ecff;
  border-radius: 8px;
  padding: 16px;
}
</style>
