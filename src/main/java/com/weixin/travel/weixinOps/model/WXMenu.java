package com.weixin.travel.weixinOps.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_NULL)
public class WXMenu {
    private List<WXButton> button;
    public WXMenu() {
        this.button = new ArrayList<>();
    }
    public void setButton(WXButton button) {
        this.button.add(button);
    }
}
