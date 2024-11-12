package com.easy.base.config;

import com.easy.base.service.ConfigurationService;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;

public class Configuration<T> {
    private final T object;

    /**
     *
     * @param object
     */
    private Configuration(T object) {
        this.object = object;
    }

    /**
     *
     * @param object
     * @return
     * @param <T>
     */
    public static <T> Configuration<T> of(T object) {
        return new Configuration<>(object);
    }

    /**
     *
     * @return String
     * @throws IllegalAccessException
     * @throws JSONException
     */
    private String converter() throws IllegalAccessException, JSONException {
        JSONObject jsonObject = new JSONObject();
        Field[] fields = object.getClass().getFields();
        for (Field field : fields) {
            jsonObject.put(field.getName(), field.get(object));
        }
        return jsonObject.toString();
    }

    /**
     *
     * @param configurationService
     * @throws JSONException
     * @throws IllegalAccessException
     */
    public void save(ConfigurationService configurationService) throws JSONException, IllegalAccessException {
        configurationService.saveConfiguration(object.getClass().getName(),converter());
    }

    /**
     *
     * @param configurationService
     * @return
     * @throws JSONException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public T fetch(ConfigurationService configurationService) throws JSONException, NoSuchFieldException, IllegalAccessException {
        return getData(configurationService.getByClassName(object.getClass().getName()).getConfig());
    }

    /**
     *
     * @param data
     * @return
     * @throws JSONException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private T getData(String data) throws JSONException, NoSuchFieldException, IllegalAccessException {
        JSONObject jsonObject = new JSONObject(data);
        for (Iterator it = jsonObject.keys(); it.hasNext(); ) {
            String key = (String) it.next();
            Field field = object.getClass().getDeclaredField(key);
            field.set(object,jsonObject.get(key));
        }
        return object;
    }

    /**
     *
     * @return
     */
    public int getNoOfProperties() {
        return object.getClass().getFields().length;
    }
}
