package com.weixin.travel.weixinOps.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WXValidateRequest {
    private String signature;
    private Long timestamp;
    private Integer nonce;
    private String echostr;
    private String toUserName;
    private String fromUserName;
    private String msgType;
}
