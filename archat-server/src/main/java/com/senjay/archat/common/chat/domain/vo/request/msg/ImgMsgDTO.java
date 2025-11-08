package com.senjay.archat.common.chat.domain.vo.request.msg;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Description: 图片消息入参
 */

/**
 * 默认行为（callSuper = false）是：
 * 只比较当前类的字段，不考虑父类字段。例如：
 * 在某个业务逻辑中比较两个图片消息是否“相同”，那你肯定不只是看宽高（width、height），还要看图片的 URL 或文件名
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ImgMsgDTO extends BaseFileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

//    扩展信息作为子类 父类是最基本的文件信息 文件地址（资源标识符） 和 大小
    @Schema(description = "宽度（像素）")
    @NotNull
    private Integer width;

    @Schema(description = "高度（像素）")
    @NotNull
    private Integer height;

}


