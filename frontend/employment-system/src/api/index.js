import request from '../utils/request'

// ===================== 用户相关 =====================
export const login = (data) => request.post('/user/login', data)
export const register = (data) => request.post('/user/register', data)

// ===================== 学生档案 =====================
export const getStudentProfile = () => request.get('/student/profile')
export const saveStudentProfile = (data) => request.post('/student/profile', data)

// ===================== 企业档案 =====================
export const getCompanyProfile = () => request.get('/company/profile')
export const saveCompanyProfile = (data) => request.post('/company/profile', data)

// ===================== 简历 =====================
export const getResume = () => request.get('/resume')
export const saveResume = (data) => request.post('/resume', data)

// ===================== 职位 =====================
export const getJobList = (params) => request.get('/job/search', { params })
export const getMyJobs = (params) => request.get('/job/my', { params })
export const publishJob = (data) => request.post('/job/publish', data)
export const updateJob = (id, data) => request.put(`/job/update/${id}`, data)
export const offlineJob = (id) => request.put(`/job/offline/${id}`)

// ===================== 求职申请 =====================
export const applyJob = (jobId) => request.post(`/application/apply?jobId=${jobId}`)
export const getMyApplications = (params) => request.get('/application/my', { params })
export const getReceivedApplications = (params) => request.get('/application/company', { params })
export const handleApplication = (id, data) => request.put(`/application/handle/${id}`, data)

// ===================== 面试邀请 =====================
export const sendInterview = (data) => request.post('/interview/invite', data)
export const getMyInterviews = (params) => request.get('/interview/my', { params })
export const getSentInterviews = (params) => request.get('/interview/company', { params })
export const acceptInterview = (id) => request.put(`/interview/accept/${id}`)
export const rejectInterview = (id) => request.put(`/interview/reject/${id}`)
export const fillInterviewResult = (id, data) => request.put(`/interview/result/${id}`, data)

// ===================== 通知 =====================
export const getNotifications = (params) => request.get('/notification/list', { params })
export const getUnreadCount = () => request.get('/notification/unread-count')
export const markRead = (id) => request.put(`/notification/read/${id}`)
export const markAllRead = () => request.put('/notification/read-all')

// ===================== AI功能 =====================
export const aiResumePolish = (data) => request.post('/ai/resume/polish', data)
export const aiResumeRecords = (params) => request.get('/ai/resume/records', { params })
export const aiInterviewStart = (data) => request.post('/ai/interview/start', data)
export const aiInterviewSubmit = (data) => request.post('/ai/interview/submit', data)
export const aiInterviewRecords = (params) => request.get('/ai/interview/records', { params })
export const aiInterviewDetail = (id) => request.get(`/ai/interview/${id}`)
export const aiCareerGenerate = () => request.post('/ai/career/generate')
export const aiCareerRecords = (params) => request.get('/ai/career/records', { params })

// ===================== 管理员 =====================
export const getCompanyList = (params) => request.get('/admin/company/list', { params })
export const auditCompany = (id, data) => request.put(`/admin/company/audit/${id}`, data)
export const getPendingJobs = (params) => request.get('/admin/job/list', { params })
export const auditJob = (id, data) => request.put(`/admin/job/audit/${id}`, data)
export const getUserList = (params) => request.get('/admin/user/list', { params })
export const toggleUserStatus = (id) => request.put(`/admin/user/toggle-status/${id}`)
