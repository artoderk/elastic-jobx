package com.dangdang.ddframe.util.compailer;

import java.io.File;

/**
 * Interface to generate Classes from source code
 *
 * @author:yjfei
 */
public interface DynamicCodeCompiler {
    Class compile(String sCode, String sName) throws Exception;

    Class compile(File file) throws Exception;
}
