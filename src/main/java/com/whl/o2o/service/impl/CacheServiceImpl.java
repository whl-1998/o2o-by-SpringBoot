package com.whl.o2o.service.impl;

import com.whl.o2o.cache.JedisUtil;
import com.whl.o2o.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Service
public class CacheServiceImpl implements CacheService {
    @Autowired
    private JedisUtil.Keys jedisKeys;

    @Override
    public void removeFromCache(String keyPrefix) {
        Set<String> keySet = jedisKeys.keys(keyPrefix + "*");//取出redis缓存中以xxx开头的所有key, 并将其保存至Set集合
        for (String key : keySet) {//遍历Set, 将redis缓存中对应的键值对删除
            jedisKeys.del(key);
        }
    }
}
