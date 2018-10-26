package com.weixin.travel.weixinOps.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.weixin.travel.common.utils.HttpClientUtils;
import com.weixin.travel.weixinOps.model.AccessToken;
import com.weixin.travel.weixinOps.model.WXButton;
import com.weixin.travel.weixinOps.model.WXMenu;
import com.weixin.travel.weixinOps.model.WXValidateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/weixinOps")
public class WeixinOpsController {
    @Value("${WX_TOKEN}")
    private String WX_TOKEN;

    @Value("${WX_ACCESS_TOKEN_URL}")
    private String WX_ACCESS_TOKEN_URL;

    @Value("${WX_CREATE_MENU_URL}")
    private String WX_CREATE_MENU_URL;

    @RequestMapping("validate")
    public String validate(WXValidateRequest request) {
        System.out.println(request.toString());
        return request.getEchostr();
    }

    @RequestMapping("initMenu")
    public boolean initMenu() {
        String tokenResult = HttpClientUtils.get(WX_ACCESS_TOKEN_URL);
        System.out.println("tokenResult" + tokenResult);
        AccessToken accessToken = JSON.parseObject(tokenResult, new TypeReference<AccessToken>(){});
        String menuRequestUrl = WX_CREATE_MENU_URL + "?access_token=" + accessToken.getAccess_token();

        String menu = getWXMenuStr();
        System.out.println(menu);
        String result = HttpClientUtils.post(menuRequestUrl, menu);
        System.out.println(result);
        return true;
    }

    public String getWXMenuStr() {
        WXMenu wxMenu = new WXMenu();
        WXButton wxButton1 = new WXButton();
        wxButton1.setName("旅游线路");

        WXButton wxButton11 = new WXButton();
        wxButton11.setType("click");
        wxButton11.setName("国内旅游");
        wxButton11.setKey("V0-Rz2l2kUyzziyUlo848yXuELtvdu-Qfzpmwd43jMs");
        wxButton1.setSub_button(wxButton11);

//        WXButton wxButton12 = new WXButton();
//        wxButton12.setType("click");
//        wxButton12.setName("国外旅游");
//        wxButton12.setKey("V0-Rz2l2kUyzziyUlo848yXuELtvdu-Qfzpmwd43jMs");
//        wxButton1.setSub_button(wxButton12);
        wxMenu.setButton(wxButton1);

        WXButton wxButton2 = new WXButton();
        wxButton2.setName("会员中心");

        WXButton wxButton21 = new WXButton();
        wxButton21.setType("view");
        wxButton21.setName("我的积分");
//        wxButton21.setKey("my_score");
        wxButton21.setUrl("http://www.baidu.com");
        wxButton2.setSub_button(wxButton21);

        WXButton wxButton22 = new WXButton();
        wxButton22.setType("view");
        wxButton22.setName("提现记录");
//        wxButton22.setKey("trade_record");
        wxButton22.setUrl("http://www.qq.com");
        wxButton2.setSub_button(wxButton22);
        wxMenu.setButton(wxButton2);

        return JSON.toJSONString(wxMenu);
    }
}
