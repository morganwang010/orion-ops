package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Objects1;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.message.MessageType;
import com.orion.ops.constant.message.ReadStatus;
import com.orion.ops.dao.WebSideMessageDAO;
import com.orion.ops.entity.domain.WebSideMessageDO;
import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.entity.request.message.WebSideMessageRequest;
import com.orion.ops.entity.vo.message.WebSideMessagePollVO;
import com.orion.ops.entity.vo.message.WebSideMessageVO;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 站内信服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:26
 */
@Service("webSideMessageService")
public class WebSideMessageServiceImpl implements WebSideMessageService {

    @Resource
    private WebSideMessageDAO webSideMessageDAO;

    @Override
    public Integer getUnreadCount() {
        LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                .eq(WebSideMessageDO::getToUserId, Currents.getUserId())
                .eq(WebSideMessageDO::getReadStatus, ReadStatus.UNREAD.getStatus());
        return webSideMessageDAO.selectCount(wrapper);
    }

    @Override
    public Integer setAllRead() {
        WebSideMessageDO update = new WebSideMessageDO();
        update.setReadStatus(ReadStatus.READ.getStatus());
        LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                .eq(WebSideMessageDO::getToUserId, Currents.getUserId())
                .eq(WebSideMessageDO::getReadStatus, ReadStatus.UNREAD.getStatus());
        return webSideMessageDAO.update(update, wrapper);
    }

    @Override
    public Integer readMessage(List<Long> idList) {
        WebSideMessageDO update = new WebSideMessageDO();
        update.setReadStatus(ReadStatus.READ.getStatus());
        LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                .eq(WebSideMessageDO::getToUserId, Currents.getUserId())
                .in(WebSideMessageDO::getId, idList)
                .eq(WebSideMessageDO::getReadStatus, ReadStatus.UNREAD.getStatus());
        return webSideMessageDAO.update(update, wrapper);
    }

    @Override
    public Integer deleteAllRead() {
        LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                .eq(WebSideMessageDO::getToUserId, Currents.getUserId())
                .eq(WebSideMessageDO::getReadStatus, ReadStatus.READ.getStatus());
        return webSideMessageDAO.delete(wrapper);
    }

    @Override
    public Integer deleteMessage(List<Long> idList) {
        return webSideMessageDAO.deleteBatchIds(idList);
    }

    @Override
    public WebSideMessageVO getMessageDetail(Long id) {
        WebSideMessageDO message = webSideMessageDAO.selectById(id);
        Valid.notNull(message, MessageConst.UNKNOWN_DATA);
        // 设为已读
        if (ReadStatus.UNREAD.getStatus().equals(message.getReadStatus())) {
            WebSideMessageDO update = new WebSideMessageDO();
            update.setId(message.getId());
            update.setReadStatus(ReadStatus.READ.getStatus());
            webSideMessageDAO.updateById(update);
        }
        return Converts.to(message, WebSideMessageVO.class);
    }

