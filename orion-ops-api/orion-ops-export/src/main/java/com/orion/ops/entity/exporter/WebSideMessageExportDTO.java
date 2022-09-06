package com.orion.ops.entity.exporter;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.office.excel.type.ExcelFieldType;
import com.orion.ops.constant.message.MessageClassify;
import com.orion.ops.constant.message.MessageType;
import com.orion.ops.constant.message.ReadStatus;
import com.orion.ops.entity.domain.WebSideMessageDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 站内信导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/30 11:09
 */
@Data
@ApiModel(value = "站内信导出")
@ExportTitle(title = "站内信导出")
@ExportSheet(name = "站内信", height = 22, freezeHeader = true, filterHeader = true)
public class WebSideMessageExportDTO {

    @ApiModelProperty(value = "收信人")
    @ExportField(index = 0, header = "收信人", width = 15, wrapText = true)
    private String username;

    @ApiModelProperty(value = "消息分类")
    @ExportField(index = 1, header = "消息分类", width = 17)
    private String classify;

    @ApiModelProperty(value = "消息类型")
    @ExportField(index = 2, header = "消息类型", width = 22)
    private String type;

    @ApiModelProperty(value = "是否已读")
    @ExportField(index = 3, header = "是否已读", width = 10)
    private String status;

    @ApiModelProperty(value = "发送时间")
    @ExportField(index = 4, header = "发送时间", width = 20, wrapText = true, type = ExcelFieldType.DATE, format = Dates.YMD_HMS)
    private Date time;

    @ApiModelProperty(value = "消息")
    @ExportField(index = 5, header = "消息", width = 65, wrapText = true)
    private String message;

    @ApiModelProperty(value = "参数")
    @ExportField(index = 6, header = "参数", width = 20, wrapText = true)
    private String params;

    static {
        TypeStore.STORE.register(WebSideMessageDO.class, WebSideMessageExportDTO.class, p -> {
            WebSideMessageExportDTO dto = new WebSideMessageExportDTO();
            dto.setUsername(p.getToUserName());
            Optional.ofNullable(p.getMessageClassify())
                    .map(MessageClassify::of)
                    .map(MessageClassify::getLabel)
                    .ifPresent(dto::setClassify);
            Optional.ofNullable(p.getMessageType())
                    .map(MessageType::of)
                    .map(MessageType::getLabel)
                    .ifPresent(dto::setType);
            Optional.ofNullable(p.getReadStatus())
                    .map(ReadStatus::of)
                    .map(ReadStatus::getLabel)
                    .ifPresent(dto::setStatus);
            dto.setTime(p.getCreateTime());
            dto.setMessage(Utils.cleanStainTag(p.getSendMessage()));
            dto.setParams(p.getParamsJson());
            return dto;
        });
    }

}
