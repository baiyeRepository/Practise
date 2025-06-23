package com.example.utils;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableScheduling
public class ValidateCodeCache {

    /**
     * 缓存验证码集合
     */
    private static List<CodeCache> codeCache = new ArrayList<>();

    /**
     * 存储code
     */
    public static void setCache(String key, String code) {
        CodeCache cache = new CodeCache();
        cache.setKey(key);
        cache.setCode(code);
        cache.setTimestamp(System.currentTimeMillis());
        codeCache.add(cache);
    }

    /**
     * 验证code
     */
    public static boolean validateCode(String key, String code) {
        return codeCache.stream().anyMatch(cache -> cache.getKey().equals(key) && cache.getCode().equals(code));
    }

    /**
     * 清理缓存
     */
    public static void removeCache(String key, String code) {
        codeCache = codeCache.stream().filter(cache -> !(cache.getKey().equalsIgnoreCase(key) && cache.getCode().equalsIgnoreCase(code)))
                .collect(Collectors.toList());
    }

    // 5 分钟扫描一次
    @Scheduled(fixedRate = 5 * 60000)
    public void scanCode() {
        System.out.println("==================验证码缓存定时器开始清理过期验证码，缓存长度：" + codeCache.size() + "==================");
        codeCache = codeCache.stream().filter(cache -> {
            long timestamp = cache.getTimestamp();
            long now = System.currentTimeMillis();
            return now - timestamp < 300000;  // 超过5分钟，清除缓存
        }).collect(Collectors.toList());
        // 设置最大值 1024 到了最大值，缓存全清空
        if (codeCache.size() >= 1024) {
            codeCache.clear();
        }
        System.out.println("========================验证码缓存清理结束，缓存长度：" + codeCache.size() + "=======================");
    }

}
