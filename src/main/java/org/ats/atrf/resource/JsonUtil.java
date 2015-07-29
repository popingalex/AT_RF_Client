package org.ats.atrf.resource;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultValueProcessor;
import net.sf.json.processors.DefaultValueProcessorMatcher;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.JSONUtils;

public class JsonUtil {
    private static JsonConfig config = new JsonConfig();
    static {
        config.setDefaultValueProcessorMatcher(new DefaultValueProcessorMatcher() {
            @Override
            public Object getMatch(@SuppressWarnings("rawtypes") Class target, @SuppressWarnings("rawtypes") Set set) {
                for (Object obj : set) {
                    Class<?> cls = (Class<?>) obj;
                    if (target.isArray() && cls.isArray()) {
                        if (cls.getComponentType().isAssignableFrom(target.getComponentType())) {
                            return cls;
                        }
                    } else {
                        if (cls.isAssignableFrom(target)) {
                            return cls;
                        }
                    }
                }
                return null;
            }
        });
        config.registerDefaultValueProcessor(Map.class, new DefaultValueProcessor() {
            @Override
            public Object getDefaultValue(@SuppressWarnings("rawtypes") Class type) {
                return new JSONObject();
            }
        });
        config.registerJsonValueProcessor(Date.class, new JsonValueProcessor() {
            @Override
            public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
                return (value == null) ? null : IConstants.DATE_FORMAT_JSON.format(value);
            }
            @Override
            public Object processArrayValue(Object value, JsonConfig jsonConfig) {
                return (value == null) ? null : IConstants.DATE_FORMAT_JSON.format(value);
            }
        });
        DateMorpher morpher = new DateMorpher(new String[] { IConstants.DATE_FORMAT_STRING });
        JSONUtils.getMorpherRegistry().registerMorpher(morpher);
    }

    public static <T> T getJava(String json, Class<T> T) {
        JSON jsonSource = JSONSerializer.toJSON(json);
        JsonConfig config = JsonUtil.config.copy();
        if (jsonSource.isArray()) {
            if (T.isArray()) {
                config.setRootClass(T.getComponentType());
                return T.cast(JSONArray.toArray((JSONArray) jsonSource, config));
            } else {
                if (Set.class.isAssignableFrom(T)) {
                    config.setCollectionType(Set.class);
                }
                return T.cast(JSONArray.toCollection((JSONArray) jsonSource, config));
            }
        } else if (jsonSource.isEmpty()) {
            return null;
        } else {
            config.setRootClass(T);
            return T.cast(JSONObject.toBean((JSONObject) jsonSource, config));
        }
    }

    public static String getJson(Object object, int indent) {
        return JSONSerializer.toJSON(object, config).toString(indent);
    }
    public static String getJson(Object object) {
        return getJson(object, 0);
    }
}
