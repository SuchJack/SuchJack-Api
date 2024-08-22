package com.such.project.model.vo;

import com.such.apicommon.model.entity.InterfaceInfo;
import com.such.project.model.entity.Post;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 接口信息封装视图
 *
 * @author SuchJack
 * @TableName product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoVO extends InterfaceInfo {

    /**
     * 调用次数
     */
    private Integer totalNum;

    private static final long serialVersionUID = 1L;
}
