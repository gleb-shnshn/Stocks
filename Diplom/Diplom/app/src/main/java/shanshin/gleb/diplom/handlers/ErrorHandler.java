package shanshin.gleb.diplom.handlers;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import shanshin.gleb.diplom.App;
import shanshin.gleb.diplom.responses.DefaultErrorResponse;
import shanshin.gleb.diplom.responses.FieldErrorResponse;

public class ErrorHandler {
    public void handleFieldError(ResponseBody errorBody) {
        Converter<ResponseBody, FieldErrorResponse> errorConverter =
                App.getInstance().getRetrofit().responseBodyConverter(FieldErrorResponse.class, new Annotation[0]);

        FieldErrorResponse errorResponse;
        try {
            errorResponse = errorConverter.convert(errorBody);
        } catch (IOException ignored) {
            return;
        }

        for (FieldErrorResponse.InvalidField invalidField : errorResponse.invalidFields) {
            App.getInstance().getUtils().showError(invalidField.message);
        }
    }

    public void handleDefaultError(ResponseBody errorBody) throws IOException {
        Converter<ResponseBody, DefaultErrorResponse> errorConverter =
                App.getInstance().getRetrofit().responseBodyConverter(DefaultErrorResponse.class, new Annotation[0]);
        DefaultErrorResponse errorResponse = errorConverter.convert(errorBody);
        App.getInstance().getUtils().showError(errorResponse.message);
    }
}
