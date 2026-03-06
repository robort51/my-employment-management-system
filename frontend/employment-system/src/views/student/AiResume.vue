<template>
  <el-card>
    <template #header><span>AI简历润色</span></template>

    <el-alert
      :title="hasResumeText ? '已检测到简历识别文本，可直接开始润色。' : '未检测到简历识别文本，请先到「我的简历」上传图片。'"
      :type="hasResumeText ? 'success' : 'warning'"
      :closable="false"
      style="margin-bottom: 16px"
    />

    <el-button type="primary" :loading="loading" :disabled="!hasResumeText" @click="handlePolish">
      一键润色已上传简历
    </el-button>

    <el-divider v-if="result" />
    <div v-if="result" class="result-box">
      <h4>润色结果：</h4>
      <div style="white-space: pre-wrap; line-height: 1.8">{{ result }}</div>
    </div>

    <el-divider />
    <h4 style="margin-bottom: 12px">历史记录</h4>
    <el-table :data="records" stripe size="small">
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column label="润色结果摘要">
        <template #default="{ row }">
          {{ previewText(row.polishedContent) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" link type="primary" @click="openDetail(row)">查看</el-button>
          <el-button size="small" link type="success" @click="copyText(row.polishedContent)">复制</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="润色建议详情" width="760px">
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
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { aiResumePolishByResume, aiResumeRecords, getResume } from '../../api'

const result = ref('')
const loading = ref(false)
const records = ref([])
const resumeText = ref('')
const dialogVisible = ref(false)
const detailText = ref('')

const hasResumeText = computed(() => !!resumeText.value.trim())

const handlePolish = async () => {
  loading.value = true
  try {
    const res = await aiResumePolishByResume()
    result.value = res.data?.polishedContent || ''
    loadRecords()
  } finally {
    loading.value = false
  }
}

const loadResume = async () => {
  try {
    const res = await getResume()
    resumeText.value = res.data?.ocrText || res.data?.content || ''
  } catch (e) {
    resumeText.value = ''
  }
}

const loadRecords = async () => {
  try {
    const res = await aiResumeRecords({ pageNum: 1, pageSize: 10 })
    records.value = res.data.records || []
  } catch (e) {}
}

const previewText = (text) => {
  if (!text) return '无内容'
  return text.length > 40 ? `${text.slice(0, 40)}...` : text
}

const openDetail = (row) => {
  detailText.value = row?.polishedContent || '无内容'
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

onMounted(async () => {
  await loadResume()
  await loadRecords()
  if (!hasResumeText.value) {
    ElMessage.info('请先在「我的简历」上传简历图片')
  }
})
</script>

<style scoped>
.result-box {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 8px;
  padding: 16px;
}
</style>
