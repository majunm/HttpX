package convert;


import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class IResponseBodyConverter2 implements Converter<ResponseBody, String> {

    private static final String TAG = "IResponseBodyConverter2";

    @Override
    public String convert(ResponseBody value) throws IOException {
        String string = value.string();
        return string;
    }
}
