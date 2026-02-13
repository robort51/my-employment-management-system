-- ============================================
-- 大学生智能职业规划与招聘管理系统 - 数据库初始化脚本
-- 数据库：employment_system
-- 创建日期：2026-02-12
-- ============================================

USE employment_system;

-- ----------------------------
-- 1. 统一用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名（登录账号）',
    password    VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    role        VARCHAR(20)  NOT NULL COMMENT '角色：student-学生 company-企业 admin-管理员',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-待审核 1-正常 2-禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 学生档案表
-- ----------------------------
DROP TABLE IF EXISTS student_profile;
CREATE TABLE student_profile (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '档案ID',
    user_id         BIGINT       NOT NULL COMMENT '关联用户ID',
    real_name       VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
    gender          VARCHAR(10)  DEFAULT NULL COMMENT '性别：男/女',
    phone           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    university      VARCHAR(100) DEFAULT NULL COMMENT '学校名称',
    major           VARCHAR(100) DEFAULT NULL COMMENT '专业',
    education       VARCHAR(20)  DEFAULT NULL COMMENT '学历：专科/本科/硕士/博士',
    graduation_year INT          DEFAULT NULL COMMENT '毕业年份',
    skills          VARCHAR(500) DEFAULT NULL COMMENT '技能标签（逗号分隔）',
    self_introduction TEXT       DEFAULT NULL COMMENT '自我介绍',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生档案表';

-- ----------------------------
-- 3. 企业档案表
-- ----------------------------
DROP TABLE IF EXISTS company_profile;
CREATE TABLE company_profile (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '企业档案ID',
    user_id         BIGINT       NOT NULL COMMENT '关联用户ID',
    company_name    VARCHAR(200) NOT NULL COMMENT '企业名称',
    license_url     VARCHAR(255) DEFAULT NULL COMMENT '营业执照图片URL',
    industry        VARCHAR(100) DEFAULT NULL COMMENT '所属行业',
    scale           VARCHAR(50)  DEFAULT NULL COMMENT '企业规模：50人以下/50-200/200-500/500以上',
    address         VARCHAR(300) DEFAULT NULL COMMENT '企业地址',
    contact_person  VARCHAR(50)  DEFAULT NULL COMMENT '联系人',
    contact_phone   VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    description     TEXT         DEFAULT NULL COMMENT '企业简介',
    audit_status    TINYINT      NOT NULL DEFAULT 0 COMMENT '审核状态：0-待审核 1-已通过 2-已拒绝',
    audit_remark    VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业档案表';

-- ----------------------------
-- 4. 招聘职位表
-- ----------------------------
DROP TABLE IF EXISTS job_position;
CREATE TABLE job_position (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '职位ID',
    company_id      BIGINT       NOT NULL COMMENT '关联企业档案ID',
    title           VARCHAR(200) NOT NULL COMMENT '职位名称',
    category        VARCHAR(100) DEFAULT NULL COMMENT '职位类别：技术/产品/设计/运营/市场/其他',
    salary_min      INT          DEFAULT NULL COMMENT '最低薪资（元/月）',
    salary_max      INT          DEFAULT NULL COMMENT '最高薪资（元/月）',
    city            VARCHAR(50)  DEFAULT NULL COMMENT '工作城市',
    education_req   VARCHAR(20)  DEFAULT NULL COMMENT '学历要求：不限/专科/本科/硕士/博士',
    experience_req  VARCHAR(50)  DEFAULT NULL COMMENT '经验要求：不限/应届/1-3年/3-5年/5年以上',
    description     TEXT         DEFAULT NULL COMMENT '职位描述',
    status          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-待审核 1-已上架 2-已下架 3-审核拒绝',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘职位表';

-- ----------------------------
-- 5. 简历表（一人一份）
-- ----------------------------
DROP TABLE IF EXISTS resume;
CREATE TABLE resume (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '简历ID',
    student_id  BIGINT   NOT NULL COMMENT '关联学生档案ID',
    content     TEXT     DEFAULT NULL COMMENT '简历内容（富文本/纯文本）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- ----------------------------
-- 6. 求职申请表
-- ----------------------------
DROP TABLE IF EXISTS job_application;
CREATE TABLE job_application (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    student_id  BIGINT   NOT NULL COMMENT '学生档案ID',
    job_id      BIGINT   NOT NULL COMMENT '职位ID',
    status      TINYINT  NOT NULL DEFAULT 0 COMMENT '状态：0-已投递 1-已查看 2-通过筛选 3-已拒绝 4-已邀面试 5-已录用',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '企业备注',
    apply_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投递时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id),
    KEY idx_job_id (job_id),
    UNIQUE KEY uk_student_job (student_id, job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='求职申请表';

-- ----------------------------
-- 7. 面试邀约表
-- ----------------------------
DROP TABLE IF EXISTS interview;
CREATE TABLE interview (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '面试ID',
    application_id  BIGINT       NOT NULL COMMENT '关联求职申请ID',
    interview_time  DATETIME     DEFAULT NULL COMMENT '面试时间',
    location        VARCHAR(300) DEFAULT NULL COMMENT '面试地点',
    type            VARCHAR(20)  DEFAULT NULL COMMENT '面试方式：线上/线下',
    meeting_link    VARCHAR(500) DEFAULT NULL COMMENT '线上面试链接',
    remark          VARCHAR(500) DEFAULT NULL COMMENT '面试备注/要求',
    status          TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-待确认 1-已接受 2-已拒绝 3-已完成 4-已取消',
    result          TINYINT      DEFAULT NULL COMMENT '面试结果：1-通过 2-未通过',
    feedback        TEXT         DEFAULT NULL COMMENT '面试反馈',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_application_id (application_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试邀约表';

-- ----------------------------
-- 8. AI简历润色记录表
-- ----------------------------
DROP TABLE IF EXISTS ai_resume_record;
CREATE TABLE ai_resume_record (
    id               BIGINT   NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    student_id       BIGINT   NOT NULL COMMENT '学生档案ID',
    original_content TEXT     DEFAULT NULL COMMENT '原始简历内容',
    polished_content TEXT     DEFAULT NULL COMMENT 'AI润色后的内容',
    create_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted          TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI简历润色记录表';

-- ----------------------------
-- 9. AI模拟面试记录表
-- ----------------------------
DROP TABLE IF EXISTS ai_interview_record;
CREATE TABLE ai_interview_record (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    student_id  BIGINT       NOT NULL COMMENT '学生档案ID',
    job_title   VARCHAR(200) DEFAULT NULL COMMENT '目标职位名称',
    qa_content  TEXT         DEFAULT NULL COMMENT '问答内容（JSON格式）',
    ai_feedback TEXT         DEFAULT NULL COMMENT 'AI综合评价',
    score       INT          DEFAULT NULL COMMENT 'AI评分（0-100）',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI模拟面试记录表';

-- ----------------------------
-- 10. 职业规划表
-- ----------------------------
DROP TABLE IF EXISTS career_plan;
CREATE TABLE career_plan (
    id           BIGINT   NOT NULL AUTO_INCREMENT COMMENT '规划ID',
    student_id   BIGINT   NOT NULL COMMENT '学生档案ID',
    plan_content TEXT     DEFAULT NULL COMMENT '规划内容',
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted      TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职业规划表';

-- ----------------------------
-- 11. 系统通知表
-- ----------------------------
DROP TABLE IF EXISTS notification;
CREATE TABLE notification (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    user_id     BIGINT       NOT NULL COMMENT '接收用户ID',
    title       VARCHAR(200) NOT NULL COMMENT '通知标题',
    content     VARCHAR(500) DEFAULT NULL COMMENT '通知内容',
    type        VARCHAR(50)  DEFAULT NULL COMMENT '通知类型：apply-投递 interview-面试 audit-审核 system-系统',
    is_read     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

-- ----------------------------
-- 初始数据：插入管理员账号（密码为 admin123 的 MD5 加密）
-- ----------------------------
INSERT INTO sys_user (username, password, role, status) VALUES
('admin', '0192023a7bbd73250516f069df18b500', 'admin', 1);