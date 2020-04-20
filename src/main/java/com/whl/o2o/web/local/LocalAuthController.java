package com.whl.o2o.web.local;

import com.whl.o2o.dto.LocalAuthExecution;
import com.whl.o2o.entity.LocalAuth;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.enums.LocalAuthStateEnum;
import com.whl.o2o.exceptions.LocalAuthOperationException;
import com.whl.o2o.service.LocalAuthService;
import com.whl.o2o.util.CodeUtil;
import com.whl.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Controller
@RequestMapping(value = "/local")
public class LocalAuthController {
    @Autowired
    private LocalAuthService localAuthService;

    /**
     * 将用户信息与平台帐号绑定
     * @param request
     * @return
     */
    @RequestMapping(value = "/bindlocalauth", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> bindLocalAuth(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        if (!CodeUtil.checkVerifyCode(request)) {
            throw new LocalAuthOperationException("输入了错误的验证码");
        }
        String username = HttpServletRequestUtil.getString(request, "username");
        String password = HttpServletRequestUtil.getString(request, "password");
        UserInfo user = (UserInfo) request.getSession().getAttribute("user");
        if (username == null || password == null || user == null || user.getUserId() == null) {
            throw new LocalAuthOperationException("用户名和密码均不能为空");
        }
        // 创建LocalAuth对象并赋值
        LocalAuth localAuth = new LocalAuth();
        localAuth.setUsername(username);
        localAuth.setPassword(password);
        localAuth.setUserInfo(user);
        // 绑定帐号
        LocalAuthExecution le = localAuthService.bindLocalAuth(localAuth);
        if (le.getState() != LocalAuthStateEnum.SUCCESS.getState()) {
            throw new LocalAuthOperationException(le.getStateInfo());
        }
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 修改密码
     * @param request
     * @return
     */
    @RequestMapping(value = "/changelocalpwd", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> changeLocalPwd(HttpServletRequest request) throws LocalAuthOperationException {
        Map<String, Object> modelMap = new HashMap<>();
        if (!CodeUtil.checkVerifyCode(request)) {
            throw new LocalAuthOperationException("验证码输入错误, 请重新输入");
        }
        String username = HttpServletRequestUtil.getString(request, "username");
        String password = HttpServletRequestUtil.getString(request, "password");
        String newPassword = HttpServletRequestUtil.getString(request, "newPassword");
        UserInfo user = (UserInfo) request.getSession().getAttribute("user");
        if (user == null || user.getUserId() == null) {
            throw new LocalAuthOperationException("当前用户信息已过期, 请重新登录");
        }
        if (username == null || password == null || newPassword == null) {
            throw new LocalAuthOperationException("输入用户名与密码为空");
        }
        if (password.equals(newPassword)) {
            throw new LocalAuthOperationException("新旧密码不能相同, 请重新输入");
        }
        LocalAuth localAuth = localAuthService.getLocalAuthByUserId(user.getUserId()).getLocalAuth();// 查看原先帐号，看看与输入的帐号是否一致，不一致则认为是非法操作
        if (localAuth == null || !localAuth.getUsername().equals(username)) {
            throw new LocalAuthOperationException("输入的帐号非本次登录的帐号");
        }
        LocalAuthExecution le = localAuthService.modifyLocalAuth(user.getUserId(), username, password, newPassword, new Date());
        if (le.getState() != LocalAuthStateEnum.SUCCESS.getState()) {
            throw new LocalAuthOperationException("登录失败" + le.getStateInfo());
        }
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 登录校验
     * @param request
     * @return
     */
    @RequestMapping(value = "/logincheck", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> logincheck(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        boolean needVerify = HttpServletRequestUtil.getBoolean(request, "needVerify");
        if (needVerify && !CodeUtil.checkVerifyCode(request)) {
            throw new LocalAuthOperationException("验证码输入错误, 请重新输入");
        }
        String username = HttpServletRequestUtil.getString(request, "username");
        String password = HttpServletRequestUtil.getString(request, "password");
        if (username == null || password == null) {
            throw new LocalAuthOperationException("输入用户名与密码为空");
        }
        // 传入帐号和密码去获取平台帐号信息
        LocalAuth localAuth = localAuthService.getLocalAuthByUsernameAndPwd(username, password).getLocalAuth();
        if (localAuth == null) {// 若能取到帐号信息则登录成功
            throw new LocalAuthOperationException("用户名或密码错误");
        }
        modelMap.put("success", true);
        request.getSession().setAttribute("user", localAuth.getUserInfo());// 同时在session里设置用户信息
        return modelMap;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> logout(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        request.getSession().setAttribute("user", null);
        modelMap.put("success", true);
        return modelMap;
    }
}
