package com.whl.o2o.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Configuration
public class PathUtil {
    private static String winPath;
    private static String linuxPath;
    private static String shopPath;
    private static String headLinePath;
    private static String shopCategoryPath;

    @Value("${win.base.path}")
    public void setWinPath(String winPath) {
        PathUtil.winPath = winPath;
    }

    @Value("${linux.base.path}")
    public void setLinuxPath(String linuxPath) {
        PathUtil.linuxPath = linuxPath;
    }

    @Value("${shop.relevant.path}")
    public void setShopPath(String shopPath) {
        PathUtil.shopPath = shopPath;
    }

    @Value("${headline.relevant.path}")
    public void setHeadLinePath(String headLinePath) {
        PathUtil.headLinePath = headLinePath;
    }

    @Value("${shopcategory.relevant.path}")
    public void setShopCategoryPath(String shopCategoryPath) {
        PathUtil.shopCategoryPath = shopCategoryPath;
    }

    public static String getImgBasePath() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return winPath;
        } else {
            return linuxPath;
        }
    }

    public static String getShopImagePath(long shopId) {
        return shopPath + shopId + "/";
    }

    public static String getHeadLineImagePath() {
        return headLinePath;
    }

    public static String getShopCategoryPath() {
        return shopCategoryPath;
    }
}
