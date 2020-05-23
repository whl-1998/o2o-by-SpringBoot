package com.whl.o2o.web.frontend;

import com.whl.o2o.dao.ProductCarDao;
import com.whl.o2o.dto.ProductCarExecution;
import com.whl.o2o.dto.ProductExecution;
import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.ProductCar;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.enums.ProductCarStateEnum;
import com.whl.o2o.exceptions.UserProductMapOperationException;
import com.whl.o2o.service.ProductCarService;
import com.whl.o2o.service.ProductService;
import com.whl.o2o.service.UserInfoService;
import com.whl.o2o.util.HttpServletRequestUtil;
import com.whl.o2o.util.PageCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Controller
@RequestMapping("/frontend")
public class ProductCarController {
    @Autowired
    private ProductCarService productCarService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private ProductService productService;


    /**
     * 列出某个顾客的购物车
     * @param request
     * @return
     */
    @RequestMapping(value = "/listproductcar", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listProductCar(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 获取分页信息
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        // 从session里获取顾客信息
        UserInfo user = (UserInfo) request.getSession().getAttribute("user");
        // 空值判断
        if (pageIndex <= 0 || pageSize <= 0) {
            throw new UserProductMapOperationException("非法的pageIndex或pageSize");
        }
        if (user == null || user.getUserId() == null) {
            throw new UserProductMapOperationException("当前用户信息获取失败");
        }
        // 根据查询条件分页返回用户消费信息
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);

        Product product = Product.builder().productName(HttpServletRequestUtil.getString(request, "productName")).build();
        ProductCar productCarCondition = ProductCar.builder().userInfo(user).product(product).build();

        ProductCarExecution pe = productCarService.selectProductCar(productCarCondition, rowIndex, pageSize);
        modelMap.put("productCarMapList", pe.getProductCarList());
        modelMap.put("count", pe.getProductCarList().size()); //Todo
        modelMap.put("success", true);
        return modelMap;
    }


    @RequestMapping(value = "/addproductcar", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> addProductCar(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        // 从session里获取顾客信息
        UserInfo user = (UserInfo) request.getSession().getAttribute("user");
        if (user == null || user.getUserId() == null) {
            throw new UserProductMapOperationException("当前用户信息获取失败");
        }
        long productId = HttpServletRequestUtil.getLong(request, "productId");
        // 空值判断
        if (productId == -1) {
            throw new UserProductMapOperationException("获取商品信息失败");
        }
        Product product;
        ProductCar productCar;
        // 根据productId获取商品信息，包含商品详情图列表
        product = productService.getProductById(productId).getProduct();
        productCar = ProductCar.builder().userInfo(user).product(product).build();
        ProductCarExecution pe = productCarService.addProductCar(productCar);
        if (pe.getState() != ProductCarStateEnum.SUCCESS.getState()) {
            throw new UserProductMapOperationException(pe.getStateInfo());
        }
        modelMap.put("success", true);
        return modelMap;
    }
}
