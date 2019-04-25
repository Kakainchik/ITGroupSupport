package kz.itgroup.itgroupsupport;

import com.google.gson.Gson;

public class JsonHelper {

    public static <T> String convertToJson(T object)
            throws IllegalArgumentException {

        if(object == null) throw new IllegalArgumentException("Parameter 'object' is null");

        Gson gson = new Gson();
        String json = gson.toJson(object);

        return json;
    }

    public static <T> T convertToObject(String jsonText, Class<T> type) {

        Gson gson = new Gson();
        T object = gson.fromJson(jsonText, type);

        return object;
    }
}
