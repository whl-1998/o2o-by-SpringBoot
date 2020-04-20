package com.whl.o2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.dto.ProductExecution;
import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.ProductCategory;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.enums.ProductStateEnum;
import com.whl.o2o.exceptions.ProductOperationException;
import com.whl.o2o.exceptions.ShopAuthMapOperationException;
import com.whl.o2o.service.ProductCategoryService;
import com.whl.o2o.service.ProductService;
import com.whl.o2o.util.CodeUtil;
import com.whl.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

@Controller
@RequestMapping("/shopadmin")
public class ProductManagementController {
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductCategoryService productCategoryService;

	// 支持上传商品详情图的最大数量
	private static final int IMAGEMAXCOUNT = 6;

	/**
	 * 通过店铺id获取该店铺下的商品列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getproductlistbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getProductListByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		if (pageIndex <= 0 || pageSize <= 0) {
			throw new ProductOperationException("非法的pageIndex或pageSize");
		}
		if (currentShop == null || currentShop.getShopId() == null) {
			throw new ProductOperationException("当前店铺信息获取失败");
		}
		// 获取传入的需要检索的条件，包括是否需要从某个商品类别以及模糊查找商品名去筛选某个店铺下的商品列表
		// 筛选的条件可以进行排列组合
		long productCategoryId = HttpServletRequestUtil.getLong(request, "productCategoryId");
		String productName = HttpServletRequestUtil.getString(request, "productName");
		Product productCondition = compactProductCondition(currentShop.getShopId(), productCategoryId, productName);
		// 传入查询条件以及分页信息进行查询，返回相应商品列表以及总数
		ProductExecution pe = productService.getProductList(productCondition, pageIndex, pageSize);
		modelMap.put("productList", pe.getProductList());
		modelMap.put("count", pe.getCount());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 通过商品id获取商品信息
	 * @param productId
	 * @return
	 */
	@RequestMapping(value = "/getproductbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getProductById(@RequestParam Long productId) {
		Map<String, Object> modelMap = new HashMap<>();
		if (productId <= 0) {
			throw new ProductOperationException("获取当前商品信息失败");
		}
		Product product = productService.getProductById(productId).getProduct();
		// 获取该店铺下的商品类别列表
		List<ProductCategory> productCategoryList = productCategoryService.getProductCategoryList(product.getShop().getShopId()).getProductCategoryList();
		modelMap.put("product", product);
		modelMap.put("productCategoryList", productCategoryList);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/addproduct", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> addProduct(HttpServletRequest request) throws IOException {
		Map<String, Object> modelMap = new HashMap<>();
		if (!CodeUtil.checkVerifyCode(request)) {
			throw new ProductOperationException("输入了错误的验证码");
		}
		ObjectMapper mapper = new ObjectMapper();
		ImageHolder thumbnail = null;
		List<ImageHolder> productImgList = new ArrayList<>();
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		if (!multipartResolver.isMultipart(request)) {// 若请求中存在文件流，则取出相关的文件（包括缩略图和详情图）
			throw new ProductOperationException("上传图片不能为空");
		}
		thumbnail = handleImage(request, thumbnail, productImgList);
		String productStr = HttpServletRequestUtil.getString(request, "productStr");
		Product product = mapper.readValue(productStr, Product.class);
		if (product == null || thumbnail == null || productImgList.size() <= 0) {
			throw new ProductOperationException("请输入完整商品信息");
		}
		// 若Product信息，缩略图以及详情图列表为非空，则开始进行商品添加操作
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		product.setShop(currentShop);
		ProductExecution pe = productService.addProduct(product, thumbnail, productImgList);
		if (pe.getState() != ProductStateEnum.SUCCESS.getState()) {
			throw new ProductOperationException(pe.getStateInfo());
		}
		modelMap.put("success", true);
		return modelMap;
	}

	private ImageHolder handleImage(HttpServletRequest request, ImageHolder thumbnail, List<ImageHolder> productImgList) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		// 取出缩略图并构建ImageHolder对象
		CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
		if (thumbnailFile != null) {
			thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
		}
		// 取出详情图列表并构建List<ImageHolder>列表对象，最多支持六张图片上传
		for (int i = 0; i < IMAGEMAXCOUNT; i++) {
			CommonsMultipartFile productImgFile = (CommonsMultipartFile) multipartRequest.getFile("productImg" + i);
			if (productImgFile != null) {
				// 若取出的第i个详情图片文件流不为空，则将其加入详情图列表
				ImageHolder productImg = new ImageHolder(productImgFile.getOriginalFilename(), productImgFile.getInputStream());
				productImgList.add(productImg);
			} else {
				break;
			}
		}
		return thumbnail;
	}

	/**
	 * 商品编辑
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/modifyproduct", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyProduct(HttpServletRequest request) throws IOException {
		Map<String, Object> modelMap = new HashMap<>();
		// 商品编辑时或上下架操作的时候调用
		// 若为前者则进行验证码判断，后者则跳过验证码判断
		boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
		if (!statusChange && !CodeUtil.checkVerifyCode(request)) {
			throw new ProductOperationException("输入了错误的验证码");
		}
		ObjectMapper mapper = new ObjectMapper();
		ImageHolder thumbnail = null;
		List<ImageHolder> productImgList = new ArrayList<>();
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		// 若请求中存在文件流，则取出相关的文件（包括缩略图和详情图）
		if (multipartResolver.isMultipart(request)) {
			thumbnail = handleImage(request, thumbnail, productImgList);
		}
		String productStr = HttpServletRequestUtil.getString(request, "productStr");
		Product product = mapper.readValue(productStr, Product.class);
		if (product == null) {
			throw new ProductOperationException("请输入修改的商品信息");
		}
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		product.setShop(currentShop);
		ProductExecution pe = productService.modifyProduct(product, thumbnail, productImgList);
		if (pe.getState() != ProductStateEnum.SUCCESS.getState()) {
			throw new ProductOperationException(pe.getStateInfo());
		}
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 封装商品查询条件到Product实例中
	 * @param shopId(mandatory)
	 * @param productCategoryId(optional)
	 * @param productName(optional)
	 * @return
	 */
	private Product compactProductCondition(long shopId, long productCategoryId, String productName) {
		Product productCondition = new Product();
		Shop shop = new Shop();
		shop.setShopId(shopId);
		productCondition.setShop(shop);
		// 若有指定类别的要求则添加进去
		if (productCategoryId != -1L) {
			ProductCategory productCategory = new ProductCategory();
			productCategory.setProductCategoryId(productCategoryId);
			productCondition.setProductCategory(productCategory);
		}
		// 若有商品名模糊查询的要求则添加进去
		if (productName != null) {
			productCondition.setProductName(productName);
		}
		return productCondition;
	}
}
