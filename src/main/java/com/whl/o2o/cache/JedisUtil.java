package com.whl.o2o.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.SafeEncoder;

import java.util.Set;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class JedisUtil {
    private final int expire = 60000;//缓存生存时间
    public Keys KEYS;//操作Key的方法
    public Strings STRINGS;//存储结构为String类型的操作
    private JedisPool jedisPool;//Redis连接池实例

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPoolWriper jedisPoolWriper) {
        this.jedisPool = jedisPoolWriper.getJedisPool();
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public void expire(String key, int seconds) {
        if (seconds <= 0) {
            return;
        }
        Jedis jedis = getJedis();
        jedis.expire(key, seconds);
        jedis.close();
    }

    public void expire(String key) {
        expire(key, expire);
    }

    // *******************************************Keys******************************************* //
    public class Keys {
        /**
         * 清空所有key
         */
        public String flushAll() {
            Jedis jedis = getJedis();
            String state = jedis.flushAll();
            jedis.close();
            return state;
        }

        public long del(String... keys) {
            Jedis jedis = getJedis();
            long count = jedis.del(keys);
            jedis.close();
            return count;
        }

        public boolean exists(String key) {
            Jedis jedis = getJedis();
            boolean exist = jedis.exists(key);
            jedis.close();
            return exist;
        }

        public Set<String> keys(String pattern) {
            Jedis jedis = getJedis();
            Set<String> set = jedis.keys(pattern);
            jedis.close();
            return set;
        }
    }

    // *******************************************Strings*******************************************//
    public class Strings {
        public String get(String key) {
            Jedis jedis = getJedis();
            String value = jedis.get(key);
            jedis.close();
            return value;
        }

        public String set(String key, String value) {
            return set(SafeEncoder.encode(key), SafeEncoder.encode(value));
        }

        public String set(String key, byte[] value) {
            return set(SafeEncoder.encode(key), value);
        }

        public String set(byte[] key, byte[] value) {
            Jedis jedis = getJedis();
            String status = jedis.set(key, value);
            jedis.close();
            return status;
        }
    }
}
