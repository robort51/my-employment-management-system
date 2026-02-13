package org.example.employmentsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.employmentsystem.entity.Notification;

public interface NotificationService {

    /** 发送通知 */
    void send(Long userId, String title, String content, String type);

    /** 查询用户的通知列表（分页，最新优先） */
    IPage<Notification> getUserNotifications(Long userId, int pageNum, int pageSize);

    /** 获取用户未读通知数量 */
    long getUnreadCount(Long userId);

    /** 标记单条通知为已读 */
    void markRead(Long id, Long userId);

    /** 全部标记为已读 */
    void markAllRead(Long userId);
}
