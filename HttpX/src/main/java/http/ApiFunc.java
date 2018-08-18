package http;

import java.lang.reflect.Type;

import io.reactivex.functions.Function;

/**
 */

public class ApiFunc<T> implements Function<String, T> {
    private Type type;

    public ApiFunc(Type type) {
        this.type = type;
    }

    @Override
    public T apply(String resp) throws Exception {
//        Gson gson = HttpRequestManager.GSON;
//        try {
//            if (type.equals(String.class)) {
//                return (T) resp;
//            } else {
//                return gson.fromJson(resp, type);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//        }
        return null;
    }
}
