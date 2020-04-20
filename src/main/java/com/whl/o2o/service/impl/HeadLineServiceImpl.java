package com.whl.o2o.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whl.o2o.cache.JedisUtil;
import com.whl.o2o.dao.HeadLineDao;
import com.whl.o2o.dto.HeadLineExecution;
import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.entity.HeadLine;
import com.whl.o2o.enums.HeadLineStateEnum;
import com.whl.o2o.exceptions.HeadLineOperationException;
import com.whl.o2o.service.CacheService;
import com.whl.o2o.service.HeadLineService;
import com.whl.o2o.util.ImageUtil;
import com.whl.o2o.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Service
public class HeadLineServiceImpl implements HeadLineService {
    @Autowired
    private HeadLineDao headLineDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;

    private static final Logger logger = LoggerFactory.getLogger(HeadLineServiceImpl.class);

    @Override
    public HeadLineExecution getHeadLineList(HeadLine headLineCondition) {
        if (headLineCondition == null || headLineCondition.getEnableStatus() == null) {
            return new HeadLineExecution(HeadLineStateEnum.EMPTY);
        }
        String key = HEAD_LINE_LIST;
        List<HeadLine> headLineList;
        ObjectMapper mapper = new ObjectMapper();
        //如果headLineCondition不为空, 且是按照enableStatus进行条件查询, 那么设置redis缓存的key = headlinelist + enableStatus
        key = key + "_" + headLineCondition.getEnableStatus();
        if (!jedisKeys.exists(key)) {
            headLineList = headLineDao.queryHeadLine(headLineCondition);
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(headLineList);
            } catch (JsonProcessingException e) {
                logger.error("对象转换jsonString格式失败: " + e.getMessage());
                throw new HeadLineOperationException("获取头条列表失败");
            }
            jedisStrings.set(key, jsonString);
        } else {
            String jsonString = jedisStrings.get(key);
            if (jsonString == null) {
                logger.error("获取jsonString失败");
                throw new HeadLineOperationException("获取头条列表失败");
            }
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, HeadLine.class);
            try {
                headLineList = mapper.readValue(jsonString, javaType);
            } catch (IOException e) {
                logger.error("jsonString转换为javaType失败: " + e.getMessage());
                throw new HeadLineOperationException("获取头条列表失败");
            }
        }
        return new HeadLineExecution(HeadLineStateEnum.SUCCESS, headLineList);
    }

    @Override
    @Transactional
    public HeadLineExecution addHeadLine(HeadLine headLine, ImageHolder thumbnail) {
        if (headLine == null) {
            return new HeadLineExecution(HeadLineStateEnum.EMPTY);
        }
        headLine.setCreateTime(new Date());
        headLine.setUpdateTime(new Date());
        headLine.setEnableStatus(1);
        headLine.setPriority(0);
        int effectedNum = headLineDao.insertHeadLine(headLine);
        if (effectedNum <= 0) {
            logger.error("插入头条列表失败, 返回0条变更");
            throw new HeadLineOperationException("新增头条失败");
        }
        if (thumbnail != null && thumbnail.getImage() != null) {
            try {
                addThumbnail(headLine, thumbnail);
            } catch (IOException e) {
                logger.error("头条图片处理失败: " + e.getMessage());
                throw new HeadLineOperationException("添加头条失败");
            }
        }
        effectedNum = headLineDao.updateHeadLine(headLine);
        if (effectedNum <= 0) {
            logger.error("更新头条失败, 返回0条变更");
            throw new HeadLineOperationException("添加头条失败");
        }
        cacheService.removeFromCache(HEAD_LINE_LIST);
        return new HeadLineExecution(HeadLineStateEnum.SUCCESS, headLine);
    }

    @Override
    @Transactional
    public HeadLineExecution modifyHeadLine(HeadLine headLine, ImageHolder thumbnail) {
        if (headLine == null || headLine.getLineId() == null || headLine.getLineId() <= 0) {
            return new HeadLineExecution(HeadLineStateEnum.EMPTY);
        }
        headLine.setUpdateTime(new Date());
        if (thumbnail != null && thumbnail.getImage() != null) {
            HeadLine tempHeadLine = headLineDao.queryHeadLineById(headLine.getLineId());
            if (tempHeadLine.getLineImg() != null) {
                ImageUtil.deleteFileOrPath(tempHeadLine.getLineImg());
            }
            try {
                addThumbnail(headLine, thumbnail);
            } catch (IOException e) {
                logger.error("插入头条图片失败: " + e.getMessage());
                throw new HeadLineOperationException("头条修改失败");
            }
        }
        int effectedNum = headLineDao.updateHeadLine(headLine);
        if (effectedNum <= 0) {
            logger.error("新增头条图片失败");
            throw new HeadLineOperationException("头条修改失败");
        }
        cacheService.removeFromCache(HEAD_LINE_LIST);
        return new HeadLineExecution(HeadLineStateEnum.SUCCESS, headLine);
    }

    @Override
    @Transactional
    public HeadLineExecution removeHeadLine(long headLineId) {
        if (headLineId <= 0) {
            return new HeadLineExecution(HeadLineStateEnum.EMPTY);
        }
        HeadLine tempHeadLine = headLineDao.queryHeadLineById(headLineId);
        if (tempHeadLine == null || tempHeadLine.getLineImg() == null) {
            return new HeadLineExecution(HeadLineStateEnum.EMPTY);
        }
        ImageUtil.deleteFileOrPath(tempHeadLine.getLineImg());
        int effectedNum = headLineDao.deleteHeadLine(headLineId);
        if (effectedNum <= 0) {
            logger.error("移除头条失败, 返回0条变更");
            throw new HeadLineOperationException("头条删除失败");
        }
        return new HeadLineExecution(HeadLineStateEnum.SUCCESS);
    }

    @Override
    @Transactional
    public HeadLineExecution removeHeadLineList(List<Long> headLineIdList) {
        if (headLineIdList == null || headLineIdList.size() <= 0) {
            return new HeadLineExecution(HeadLineStateEnum.EMPTY);
        }
        List<HeadLine> headLineList = headLineDao.queryHeadLineByIds(headLineIdList);
        if (headLineList == null || headLineIdList.size() <= 0) {
            return new HeadLineExecution(HeadLineStateEnum.EMPTY);
        }
        for (HeadLine headLine : headLineList) {//先删除图片
            if (headLine.getLineImg() != null) {
                ImageUtil.deleteFileOrPath(headLine.getLineImg());
            }
        }
        int effectedNum = headLineDao.batchDeleteHeadLine(headLineIdList);
        if (effectedNum <= 0) {
            logger.error("批量移除头条失败, 返回0条变更");
            throw new HeadLineOperationException("头条批量删除失败");
        }
        return new HeadLineExecution(HeadLineStateEnum.SUCCESS);
    }

    private void addThumbnail(HeadLine headLine, ImageHolder thumbnail) throws IOException {
        String dest = PathUtil.getHeadLineImagePath();
        String thumbnailAddr = ImageUtil.generateNormalImg(thumbnail, dest);
        headLine.setLineImg(thumbnailAddr);
    }
}
