package com.whl.o2o.web.frontend;

import com.whl.o2o.entity.HeadLine;
import com.whl.o2o.entity.ShopCategory;
import com.whl.o2o.service.HeadLineService;
import com.whl.o2o.service.ShopCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author whl
 * @version V1.0
 * @Title:主页请求
 * @Description:
 */

@Controller
@RequestMapping("/frontend")
public class MainPageController {
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private HeadLineService headLineService;

    /**
     * 初始化前端展示系统的主页信息,包括获取一级店铺类别列表以及头条列表
     * @return
     */
    @RequestMapping(value = "/listmainpageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listMainPageInfo() {
        Map<String,Object> modelMap = new HashMap<>();
        List<ShopCategory> shopCategoryList;
        //获取一级店铺类别列表(parent为空的shopCategory)
        shopCategoryList = shopCategoryService.getShopCategoryList(null).getShopCategoryList();
        modelMap.put("shopCategoryList", shopCategoryList);
        //获取状态为可用(1)的头条列表
        HeadLine headLineCondition = HeadLine.builder().enableStatus(1).build();
        List<HeadLine> headLineList = headLineService.getHeadLineList(headLineCondition).getHeadLineList();
        modelMap.put("headLineList", headLineList);
        modelMap.put("success", true);
        return modelMap;
    }
}
