package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.dao.WebhookConfigDAO;
import com.orion.ops.entity.domain.WebhookConfigDO;
import com.orion.ops.entity.request.webhook.WebhookConfigRequest;
import com.orion.ops.entity.vo.webhook.WebhookConfigVO;
import com.orion.ops.service.api.AlarmGroupNotifyService;
import com.orion.ops.service.api.WebhookConfigService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * webhook 配置服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/23 17:55
 */
@Service("webhookConfigService")
public class WebhookConfigServiceImpl implements WebhookConfigService {

    @Resource
    private WebhookConfigDAO webhookConfigDAO;

    @Resource
    private AlarmGroupNotifyService alarmGroupNotifyService;

    @Override
    public DataGrid<WebhookConfigVO> getWebhookList(WebhookConfigRequest request) {
        LambdaQueryWrapper<WebhookConfigDO> wrapper = new LambdaQueryWrapper<WebhookConfigDO>()
                .eq(Objects.nonNull(request.getId()), WebhookConfigDO::getId, request.getId())
                .eq(Objects.nonNull(request.getType()), WebhookConfigDO::getWebhookType, request.getType())
                .like(Strings.isNotBlank(request.getUrl()), WebhookConfigDO::getWebhookUrl, request.getUrl())
                .like(Strings.isNotBlank(request.getName()), WebhookConfigDO::getWebhookName, request.getName());
        return DataQuery.of(webhookConfigDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(WebhookConfigVO.class);
    }

    @Override
    public WebhookConfigVO getWebhookDetail(Long id) {
        // 查询
        WebhookConfigDO config = webhookConfigDAO.selectById(id);
        Valid.notNull(config, MessageConst.WEBHOOK_ABSENT);
        return Converts.to(config, WebhookConfigVO.class);
    }

    @Override
    public Long addWebhook(WebhookConfigRequest request) {
        // 检查名称是否重复
        String name = request.getName();
        this.checkNamePresent(null, name);
        // 插入
        WebhookConfigDO insert = new WebhookConfigDO();
        insert.setWebhookName(name);
        insert.setWebhookUrl(request.getUrl());
        insert.setWebhookType(request.getType());
        insert.setWebhookConfig(request.getConfig());
        webhookConfigDAO.insert(insert);
        // 设置日志参数
        EventParamsHolder.addParams(insert);
        return insert.getId();
    }

    @Override
    public Integer updateWebhook(WebhookConfigRequest request) {
        Long id = request.getId();
        String name = request.getName();
        // 查询
        WebhookConfigDO config = webhookConfigDAO.selectById(id);
        Valid.notNull(config, MessageConst.WEBHOOK_ABSENT);
        // 检查名称是否重复
        this.checkNamePresent(id, name);
        // 更新
        WebhookConfigDO update = new WebhookConfigDO();
        update.setId(id);
        update.setWebhookName(name);
        update.setWebhookUrl(request.getUrl());
        update.setWebhookType(request.getType());
        update.setWebhookConfig(request.getConfig());
        int effect = webhookConfigDAO.updateById(update);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.NAME, config.getWebhookName());
        EventParamsHolder.addParams(update);
        return effect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteWebhook(Long id) {
        // 查询
        WebhookConfigDO config = webhookConfigDAO.selectById(id);
        Valid.notNull(config, MessageConst.WEBHOOK_ABSENT);
        // 删除
        int effect = webhookConfigDAO.deleteById(id);
        // 删除报警组通知方式
        effect += alarmGroupNotifyService.deleteByWebhookId(id);
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, config.getWebhookName());
        // 设置日志参数
        return effect;
    }

    /**
     * 检查是否存在
     *
     * @param id   id
     * @param name name
     */
    private void checkNamePresent(Long id, String name) {
        LambdaQueryWrapper<WebhookConfigDO> presentWrapper = new LambdaQueryWrapper<WebhookConfigDO>()
                .ne(id != null, WebhookConfigDO::getId, id)
                .eq(WebhookConfigDO::getWebhookName, name);
        boolean present = DataQuery.of(webhookConfigDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_PRESENT);
    }

}
