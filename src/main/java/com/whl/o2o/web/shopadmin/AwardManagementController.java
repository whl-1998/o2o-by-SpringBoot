package com.whl.o2o.web.shopadmin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.whl.o2o.dto.AwardExecution;
import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.entity.Award;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.enums.AwardStateEnum;
import com.whl.o2o.exceptions.AwardOperationException;
import com.whl.o2o.service.AwardService;
import com.whl.o2o.util.CodeUtil;
import com.whl.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
@RequestMapping("/shopadmin")
public class AwardManagementController {
	@Autowired
	private AwardService awardService;

	/**
	 * 通过店铺id获取该店铺下的奖品列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listawardsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listAwardsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		if (pageIndex <= 0 || pageSize <= 0) {
			throw new AwardOperationException("非法的pageIndex或pageSize");
		}
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		if (currentShop == null || currentShop.getShopId() == null) {
			throw new AwardOperationException("当前店铺信息获取失败");
		}
        String awardName = HttpServletRequestUtil.getString(request, "awardName");
        Award awardCondition = compactAwardCondition4Search(currentShop.getShopId(), awardName);
        AwardExecution ae = awardService.getAwardList(awardCondition, pageIndex, pageSize);
        modelMap.put("awardList", ae.getAwardList());
        modelMap.put("count", ae.getCount());
        modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 通过商品id获取奖品信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getawardbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getAwardbyId(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		long awardId = HttpServletRequestUtil.getLong(request, "awardId");
		if (awardId <= 0) {
			throw new AwardOperationException("award信息获取失败");
		}
        Award award = awardService.getAwardById(awardId).getAward();
        modelMap.put("award", award);
        modelMap.put("success", true);
		return modelMap;
	}

    /**
     * 添加奖品
     * @param request
     * @return
     */
	@RequestMapping(value = "/addaward", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> addAward(HttpServletRequest request) throws IOException {
		Map<String, Object> modelMap = new HashMap<>();
		if (!CodeUtil.checkVerifyCode(request)) {
			throw new AwardOperationException("输入了错误的验证码");
		}
		ObjectMapper mapper = new ObjectMapper();
		String awardStr = HttpServletRequestUtil.getString(request, "awardStr");
		ImageHolder thumbnail = null;
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (multipartResolver.isMultipart(request)) {
            thumbnail = handleImage(request, thumbnail);
        }
        Award award = mapper.readValue(awardStr, Award.class);
		if (award == null) {
			throw new AwardOperationException("请输入奖品信息");
		}
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        award.setShopId(currentShop.getShopId());
        AwardExecution ae = awardService.addAward(award, thumbnail);
        if (ae.getState() != AwardStateEnum.SUCCESS.getState()) {
			throw new AwardOperationException(ae.getStateInfo());
        }
        modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/modifyaward", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyAward(HttpServletRequest request) throws IOException {
		boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
		Map<String, Object> modelMap = new HashMap<>();
		if (!statusChange && !CodeUtil.checkVerifyCode(request)) {
			throw new AwardOperationException("输入了错误的验证码");
		}
		ObjectMapper mapper = new ObjectMapper();
		ImageHolder thumbnail = null;
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (multipartResolver.isMultipart(request)) {
            thumbnail = handleImage(request, thumbnail);
        }
        String awardStr = HttpServletRequestUtil.getString(request, "awardStr");
        Award award = mapper.readValue(awardStr, Award.class);
		if (award == null) {
			throw new AwardOperationException("请输入奖品信息");
		}
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        award.setShopId(currentShop.getShopId());
        AwardExecution pe = awardService.modifyAward(award, thumbnail);
        if (pe.getState() != AwardStateEnum.SUCCESS.getState()) {
			throw new AwardOperationException(pe.getStateInfo());
		}
        modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 封装商品查询条件到award实例中
	 * @param shopId
	 * @param awardName
	 * @return
	 */
	private Award compactAwardCondition4Search(long shopId, String awardName) {
		Award awardCondition = new Award();
		awardCondition.setShopId(shopId);
		if (awardName != null) {
			awardCondition.setAwardName(awardName);
		}
		return awardCondition;
	}

	private ImageHolder handleImage(HttpServletRequest request, ImageHolder thumbnail) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		// 取出缩略图并构建ImageHolder对象
		CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
		if (thumbnailFile != null) {
			thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
		}
		return thumbnail;
	}
}