    @Override
    public DataGrid<WebSideMessageVO> getMessageList(WebSideMessageRequest request) {
        LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                .eq(WebSideMessageDO::getToUserId, Currents.getUserId())
                .eq(Objects.nonNull(request.getClassify()), WebSideMessageDO::getMessageClassify, request.getClassify())
                .eq(Objects.nonNull(request.getType()), WebSideMessageDO::getMessageType, request.getType())
                .eq(Objects.nonNull(request.getStatus()), WebSideMessageDO::getReadStatus, request.getStatus())
                .like(Strings.isNotBlank(request.getMessage()), WebSideMessageDO::getSendMessage, request.getMessage())
                .between(Objects1.isNoneNull(request.getRangeStart(), request.getRangeEnd()), WebSideMessageDO::getCreateTime,
                        request.getRangeStart(), request.getRangeEnd())
                .orderByDesc(WebSideMessageDO::getId);
        return DataQuery.of(webSideMessageDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(WebSideMessageVO.class);
    }

    @Override
    public WebSideMessagePollVO getNewMessage(WebSideMessageRequest request) {
        WebSideMessagePollVO vo = new WebSideMessagePollVO();
        vo.setUnreadCount(this.getUnreadCount());
        // 查询列表
        LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                .eq(WebSideMessageDO::getToUserId, Currents.getUserId())
                .eq(Objects.nonNull(request.getStatus()), WebSideMessageDO::getReadStatus, request.getStatus())
                .gt(Objects.nonNull(request.getMaxId()), WebSideMessageDO::getId, request.getMaxId())
                .orderByDesc(WebSideMessageDO::getId);
        List<WebSideMessageVO> list = DataQuery.of(webSideMessageDAO)
                .wrapper(wrapper)
                .list(WebSideMessageVO.class);
        vo.setNewMessages(list);
        return vo;
    }

    @Override
    public List<WebSideMessageVO> getMoreMessage(WebSideMessageRequest request) {
        LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                .eq(WebSideMessageDO::getToUserId, Currents.getUserId())
                .eq(Objects.nonNull(request.getStatus()), WebSideMessageDO::getReadStatus, request.getStatus())
                .lt(WebSideMessageDO::getId, request.getMaxId())
                .orderByDesc(WebSideMessageDO::getId)
                .last(Const.LIMIT + Const.SPACE + request.getLimit());
        return DataQuery.of(webSideMessageDAO)
                .wrapper(wrapper)
                .list(WebSideMessageVO.class);
    }

    @Override
    public WebSideMessagePollVO pollWebSideMessage(Long maxId) {
        WebSideMessagePollVO vo = new WebSideMessagePollVO();
        Long userId = Currents.getUserId();
        // 未读数量
        vo.setUnreadCount(this.getUnreadCount());
        if (maxId == null) {
            // 没传则代表第一次访问接口 获取最大id
            LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                    .eq(WebSideMessageDO::getToUserId, userId)
                    .eq(WebSideMessageDO::getReadStatus, ReadStatus.UNREAD.getStatus())
                    .orderByDesc(WebSideMessageDO::getId)
                    .last(Const.LIMIT_1);
            WebSideMessageDO lastMessage = webSideMessageDAO.selectOne(wrapper);
            if (lastMessage == null) {
                vo.setMaxId(0L);
            } else {
                vo.setMaxId(lastMessage.getId());
            }
        } else {
            // 传了则设置新消息
            LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                    .eq(WebSideMessageDO::getToUserId, userId)
                    .eq(WebSideMessageDO::getReadStatus, ReadStatus.UNREAD.getStatus())
                    .gt(WebSideMessageDO::getId, maxId);
            List<WebSideMessageDO> newMessages = webSideMessageDAO.selectList(wrapper);
            if (newMessages.isEmpty()) {
                vo.setMaxId(maxId);
            } else {
                vo.setMaxId(Lists.last(newMessages).getId());
                vo.setNewMessages(Converts.toList(newMessages, WebSideMessageVO.class));
            }
        }
        return vo;
    }

    @Override
    public void addMessage(MessageType type, Map<String, Object> params) {
        UserDTO user = Currents.getUser();
        this.addMessage(type, user.getId(), user.getUsername(), params);
    }

    @Override
    public void addMessage(MessageType type, Long userId, String username, Map<String, Object> params) {
        WebSideMessageDO message = new WebSideMessageDO();
        message.setMessageClassify(type.getClassify().getClassify());
        message.setMessageType(type.getType());
        message.setReadStatus(ReadStatus.UNREAD.getStatus());
        message.setToUserId(userId);
        message.setToUserName(username);
        message.setSendMessage(Strings.format(type.getTemplate(), params));
        message.setParamsJson(JSON.toJSONString(params));
        message.setDeleted(Const.NOT_DELETED);
        Date now = new Date();
        message.setCreateTime(now);
        message.setUpdateTime(now);
        webSideMessageDAO.insert(message);
    }

}
