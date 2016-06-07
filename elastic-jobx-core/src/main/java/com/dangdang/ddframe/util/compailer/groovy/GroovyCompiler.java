package com.dangdang.ddframe.util.compailer.groovy;


import com.dangdang.ddframe.util.compailer.DynamicCodeCompiler;
import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author:yjfei
 */
public class GroovyCompiler implements DynamicCodeCompiler {
    private static final Logger logger = LoggerFactory.getLogger(GroovyCompiler.class);

    /**
     * Compiles Groovy code and returns the Class of the compiles code.
     *
     * @param sCode source code
     * @param sName class name
     * @return Class
     */
    public Class compile(String sCode, String sName) {
        GroovyClassLoader loader = getGroovyClassLoader();
        logger.warn("Compiling filter: " + sName);
        Class groovyClass = loader.parseClass(sCode, sName);
        return groovyClass;
    }

    /**
     * @return a new GroovyClassLoader
     */
    GroovyClassLoader getGroovyClassLoader() {
        return new GroovyClassLoader();
    }

    /**
     * Compiles groovy class from a file
     *
     * @param file file
     * @return Class
     * @throws IOException IO exception
     */
    public Class compile(File file) throws IOException {
        GroovyClassLoader loader = getGroovyClassLoader();
        Class groovyClass = loader.parseClass(file);
        return groovyClass;
    }
}
