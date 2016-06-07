package com.dangdang.ddframe.util.compailer;

import com.dangdang.ddframe.util.compailer.groovy.GroovyCompiler;
import com.dangdang.ddframe.util.objectpool.HashObjectPool;

/**
 * 动态编译器工厂
 *
 * @author:xiong.j
 */
public class DynamicCodeCompilerFactory {

    private final static String GROOVY = "groovy";

    private final static String JAVA = "JAVA";

    private static final HashObjectPool<String, DynamicCodeCompiler> strategyClassPool = new HashObjectPool<String, DynamicCodeCompiler>();

    /**
     * 跟据类型获取动态编译器
     *
     * @param type 类型
     * @return 动态编译器
     */
    public static DynamicCodeCompiler getCompiler(String type){
        if (null == type) return null;

        DynamicCodeCompiler result = null;

        if (strategyClassPool.containsKey(type)) {
            return strategyClassPool.get(type);
        }

        result = getNewCompiler(type);

        return result;
    }

    private static synchronized DynamicCodeCompiler getNewCompiler(String type){
        if (strategyClassPool.containsKey(type)) {
            return strategyClassPool.get(type);
        }

        DynamicCodeCompiler result = null;
        if (GROOVY.equals(type)) {
            result = new GroovyCompiler();
        }
        strategyClassPool.put(type, result);

        return result;
    }

}
