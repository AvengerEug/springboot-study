package com.eugene.sumarry.springbootstudy.common;

import java.util.concurrent.ConcurrentHashMap;

public class AppContext extends ConcurrentHashMap<String, Object> {

    private static final ThreadLocal<AppContext> theradLocal = new ThreadLocal();

    private static AppContext appContext;

    public static void initAppContext() {
        getAppContext();
    }

    public static void clearAppContext() {
        getAppContext().clear();
    }

    public static AppContext getAppContext() {
        if (appContext == null) {
            appContext = AppContextSingleton.getInstance();
            theradLocal.set(appContext);
        }

        return appContext;
    }

    public static void setAttribute(String key, Object value) {
        getAppContext().put(key, value);
    }

    public static Object getAttribute(String key) {
        return getAppContext().get(key);
    }

    private enum AppContextSingleton {
        ;

        public static AppContext appContext = new AppContext();

        public static AppContext getInstance() {
            return appContext;
        }
    }


}
