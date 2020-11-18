package com.mjxx.speechlibsnative.mjxx.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by topTech on 2017/10/11 0011.
 * des:
 */

public class JsonUtil {
    /**
     * Json请求封装
     *
     */
    public static String toJson(Object value) {
        if (value == null) {
            return "{}";
        }
        Gson gson = new Gson();
        return gson.toJson(value);
    }

    public static String objectToJsonDisableHtmlEscaping(Object object) {
        if (object == null) {
            return "{}";
        }
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping().create();
        return gson.toJson(object);
    }


    /**
     * jsonToObject
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(String json, Class<T> classOfT) {
        Gson gson = new Gson();
        T object;
        try {
            object = (T) gson.fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    public static <T> List<T> jsonToObjectList(String json, Class<T> classOfT){
        List<T> list = new ArrayList<T>();
        try {
            if (json != null) {
                Gson gson = new Gson();
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject;
                T object;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);
                    object = (T) gson.fromJson(jsonObject.toString(), classOfT);
                    list.add(object);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }
}
