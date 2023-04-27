package com.example.mylibrary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 定义所有的module信息
 */
public class ModuleInfoTable {
    public static final String MODULE_LOGIN = "moduleLogin";
    public static final String MODULE_SHARE = "moduleShare";
    public static final String MODULE_MENU = "moduleMenu";
    public static final String MODULE_REPOSITORY = "moduleRepository";
    public static final List<String> DEFAULT_MODULE_LIST = Arrays.asList(MODULE_REPOSITORY, MODULE_LOGIN, MODULE_SHARE, MODULE_MENU);

    public static final HashMap<String, String> map = new HashMap<>();

    static {
        map.put(MODULE_REPOSITORY, "com.lc.repository.init.RepositoryPlugin");
        map.put(MODULE_LOGIN, "com.example.login.init.LoginPlugin");
        map.put(MODULE_MENU, "com.lc.menu.init.MenuPlugin");

    }

}
