package com.whl.o2o.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:商品实体类
 * @Description:
 */
@Data
@Builder
public class Product {
    private Long productId;
    private String productName;
    private String productDesc;
    private String imgAddr;//商铺缩略图地址
    private String normalPrice;
    private String promotionPrice;
    private Integer priority;
    private Integer point;//积分
    private Date createTime;
    private Date updateTime;
    private Integer enableStatus;//1可用 0下架
    private List<ProductImg> productImgList;//商品详情图片列表 一对多
    private ProductCategory productCategory;//描述该商品分类
    private Shop shop;//描述该商品所属店铺

    @Tolerate
    public Product() {
    }
}
