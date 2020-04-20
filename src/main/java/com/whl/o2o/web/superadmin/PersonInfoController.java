package com.whl.o2o.web.superadmin;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.whl.o2o.dto.ConstantForSuperAdmin;
import com.whl.o2o.dto.UserInfoExecution;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.enums.UserInfoStateEnum;
import com.whl.o2o.service.UserInfoService;
import com.whl.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping("/superadmin")
public class PersonInfoController {
	@Autowired
	private UserInfoService userInfoService;

	@RequestMapping(value = "/listpersonInfos", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> listPersonInfos(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		UserInfoExecution pie;
		// 获取分页信息
		int pageIndex = HttpServletRequestUtil.getInt(request, ConstantForSuperAdmin.PAGE_NO);
		int pageSize = HttpServletRequestUtil.getInt(request, ConstantForSuperAdmin.PAGE_SIZE);
		if (pageIndex > 0 && pageSize > 0) {
			try {
				UserInfo userInfo = new UserInfo();
				int enableStatus = HttpServletRequestUtil.getInt(request, "enableStatus");
				if (enableStatus > -1) {
					// 若查询条件中有按照可用状态来查询，则将其作为查询条件传入
					userInfo.setEnableStatus(enableStatus);
				}
				String name = HttpServletRequestUtil.getString(request, "name");
				if (name != null) {
					// 若查询条件中有按照名字来查询，则将其作为查询条件传入，并decode
					userInfo.setUsername(URLDecoder.decode(name, "UTF-8"));
				}
				pie = userInfoService.getPersonInfoList(userInfo, pageIndex, pageSize);
			} catch (Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}
			if (pie.getPersonInfoList() != null) {
				modelMap.put(ConstantForSuperAdmin.PAGE_SIZE, pie.getPersonInfoList());
				modelMap.put(ConstantForSuperAdmin.TOTAL, pie.getCount());
				modelMap.put("success", true);
			} else {
				modelMap.put(ConstantForSuperAdmin.PAGE_SIZE, new ArrayList<UserInfo>());
				modelMap.put(ConstantForSuperAdmin.TOTAL, 0);
				modelMap.put("success", true);
			}
			return modelMap;
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "空的查询信息");
			return modelMap;
		}
	}

	/**
	 * 修改用户信息，主要是修改用户帐号可用状态
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/modifypersonInfo", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyPersonInfo(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 从前端请求中获取用户Id以及可用状态
		long userId = HttpServletRequestUtil.getLong(request, "userId");
		int enableStatus = HttpServletRequestUtil.getInt(request, "enableStatus");
		// 非空判断
		if (userId >= 0 && enableStatus >= 0) {
			try {
				UserInfo userInfo = new UserInfo();
				userInfo.setUserId(userId);
				userInfo.setEnableStatus(enableStatus);
				// 修改用户可用状态
				UserInfoExecution ae = userInfoService.modifyUserInfo(userInfo);
				if (ae.getState() == UserInfoStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", ae.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入需要修改的帐号信息");
		}
		return modelMap;
	}

}
