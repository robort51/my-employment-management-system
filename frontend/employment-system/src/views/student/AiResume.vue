<template>
  <el-card>
    <template #header><span>AI简历润色</span></template>
    <el-form label-width="100px" style="max-width:700px">
      <el-form-item label="简历内容">
        <el-input v-model="content" type="textarea" :rows="8" placeholder="粘贴你的简历内容，AI将帮你优化润色" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handlePolish">开始润色</el-button>
      </el-form-item>
    </el-form>
    <el-divider v-if="result" />
    <div v-if="result" class="result-box">
      <h4>润色结果：</h4>
      <div style="white-space:pre-wrap;line-height:1.8">{{ result }}</div>
    </div>
    <el-divider />
    <h4 style="margin-bottom:12px">历史记录</h4>
    <el-table :data="records" stripe size="small">
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column prop="originalContent" label="原始内容" show-overflow-tooltip />
      <el-table-column prop="polishedContent" label="润色结果" show-overflow-tooltip />
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { aiResumePolish, aiResumeRecords } from '../../api'

const content = ref('')
const result = ref('')
const loading = ref(false)
const records = ref([])

const handlePolish = async () => {
  if (!content.value.trim()) return ElMessage.warning('请输入简历内容')
  loading.value = true
  try {
    const res = await aiResumePolish({ content: content.value })
    result.value = res.data
    loadRecords()
  } finally {
    loading.value = false
  }
}

const loadRecords = async () => {
  try {
    const res = await aiResumeRecords({ pageNum: 1, pageSize: 10 })
    records.value = res.data.records || []
  } catch (e) {}
}

onMounted(loadRecords)
</script>

<style scoped>
.result-box {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 8px;
  padding: 16px;
}
</style>
