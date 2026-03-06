<template>
  <el-card>
    <template #header><span>我的简历</span></template>

    <el-alert
      title="请上传清晰的简历图片（jpg/jpeg/png/webp/bmp），系统会自动识别文字供 AI 润色使用。"
      type="info"
      :closable="false"
      style="margin-bottom: 16px"
    />

    <el-upload
      :show-file-list="false"
      accept=".jpg,.jpeg,.png,.webp,.bmp"
      :http-request="handleUpload"
      :before-upload="beforeUpload"
    >
      <el-button type="primary" :loading="uploading">上传/重新上传简历图片</el-button>
    </el-upload>

    <div v-if="resume.imageUrl" style="margin-top: 20px">
      <h4 style="margin-bottom: 8px">已上传图片</h4>
      <div style="display: flex; align-items: center; gap: 12px">
        <el-tag type="info">{{ fileName }}</el-tag>
        <el-button size="small" type="primary" @click="handlePreview">预览</el-button>
      </div>
    </div>

    <div style="margin-top: 20px">
      <h4 style="margin-bottom: 8px">OCR识别文本（AI润色将基于此内容）</h4>
      <el-input v-model="resume.ocrText" type="textarea" :rows="14" readonly />
    </div>

    <el-dialog v-model="previewVisible" title="简历图片预览" width="780px">
      <el-image
        v-if="previewUrl"
        :src="previewUrl"
        fit="contain"
        style="width: 100%; max-height: 70vh; border: 1px solid #ebeef5; border-radius: 8px"
      />
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getResume, uploadResumeImage } from '../../api'

const uploading = ref(false)
const resume = reactive({
  imageUrl: '',
  ocrText: ''
})
const previewVisible = ref(false)

const previewUrl = computed(() => {
  if (!resume.imageUrl) return ''
  if (resume.imageUrl.startsWith('http://') || resume.imageUrl.startsWith('https://')) {
    return resume.imageUrl
  }
  const normalized = resume.imageUrl.startsWith('/') ? resume.imageUrl : `/${resume.imageUrl}`
  if (import.meta.env.DEV) {
    return `${window.location.protocol}//${window.location.hostname}:8080${normalized}`
  }
  return normalized
})
const fileName = computed(() => (resume.imageUrl ? resume.imageUrl.split('/').pop() || '简历图片' : ''))

const loadResume = async () => {
  try {
    const res = await getResume()
    if (res.data) {
      resume.imageUrl = res.data.imageUrl || ''
      resume.ocrText = res.data.ocrText || res.data.content || ''
    }
  } catch (e) {}
}

const beforeUpload = (file) => {
  const isImage = /^image\//.test(file.type)
  if (!isImage) {
    ElMessage.warning('只支持图片格式文件')
    return false
  }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.warning('图片大小不能超过10MB')
    return false
  }
  return true
}

const handleUpload = async ({ file }) => {
  const formData = new FormData()
  formData.append('file', file)
  uploading.value = true
  try {
    const res = await uploadResumeImage(formData)
    const data = res.data || {}
    resume.imageUrl = data.imageUrl || ''
    resume.ocrText = data.ocrText || data.content || ''
    ElMessage.success('上传并识别成功')
  } finally {
    uploading.value = false
  }
}

const handlePreview = () => {
  if (!previewUrl.value) {
    ElMessage.warning('暂无可预览图片')
    return
  }
  previewVisible.value = true
}

onMounted(loadResume)
</script>
