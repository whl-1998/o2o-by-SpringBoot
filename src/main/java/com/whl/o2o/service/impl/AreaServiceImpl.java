package com.whl.o2o.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whl.o2o.cache.JedisUtil;
import com.whl.o2o.dao.AreaDao;
import com.whl.o2o.dto.AreaExecution;
import com.whl.o2o.entity.Area;
import com.whl.o2o.enums.AreaStateEnum;
import com.whl.o2o.exceptions.AreaOperationException;
import com.whl.o2o.service.AreaService;
import com.whl.o2o.service.CacheService;
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
public class AreaServiceImpl implements AreaService {
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;
    @Autowired
    private CacheService cacheService;

    private static Logger logger = LoggerFactory.getLogger(AreaServiceImpl.class);

    @Override
    @Transactional
    public AreaExecution getAreaList() {
        String key = AREA_LIST_KEY;
        List<Area> areaList;
        ObjectMapper mapper = new ObjectMapper();
        if (!jedisKeys.exists(key)) {//如果redis缓存中不存在key, 那么从数据库获取
            areaList = areaDao.queryArea();
            String jsonString;
            try {//将数据库获取到的List转换为json格式(String), 并存入redis缓存
                jsonString = mapper.writeValueAsString(areaList);
            } catch (JsonProcessingException e) {
                logger.error("对象转换jsonString格式失败: ", e.getMessage());
                throw new AreaOperationException(e.getMessage());
            }
            jedisStrings.set(key, jsonString);
        } else {
            String jsonString = jedisStrings.get(AREA_LIST_KEY);
            if (jsonString == null) {
                logger.error("获取jsonString失败");
                throw new AreaOperationException("获取头条列表失败");
            }
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Area.class);
            try {
                areaList = mapper.readValue(jsonString, javaType);
            } catch (IOException e) {
                logger.error("jsonString转换为javaType失败: ", e.getMessage());
                throw new AreaOperationException(e.getMessage());
            }
        }
        return new AreaExecution(AreaStateEnum.SUCCESS, areaList);
    }

    @Override
    @Transactional
    public AreaExecution addArea(Area area) {
        if (area == null || area.getAreaName() == null || "".equals(area.getAreaName())) {
            return new AreaExecution(AreaStateEnum.EMPTY);
        }
        area.setCreateTime(new Date());
        area.setUpdateTime(new Date());
        area.setPriority(0);
        int effectedNum = areaDao.insertArea(area);
        if (effectedNum <= 0) {
            logger.error("插入区域列表失败, 返回0条变更");
            throw new AreaOperationException("添加区域信息失败");
        }
        cacheService.removeFromCache(AREA_LIST_KEY);//新增操作时, 需要删除缓存
        return new AreaExecution(AreaStateEnum.SUCCESS, area);
    }

    @Override
    @Transactional
    public AreaExecution modifyArea(Area area) {
        if (area == null || area.getAreaId() == null || area.getAreaId() <= 0) {
            return new AreaExecution(AreaStateEnum.EMPTY);
        }
        area.setUpdateTime(new Date());
        int effectedNum = areaDao.updateArea(area);
        if (effectedNum <= 0) {
            logger.error("修改区域列表失败, 返回0条变更");
            throw new AreaOperationException("修改区域信息失败");
        }
        cacheService.removeFromCache(AREA_LIST_KEY);//新增操作时, 需要删除缓存
        return new AreaExecution(AreaStateEnum.SUCCESS, area);
    }
}
