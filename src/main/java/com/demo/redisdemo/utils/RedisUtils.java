package com.demo.redisdemo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * 封装了对象和字符串的存,取,删除,设置过期时间操作. 所有操作可以指定数据库索引. 存,取可以设置过期时间. 没有设置默认过期时间,存值时尽量设置过期时间
 * 注意：该工具类非线程安全，需要手动给该工具类加锁，避免频繁使用
 * @author chunhui.tan
 * @version 创建时间：2018年10月8日 下午3:31:00
 */
@Component
public class RedisUtils {
    @Autowired
    @Qualifier("changeRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ValueOperations<String, Object> valueOperations;
    @Autowired
    private HashOperations<String, String, Object> hashOperations;
    @Autowired
    private ListOperations<String, Object> listOperations;
    @Autowired
    private SetOperations<String, Object> setOperations;
    @Autowired
    private ZSetOperations<String, Object> zSetOperations;
    /** 默认过期时长，单位：秒 */
    //public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**
     * 不设置过期时长
     */
    public final static long NOT_EXPIRE = -1;

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 插入值-对象,指定数据库索引,指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param dbIndex 数据库索引 范围 0-15 默认0
     * @param expire  过期时间 单位：秒
     */
    public void set(String key, Object value, Integer dbIndex, long expire) throws JsonProcessingException {
        // 选择数据库
        setDbIndex(dbIndex);
        valueOperations.set(key, toJson(value));
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    /**
     * 插入值-对象
     *
     * @param key     键
     * @param value   值
     * @param dbIndex 数据库索引 范围 0-15 默认0
     */
    public void set(String key, Object value, Integer dbIndex) throws JsonProcessingException {
        set(key, value, dbIndex, NOT_EXPIRE);
    }

    /**
     * 插入值-对象 ,默认0 index数据库
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) throws JsonProcessingException {
        set(key, value, 0, NOT_EXPIRE);
    }

    /**
     * 获取值-对象,指定数据库索引,并设置过期时间
     *
     * @param key     键
     * @param clazz   字节码对象
     * @param dbIndex 数据库索引 范围 0-15 默认0
     * @param expire  过期时间 单位：秒
     * @return
     */
    public <T> T get(String key, Class<T> clazz, Integer dbIndex, long expire) throws IOException {
        setDbIndex(dbIndex);
        String value = (String)valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    /**
     * 取值-对象 指定数据库索引
     *
     * @param key     键
     * @param clazz   字节码对象
     * @param dbIndex 数据库索引 范围 0-15 默认0
     * @return
     */
    public <T> T get(String key, Class<T> clazz, Integer dbIndex) throws IOException {
        return get(key, clazz, dbIndex, NOT_EXPIRE);
    }

    /**
     * 取值-对象
     *
     * @param key   键
     * @param clazz 字节码对象
     * @return
     */
    public <T> T get(String key, Class<T> clazz) throws IOException {
        return get(key, clazz, 0, NOT_EXPIRE);
    }

    /**
     * 获取值-字符串,指定数据库索引,设置过期时间
     *
     * @param key     键
     * @param dbIndex 数据库索引 范围 0-15 默认0
     * @param expire  过期时间 单位：秒
     * @return
     */
    public String get(String key, Integer dbIndex, long expire) {
        setDbIndex(dbIndex);
        String value = (String)valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    /**
     * 取值-字符串,指定数据库索引
     *
     * @param key     键
     * @param dbIndex 数据库索引 范围 0-15 默认0
     * @return
     */
    public String get(String key, Integer dbIndex) {
        return get(key, dbIndex, NOT_EXPIRE);
    }

    /**
     * 取值-字符串
     *
     * @param key 键
     * @return
     */
    public String get(String key) {
        return get(key, 0, NOT_EXPIRE);
    }

    /**
     * 删除 指定数据库索引
     *
     * @param key     键
     * @param dbIndex 数据库索引 范围 0-15 默认0
     */
    public Boolean delete(String key, Integer dbIndex) {
        setDbIndex(dbIndex);
        return redisTemplate.delete(key);
    }

    /**
     * 删除
     *
     * @param key 键
     */
    public Boolean delete(String key) {
        return delete(key, 0);
    }

    /**
     * 设置过期时间 ,指定数据库索引
     *
     * @param key     键
     * @param dbIndex 数据库索引 范围 0-15 默认0
     * @param expire  过期时间 单位：秒
     */
    public void expire(String key, Integer dbIndex, int expire) {
        setDbIndex(dbIndex);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    /**
     * 设置过期时间
     *
     * @param key    键
     * @param expire 过期时间 单位：秒
     */
    public void expire(String key, int expire) {
        expire(key, 0, expire);
    }

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object) throws JsonProcessingException {
        if (object instanceof Integer || object instanceof Long || object instanceof Float || object instanceof Double
                || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);

    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);

    }

    /**
     * 设置数据库索引
     *
     * @param dbIndex
     */
    private void setDbIndex(Integer dbIndex) {
        if (dbIndex == null || dbIndex > 15 || dbIndex < 0) {
            dbIndex = 0;
        }
        LettuceConnectionFactory jedisConnectionFactory = (LettuceConnectionFactory) redisTemplate
                .getConnectionFactory();
        jedisConnectionFactory.setDatabase(dbIndex);
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
    }
}
