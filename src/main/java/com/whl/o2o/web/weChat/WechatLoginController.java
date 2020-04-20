package com.whl.o2o.web.weChat;

import com.whl.o2o.dto.UserAccessToken;
import com.whl.o2o.dto.WeChatAuthExecution;
import com.whl.o2o.dto.WechatUser;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.entity.WeChatAuth;
import com.whl.o2o.enums.WeChatAuthStateEnum;
import com.whl.o2o.service.UserInfoService;
import com.whl.o2o.service.WeChatAuthService;
import com.whl.o2o.util.weixin.WechatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Controller
@RequestMapping("/wechatlogin")
public class WechatLoginController {
    private static Logger log = LoggerFactory.getLogger(WechatLoginController.class);
    private static final String FRONTEND = "1";
    private static final String SHOPEND = "2";

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private WeChatAuthService weChatAuthService;


    @RequestMapping(value = "/logincheck", method = { RequestMethod.GET })
    public String doGet(HttpServletRequest request, HttpServletResponse response) {
        log.debug("weixin login get...");
        // 获取微信公众号传输过来的code,通过code可获取access_token,进而获取用户信息
        String code = request.getParameter("code");
        String roleType = request.getParameter("state");
        log.debug("weixin login code:" + code);
        WechatUser user = null;
        String openId = null;
        WeChatAuth auth = null;
        if (null != code) {
            UserAccessToken token;
            try {
                // 通过code获取access_token
                token = WechatUtil.getUserAccessToken(code);
                log.debug("weixin login token:" + token.toString());
                // 通过token获取accessToken
                String accessToken = token.getAccessToken();
                // 通过token获取openId
                openId = token.getOpenId();
                // 通过access_token和openId获取用户昵称等信息
                user = WechatUtil.getUserInfo(accessToken, openId);
                log.debug("weixin login user:" + user.toString());
                request.getSession().setAttribute("openId", openId);
                auth = weChatAuthService.getWeChatAuthByOpenId(openId);
            } catch (IOException e) {
                log.error("error in getUserAccessToken or getUserInfo or findByOpenId: " + e.toString());
                e.printStackTrace();
            }
        }
        //若auth为空 则创建
        if (auth == null) {
            UserInfo userInfo = WechatUtil.getUserInfoFromRequest(user);
            auth = new WeChatAuth();
            auth.setOpenId(openId);
            if (FRONTEND.equals(roleType)) {
                userInfo.setUserType(1);
            } else {
                userInfo.setUserType(2);
            }
            auth.setUserInfo(userInfo);
            WeChatAuthExecution we = weChatAuthService.register(auth);
            if (we.getState() != WeChatAuthStateEnum.SUCCESS.getState()) {
                return null;
            } else {
                userInfo = userInfoService.getUserInfoById(auth.getUserInfo().getUserId()).getPersonInfo();
                request.getSession().setAttribute("user", userInfo);
            }
        }
        // 若用户点击的是前端展示系统则进入前端展示系统 否则进入店家管理系统
        if (FRONTEND.equals(roleType)) {
            return "frontend/index";
        } else {
            return "shop/shoplist";
        }
    }
}
