package com.edaijia.drivertraceservice.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author zhoutao
 * @Description: XXX数据传输对象定义
 * @date 2018/3/5
 */
@Data
@ApiModel
public class DemoDTO implements Serializable {
    @ApiModelProperty(value = "用户id", required = false)
    private Long id;

    //jsr校验 更多参考hibernate validator 或 javax.validation.constraints
    @NotBlank(message = "userName不能为空")
    @ApiModelProperty(value = "用户名称", required = true)
    private String userName;

    @ApiModelProperty(value = "标题", required = false)
    private String title;
}
