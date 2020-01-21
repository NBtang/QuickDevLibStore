package com.laotang.quickdev.arouter_reflect;

import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.android.arouter.facade.template.IRouteGroup;

import java.util.HashMap;
import java.util.Map;

public class ARouterReflect {
    private static volatile Map<String, Map<String, RouteMeta>> routerMap;

    public synchronized static Class<?> getDestination(String path) {
        if (path == null) {
            return null;
        }
        if (path.length() < 3) {
            return null;
        }
        if (!path.startsWith("/")) {
            return null;
        }
        String[] values = path.replaceFirst("/", "").split("/");
        if (values.length <= 1) {
            return null;
        }
        String group = values[0];
        if (routerMap != null && routerMap.containsKey(group)) {
            Map<String, RouteMeta> atlas = routerMap.get(group);
            if (atlas.containsKey(path)) {
                return atlas.get(path).getDestination();
            } else {
                return null;
            }
        }
        if (routerMap == null) {
            routerMap = new HashMap<>();
        }
        String classFullName = "com.alibaba.android.arouter.routes.ARouter$$Group$$" + group;
        Class<?> destination = null;
        try {
            Class<?> clazz = Class.forName(classFullName);
            if (IRouteGroup.class.isAssignableFrom(clazz)) {
                IRouteGroup instance = (IRouteGroup) clazz.newInstance();
                Map<String, RouteMeta> atlas = new HashMap<>();
                instance.loadInto(atlas);
                routerMap.put(group, atlas);
                if (atlas.containsKey(path)) {
                    destination = atlas.get(path).getDestination();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return destination;
    }
}
