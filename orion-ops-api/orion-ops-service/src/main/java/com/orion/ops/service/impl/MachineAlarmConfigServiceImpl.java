package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.machine.MachineAlarmType;
import com.orion.ops.dao.MachineAlarmConfigDAO;
import com.orion.ops.dao.MachineAlarmGroupDAO;
import com.orion.ops.entity.domain.MachineAlarmConfigDO;
import com.orion.ops.entity.domain.MachineAlarmGroupDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.entity.request.machine.MachineAlarmConfigRequest;
import com.orion.ops.entity.vo.machine.MachineAlarmConfigVO;
import com.orion.ops.entity.vo.machine.MachineAlarmConfigWrapperVO;
import com.orion.ops.service.api.MachineAlarmConfigService;
import com.orion.ops.service.api.MachineAlarmGroupService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.service.api.MachineMonitorService;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机器报警配置服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:52
 */
@Service("machineAlarmConfigService")
public class MachineAlarmConfigServiceImpl implements MachineAlarmConfigService {

    @Resource
    private MachineAlarmConfigDAO machineAlarmConfigDAO;

    @Resource
    private MachineAlarmGroupDAO machineAlarmGroupDAO;

    @Resource
    private MachineAlarmGroupService machineAlarmGroupService;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private MachineMonitorService machineMonitorService;

    @Override
    public MachineAlarmConfigWrapperVO getAlarmConfigInfo(Long machineId) {
        MachineAlarmConfigWrapperVO wrapper = new MachineAlarmConfigWrapperVO();
        // 查询配置
        List<MachineAlarmConfigDO> config = this.selectByMachineId(machineId);
        List<MachineAlarmConfigVO> alarmConfig = Converts.toList(config, MachineAlarmConfigVO.class);
        // 查询报警组
        List<MachineAlarmGroupDO> group = machineAlarmGroupService.selectByMachineId(machineId);
        List<Long> groupIdList = group.stream()
                .map(MachineAlarmGroupDO::getGroupId)
                .collect(Collectors.toList());
        wrapper.setConfig(alarmConfig);
        wrapper.setGroupIdList(groupIdList);
        return wrapper;
    }

    @Override
    public List<MachineAlarmConfigVO> getAlarmConfig(Long machineId) {
        // 查询配置
        List<MachineAlarmConfigDO> config = this.selectByMachineId(machineId);
        return Converts.toList(config, MachineAlarmConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setAlarmConfig(MachineAlarmConfigRequest request) {
        // 查询机器信息
        Long machineId = request.getMachineId();
        MachineInfoDO machine = machineInfoService.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        // 删除配置
        Integer type = request.getType();
        LambdaQueryWrapper<MachineAlarmConfigDO> wrapper = new LambdaQueryWrapper<MachineAlarmConfigDO>()
                .eq(MachineAlarmConfigDO::getMachineId, machineId)
                .eq(MachineAlarmConfigDO::getAlarmType, type);
        machineAlarmConfigDAO.delete(wrapper);
        // 插入配置
        MachineAlarmConfigDO config = new MachineAlarmConfigDO();
        config.setMachineId(machineId);
        config.setAlarmType(type);
        config.setAlarmThreshold(request.getAlarmThreshold());
        config.setTriggerThreshold(request.getTriggerThreshold());
        config.setNotifySilence(request.getNotifySilence());
        machineAlarmConfigDAO.insert(config);
        // 同步报警配置
        MachineMonitorDO machineMonitor = machineMonitorService.selectByMachineId(machineId);
        if (machineMonitor != null) {
            machineMonitorService.syncMonitorAgent(machineId, machineMonitor.getMonitorUrl(), machineMonitor.getAccessToken());
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.NAME, machine.getMachineName());
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, machineId);
        EventParamsHolder.addParam(EventKeys.LABEL, MachineAlarmType.of(type).getLabel());
        EventParamsHolder.addParams(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setAlarmGroup(Long machineId, List<Long> groupIdList) {
        // 查询机器信息
        MachineInfoDO machine = machineInfoService.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        // 删除报警组
        machineAlarmGroupService.deleteByMachineId(machineId);
        // 插入报警组
        groupIdList.stream()
                .map(g -> {
                    MachineAlarmGroupDO group = new MachineAlarmGroupDO();
                    group.setMachineId(machineId);
                    group.setGroupId(g);
                    return group;
                }).forEach(machineAlarmGroupDAO::insert);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.NAME, machine.getMachineName());
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, machineId);
        EventParamsHolder.addParam(EventKeys.ID_LIST, groupIdList);
    }

    @Override
    public List<MachineAlarmConfigDO> selectByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineAlarmConfigDO> wrapper = new LambdaQueryWrapper<MachineAlarmConfigDO>()
                .eq(MachineAlarmConfigDO::getMachineId, machineId);
        return machineAlarmConfigDAO.selectList(wrapper);
    }

    @Override
    public Integer selectCountByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineAlarmConfigDO> wrapper = new LambdaQueryWrapper<MachineAlarmConfigDO>()
                .eq(MachineAlarmConfigDO::getMachineId, machineId);
        return machineAlarmConfigDAO.selectCount(wrapper);
    }

    @Override
    public Integer deleteByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineAlarmConfigDO> wrapper = new LambdaQueryWrapper<MachineAlarmConfigDO>()
                .eq(MachineAlarmConfigDO::getMachineId, machineId);
        return machineAlarmConfigDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<MachineAlarmConfigDO> wrapper = new LambdaQueryWrapper<MachineAlarmConfigDO>()
                .in(MachineAlarmConfigDO::getMachineId, machineIdList);
        return machineAlarmConfigDAO.delete(wrapper);
    }

}
