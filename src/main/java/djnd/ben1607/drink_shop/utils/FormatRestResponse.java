package djnd.ben1607.drink_shop.utils;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import djnd.ben1607.drink_shop.domain.response.RestResponse;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            Class selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {

        // Lấy đường dẫn request
        String path = request.getURI().getPath();

        // Bỏ qua Swagger API Docs để tránh lỗi
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-resources")) {
            return body; // Trả về nguyên bản, không bọc vào RestResponse
        }

        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(status);

        // Nếu là String hoặc Resource thì không bọc trong response chung
        if (body instanceof String || body instanceof Resource) {
            return body;
        }

        if (status >= 400) {
            return body; // Trả về lỗi gốc mà không bọc lại
        } else {
            res.setData(body);
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            res.setMessage(message != null ? message.value() : "Call API success!");
        }
        return res;
    }

}
