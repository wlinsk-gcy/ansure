package com.wlinsk.ansure.basic.config.redis;

import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wlinsk
 * @Date: 2025/6/28
 */
@Slf4j
@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.application.name:}:")
    private String prefix;

    private static final long EXPIRE_TIME = 1800;

    private static final long FLUSH_TIME = 1800;


    public void pushList(String key, String value) {
        try {
            stringRedisTemplate.opsForList().rightPush(getKey(key), value);
        } catch (Exception e) {
            log.error("redis insert error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    public void pushAllList(String key, List<String> value) {
        try {
            ListOperations<String, String> stringObjectListOperations = stringRedisTemplate.opsForList();
            value.forEach(val -> stringObjectListOperations.rightPush(getKey(key), val));
        } catch (Exception e) {
            log.error("redis insert error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    public List<String> getRangeList(String key, int lastN) {
        try {
            BoundListOperations<String, String> listOps = stringRedisTemplate.boundListOps(getKey(key));
            return listOps.range(0, lastN);
        } catch (Exception e) {
            log.error("redis get error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    public String getListLastOne(String key){
        try {
            List<String> range = stringRedisTemplate.opsForList().range(getKey(key), -1, -1);
            if (!CollectionUtils.isEmpty(range)) {
                return range.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("redis get error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    public void updateListLastElement(String key, String value){
        try {
            long size = redisTemplate.opsForList().size(key);
            if (size == 0) {
                throw new BasicException(SysCode.SYSTEM_ERROE); // 队列为空
            }
            long lastIndex = size - 1;
            redisTemplate.opsForList().set(key, lastIndex, value);
        } catch (BasicException e) {
            log.error("redis update error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    /**
     * set key-val 键值对
     *
     * @param strKey
     * @param value
     */
    public void setVal(String strKey, Object value) {
        try {
            redisTemplate.opsForValue().set(getKey(strKey), value);
        } catch (Exception e) {
            log.error("redis insert error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }
    /**
     * set key-val 键值对
     *
     * @param strKey
     * @param value
     * @param time   过期时间单位秒
     */
    public void setVal(String strKey, Object value, long time) {
        if (time <= 0) {
            time = EXPIRE_TIME;
        }
        String key = getKey(strKey);
        try {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis insert error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    /**
     * set key-val 键值对
     *
     * @param strKey
     * @param value
     * @param time   过期时间单位秒
     */
    public void setValWithTTL(String strKey, Object value, long time) {
        time = time <= 0 ? EXPIRE_TIME : time;
        try {
            redisTemplate.opsForValue().set(getKey(strKey), value, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis insert error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    /**
     * 获取一个 key 下的所有键值对 ---> hgetall
     *
     * @param key
     * @return
     */
    public Map<Object, Object> getHashStringAll(String key) {
        return redisTemplate.opsForHash().entries(getKey(key));
    }

    public void flushKey(String key) {
        try {
            String flushKey = getKey(key);
            redisTemplate.expire(flushKey, FLUSH_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis flush error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    public void putHashAllValue(String hashKey, Map<Object, Object> map, long time) {
        if (time <= 0) {
            time = EXPIRE_TIME;
        }
        try {
            redisTemplate.opsForHash().putAll(getKey(hashKey), map);
            redisTemplate.expire(getKey(hashKey), time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis insert error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    public void setHashVal(String hashKey, String valKey, String val, long time) {
        if (time <= 0) {
            time = EXPIRE_TIME;
        }
        String key = getKey(hashKey);
        try {
            redisTemplate.opsForHash().put(key, valKey, val);
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis insert error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    public void setHashVal(String hashKey, String valKey, String val) {
        String key = getKey(hashKey);
        try {
            redisTemplate.opsForHash().put(key, valKey, val);
        } catch (Exception e) {
            log.error("redis insert error", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    public void setHashVal(String hashKey, String valKey, Object value) {
        String key = getKey(hashKey);
        try {
            this.redisTemplate.opsForHash().put(key, valKey, value);
        } catch (Exception e) {
            log.error("@@@@@@@@@@ --> set " + key + " value error, cause of ", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    public void setHashVal(String hashKey, String valKey, Object value, long time) {
        if (time <= 0) {
            time = EXPIRE_TIME;
        }
        String key = getKey(hashKey);
        try {
            this.redisTemplate.opsForHash().put(key, valKey, value);
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("@@@@@@@@@@ --> set " + key + " value error, cause of ", e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    /**
     * 该方法返回值可能带引号
     *
     * @param hashKey
     * @param valKey
     * @return
     */
    public Object getHashStringVal(String hashKey, String valKey) {
        String key = getKey(hashKey);
        return stringRedisTemplate.opsForHash().get(key, valKey);
    }

    public Object getHashVal(String hashKey, String valKey) {
        String key = getKey(hashKey);
        return redisTemplate.opsForHash().get(key, valKey);
    }


    public String getVal(String strKey) {
        String key = getKey(strKey);
        return (String) redisTemplate.opsForValue().get(key);
    }

    public Object getObjVal(String strKey) {
        String key = getKey(strKey);
        return redisTemplate.opsForValue().get(key);
    }


    /**
     * 获取失效时间
     *
     * @param strKey
     * @return
     */
    public Long getExpireTime(String strKey) {
        String key = getKey(strKey);
        return stringRedisTemplate.getExpire(key);
    }

    public long delHashVal(String key, String hashKey) {
        return redisTemplate.opsForHash().delete(getKey(key), hashKey);
    }

    public boolean delVal(String key) {
        return redisTemplate.delete(getKey(key));
    }

    /**
     * 获取key下所有的hash
     *
     * @param key
     */
    public List<Object> getHashValueAll(String key) {
        return redisTemplate.opsForHash().values(getKey(key));
    }


    private String getKey(String key) {
        return prefix + key;
    }
}
