package com.whl.o2o.util;

import com.whl.o2o.dto.ImageHolder;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class ImageUtil {
    /**
     * 处理缩略图
     * @param thumbnail
     * @param targetAddr
     * @return 新生成图片的相对值路径
     */
    public static String generateThumbnail(ImageHolder thumbnail, String targetAddr) throws IOException {
        String realFileName = getRandomFileName();// 获取不重复的随机名
        String extension = getFileExtension(thumbnail.getImageName());// 获取文件的扩展名如png,jpg等
        makeDirPath(targetAddr);// 如果目标路径不存在，则自动创建
        String relativeAddr = targetAddr + realFileName + extension;// 获取文件存储的相对路径(带文件名)
        File dest = new File(PathUtil.getImgBasePath() + relativeAddr);// 获取文件要保存到的目标路径File对象
        Thumbnails.of(thumbnail.getImage()).size(200, 200).outputQuality(0.8f).toFile(dest); // 调用Thumbnails更改原图片格式, 并写入到dest
        return relativeAddr;// 返回图片相对路径地址
    }

    /**
     * 处理详情图
     * @param thumbnail
     * @param targetAddr
     * @return 新生成图片的相对值路径
     */
    public static String generateNormalImg(ImageHolder thumbnail, String targetAddr) throws IOException{
        String realFileName = getRandomFileName();// 获取不重复的随机名
        String extension = getFileExtension(thumbnail.getImageName());// 获取文件的扩展名如png,jpg等
        makeDirPath(targetAddr);// 如果目标路径不存在，则自动创建
        String relativeAddr = targetAddr + realFileName + extension;// 获取文件存储的相对路径(带文件名)
        File dest = new File(PathUtil.getImgBasePath() + relativeAddr);
        Thumbnails.of(thumbnail.getImage()).size(337, 640).outputQuality(0.9f).toFile(dest);
        return relativeAddr;
    }

    private static void makeDirPath(String targetAddr) {
        String realFileParentPath = PathUtil.getImgBasePath() + targetAddr;
        File dirPath = new File(realFileParentPath);
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }
    }

    private static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getRandomFileName() {
        int rannum = new Random().nextInt(89999) + 10000;
        String nowTimeStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return nowTimeStr + rannum;
    }

    /**
     * storePath是文件的路径还是目录的路径，
     * 如果storePath是文件路径则删除该文件，
     * 如果storePath是目录路径则删除该目录下的所有文件
     * @param storePath
     */
    public static void deleteFileOrPath(String storePath) {
        File fileOrPath = new File(PathUtil.getImgBasePath() + storePath);
        if (fileOrPath.exists()) {
            if (fileOrPath.isDirectory()) {
                File files[] = fileOrPath.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            fileOrPath.delete();
        }
    }
}
