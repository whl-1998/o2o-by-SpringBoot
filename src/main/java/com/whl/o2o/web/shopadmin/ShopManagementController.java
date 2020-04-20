package com.whl.o2o.web.shopadmin;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.dto.ShopExecution;
import com.whl.o2o.entity.Area;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.ShopCategory;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.enums.ShopStateEnum;
import com.whl.o2o.exceptions.ShopOperationException;
import com.whl.o2o.service.AreaService;
import com.whl.o2o.service.ShopCategoryService;
import com.whl.o2o.service.ShopService;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:店铺管理Controller
 */
@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private AreaService areaService;

    /**
     * 通过shopId获取shop以及所有的areaList
     * @param request
     * @return
     */
    @RequestMapping(value = "/getshopbyid", method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopById(HttpServletRequest request) {
        Map<String,Object> modelMap = new HashMap<>();
        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        if (shopId <= 0) {
            throw new ShopOperationException("获取店铺信息失败");
        }
        Shop shop = shopService.getByShopId(shopId).getShop();
        List<Area> areaList = areaService.getAreaList().getAreaList();
        modelMap.put("shop", shop);
        modelMap.put("areaList", areaList);//获取的所有area用于修改时的下拉表单展示
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 获取shop的区域以及类别信息返回给前端
     * @return
     */
    @RequestMapping(value = "/getshopinitinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopInitInfo() {
        Map<String,Object> modelMap = new HashMap<>();
        List<ShopCategory> shopCategoryList;
        List<Area> areaList;
        shopCategoryList = shopCategoryService.getShopCategoryList(new ShopCategory()).getShopCategoryList();//获取所有店铺类别
        areaList = areaService.getAreaList().getAreaList();
        modelMap.put("shopCategoryList", shopCategoryList);
        modelMap.put("areaList", areaList);
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 店铺注册
     * @param request
     * @return
     */
    @RequestMapping(value = "/registershop", method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> registerShop(HttpServletRequest request) throws IOException {
        Map<String,Object> modelMap = new HashMap<>();
        if (!CodeUtil.checkVerifyCode(request)) {
            throw new ShopOperationException("输入验证码有误");
        }
        //1.接受并转化相应的参数
        String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
        ObjectMapper mapper = new ObjectMapper();
        Shop shop = mapper.readValue(shopStr, Shop.class);
        //采用cmp接收图片信息
        CommonsMultipartFile shopImg;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (commonsMultipartResolver.isMultipart(request)) {//如果request中附带传入的文件流 则转换后获取
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
        } else { //这里图片上传是必须操作
            throw new ShopOperationException("上传图片不能为空");
        }
        //2.注册店铺
        if (shop == null || shopImg == null) {
            throw new ShopOperationException("请输入店铺信息");
        }
        UserInfo owner = (UserInfo) request.getSession().getAttribute("user");
        shop.setUserInfo(owner);//将注册的店铺的用户字段设置为当前请求下的用户
        ShopExecution shopExecution;
        ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(), shopImg.getInputStream());
        shopExecution = shopService.addShop(shop, imageHolder);
        if (shopExecution.getState() == ShopStateEnum.SUCCESS.getState()) { //创建成功
            modelMap.put("success", true);
            //一个用户可以创建多个店铺,获取当前用户可操作所有的shop
            List<Shop> shopList = (List<Shop>) request.getSession().getAttribute("shopList");
            if (shopList == null || shopList.size() == 0) {
                shopList = new ArrayList<>();
            }
            shopList.add(shopExecution.getShop());
            request.getSession().setAttribute("shopList", shopList);
        } else {
            throw new ShopOperationException(shopExecution.getStateInfo());
        }
        return modelMap;
    }

    /**
     * 店铺修改
     * @param request
     * @return
     */
    @RequestMapping(value = "/modifyshop", method = RequestMethod.POST)
    @ResponseBody
    private Map<String,Object> modifyshop(HttpServletRequest request) throws IOException {
        Map<String,Object> modelMap = new HashMap<>();
        if (!CodeUtil.checkVerifyCode(request)) {
            throw new ShopOperationException("输入验证码有误");
        }
        String shopStr = HttpServletRequestUtil.getString(request, "shopStr");//获取key为shopStr的value并转换为String
        ObjectMapper mapper = new ObjectMapper();
        Shop shop = mapper.readValue(shopStr, Shop.class);
        CommonsMultipartFile shopImg = null;
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (commonsMultipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
        }
        if (shop == null || shop.getShopId() == null) {
            throw new ShopOperationException("店铺信息获取失败");
        }
        ShopExecution shopExecution;
        if (shopImg == null) {
            shopExecution = shopService.modifyShop(shop, null);
        } else {
            ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(), shopImg.getInputStream());
            shopExecution = shopService.modifyShop(shop, imageHolder);
        }
        if (shopExecution.getState() != ShopStateEnum.SUCCESS.getState()) {//创建成功
            throw new ShopOperationException(shopExecution.getStateInfo());
        }
        modelMap.put("success", true);
        return modelMap;

    }


    /**
     * 根据当前登陆的用户信息，返回该用户创建的shoplist
     * @param request
     * @return
     */
    @RequestMapping(value = "/getshoplist", method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopList(HttpServletRequest request) {
        Map<String,Object> modelMap = new HashMap<>();
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("user");
        Shop shopCondition = new Shop();
        shopCondition.setUserInfo(userInfo);
        ShopExecution shopExecution = shopService.getShopList(shopCondition, 0, 100);//获取拥有者为当前用户id的所有店铺
        modelMap.put("shopList", shopExecution.getShopList());
        request.getSession().setAttribute("shopList", shopExecution.getShopList());
        modelMap.put("user", userInfo);
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 用于管理session相关的操作
     * @param request
     * @return
     */
    @RequestMapping(value = "/getshopmanagementinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> getShopManagementInfo(HttpServletRequest request) {
        Map<String,Object> modelMap = new HashMap<>();
        Long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        if (shopId <= 0) {
            Object currentShopObj = request.getSession().getAttribute("currentShop");
            if (currentShopObj == null) {
                modelMap.put("redirect", true);
                modelMap.put("url", "/o2o/shop/shoplist");
            } else {
                Shop currentShop = (Shop) currentShopObj;
                modelMap.put("redirect", false);
                modelMap.put("shopId", currentShop.getShopId());
            }
        } else {
            Shop currentShop = new Shop();
            currentShop.setShopId(shopId);
            request.getSession().setAttribute("currentShop", currentShop);
            modelMap.put("redirect", false);
        }
        return modelMap;
    }
}
