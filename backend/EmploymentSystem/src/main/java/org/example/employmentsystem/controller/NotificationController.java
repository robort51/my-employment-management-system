package org.example.employmentsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.employmentsystem.common.Result;
import org.example.employmentsystem.entity.Notification;
import org.example.employmentsystem.service.NotificationService;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取当前用户的通知列表（分页）
     * GET /api/notification/list?pageNum=1&pageSize=10
     */
    @GetMapping("/list")
    public Result<IPage<Notification>> list(HttpServletRequest request,
                                            @RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<Notification> page = notificationService.getUserNotifications(userId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取未读通知数量
     * GET /api/notification/unread-count
     */
    @GetMapping("/unread-count")
    public Result<Long> unreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 标记单条通知为已读
     * PUT /api/notification/read/{id}
     */
    @PutMapping("/read/{id}")
    public Result<?> markRead(HttpServletRequest request,
                              @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markRead(id, userId);
        return Result.success("已标记为已读", null);
    }

    /**
     * 全部标记为已读
     * PUT /api/notification/read-all
     */
    @PutMapping("/read-all")
    public Result<?> markAllRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAllRead(userId);
        return Result.success("已全部标记为已读", null);
    }
}
