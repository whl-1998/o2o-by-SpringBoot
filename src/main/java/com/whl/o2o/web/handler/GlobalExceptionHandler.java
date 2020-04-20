package com.whl.o2o.web.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whl.o2o.entity.*;
import com.whl.o2o.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class GlobalExceptionHandler {
	private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * @param e 拦截到的Controller中抛出的异常
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public Map<String, Object> handle(Exception e) {
		Map<String, Object> modelMap = new HashMap<>();
		modelMap.put("success", false);
		if (e instanceof ShopOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof ShopCategoryOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof AreaOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof HeadLineOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof LocalAuthOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof AwardOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof ProductOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof ProductCategoryOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof UserShopMapOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof UserInfoOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof UserAwardMapOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof UserProductMapOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else if (e instanceof WeChatAuthOperationException) {
			modelMap.put("errMsg", e.getMessage());
		} else {
			logger.error("系统出现异常", e.getMessage());
			modelMap.put("errMsg", "未知错误，请联系工作人员进行解决");
		}
		return modelMap;
	}
}
