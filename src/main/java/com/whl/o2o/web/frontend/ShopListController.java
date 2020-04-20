package com.whl.o2o.web.frontend;

import com.whl.o2o.dto.ShopExecution;
import com.whl.o2o.entity.Area;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.ShopCategory;
import com.whl.o2o.exceptions.ShopOperationException;
import com.whl.o2o.service.AreaService;
import com.whl.o2o.service.ShopCategoryService;
import com.whl.o2o.service.ShopService;
import com.whl.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Controller
@RequestMapping("frontend")
public class ShopListController {
    @Autowired
    private AreaService areaService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private ShopService shopService;

    @RequestMapping(value = "listshopspageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listShopsPageInfo(HttpServletRequest request) {
        Map<String,Object> modelMap = new HashMap<>();
        long parentId = HttpServletRequestUtil.getLong(request, "parentId");
        List<ShopCategory> shopCategoryList = null;
        //如果parentId是存在的,则取出该根分类下的子分类列表
        if (parentId != -1) {
            ShopCategory parentCondition = ShopCategory.builder().shopCategoryId(parentId).build();
            ShopCategory shopCategoryCondition = ShopCategory.builder().parent(parentCondition).build();
            shopCategoryList = shopCategoryService.getShopCategoryList(shopCategoryCondition).getShopCategoryList();
        } else {
            shopCategoryList = shopCategoryService.getShopCategoryList(null).getShopCategoryList();
        }
        modelMap.put("shopCategoryList", shopCategoryList);
        List<Area> areaList = areaService.getAreaList().getAreaList();
        modelMap.put("areaList", areaList);
        modelMap.put("success", true);
        return modelMap;
    }

    @RequestMapping(value = "listshops",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listShops(HttpServletRequest request) {
        Map<String,Object> modelMap = new HashMap<>();
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        if (pageIndex <= 0 || pageSize <= 0) {
            throw new ShopOperationException("非法的pageIndex或pageSize");
        }
        long parentId = HttpServletRequestUtil.getLong(request, "parentId");
        long shopCategoryId = HttpServletRequestUtil.getLong(request, "shopCategory");
        int areaId = HttpServletRequestUtil.getInt(request, "areaId");
        String shopName = HttpServletRequestUtil.getString(request,"shopName");
        Shop shopCondition = compactShopCondition4Search(parentId, shopCategoryId, areaId, shopName);
        ShopExecution shopExecution = shopService.getShopList(shopCondition, pageIndex, pageSize);
        modelMap.put("shopList", shopExecution.getShopList());
        modelMap.put("count", shopExecution.getCount());
        modelMap.put("success", true);
        return modelMap;
    }

    private Shop compactShopCondition4Search(long parentId, long shopCategoryId, int areaId, String shopName) {
        Shop shopCondition = new Shop();
        if (parentId != -1L) {
            ShopCategory parentCategory = ShopCategory.builder().shopCategoryId(parentId).build();
            ShopCategory childCategory = ShopCategory.builder().parent(parentCategory).build();
            shopCondition.setShopCategory(childCategory);
        }
        if (shopCategoryId != -1L) {
            //查询某个子分类下的所以店铺列表
            ShopCategory shopCategory = ShopCategory.builder().shopCategoryId(shopCategoryId).build();
            shopCondition.setShopCategory(shopCategory);
        }
        if (areaId != -1L) {
            Area area = Area.builder().areaId(areaId).build();
            shopCondition.setArea(area);
        }
        if (shopName != null) {
            shopCondition.setShopName(shopName);
        }
        //前端展示的都是审核通过的店铺
        shopCondition.setEnableStatus(1);
        return shopCondition;
    }
}
