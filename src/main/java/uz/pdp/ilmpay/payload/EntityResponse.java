package uz.pdp.ilmpay.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;

    public static <T> EntityResponse<T> success(T data) {
        return new EntityResponse<>(true, "Success", data, null);
    }

    public static <T> EntityResponse<T> error(String message) {
        return new EntityResponse<>(false, message, null, Collections.singletonList(message));
    }
}
