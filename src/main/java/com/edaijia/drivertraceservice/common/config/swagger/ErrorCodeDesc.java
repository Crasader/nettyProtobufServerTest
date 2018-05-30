package com.edaijia.drivertraceservice.common.config.swagger;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.io.Serializable;

/**
 * 错误码
 * 这只是配合swagger生成文档所用
 *
 * 具体定义见com.zhouyutong.zapplication.api.ErrorCode
 *
 */
@ApiModel
@Getter
public class ErrorCodeDesc implements Serializable {
    @ApiModelProperty(value = "Parameter *** can not be empty.")
    private String _4001;
    @ApiModelProperty(value = "Parameter *** format is invalid.")
    private String _4002;
    @ApiModelProperty(value = "Signature verification failure.")
    private String _4003;
    @ApiModelProperty(value = "Do this without permission.")
    private String _4004;
    @ApiModelProperty(value = "Decryption failure.")
    private String _4005;
    @ApiModelProperty(value = "Illegal invocation party.")
    private String _4006;
    @ApiModelProperty(value = "File type allowed only *** to upload.")
    private String _4007;
    @ApiModelProperty(value = "File size can't exceed ***K.")
    private String _4008;


    @ApiModelProperty(value = "Server error,[***].")
    private String _5000;
    @ApiModelProperty(value = "Server error,[***].")
    private String _5001;
    @ApiModelProperty(value = "Server error,[***].")
    private String _5002;
    @ApiModelProperty(value = "Server error,[***].")
    private String _5003;
    @ApiModelProperty(value = "Server error,[***].")
    private String _5004;
    @ApiModelProperty(value = "Server error,[***].")
    private String _5005;

}
