package com.weixin.travel.weixinOps.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class WXButton {
    private String type;
    private String name;
    private String key;
    private String url;
    private List<WXButton> sub_button;

    public WXButton() {
        this.sub_button = new ArrayList<>();
    }

    public void setSub_button(WXButton wxButton) {
        this.sub_button.add(wxButton);
    }
}
