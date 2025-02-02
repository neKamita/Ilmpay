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
    private String errorCode;
    private T data;
    private List<String> errors;
    private String funnyMessage; // Custom funny comment added for extra flavor

    // Quick constructor for success responses
    public static <T> EntityResponse<T> success(T data) {
        return new EntityResponse<>(true, "Success", null, data, null, "Keep calm and code on!");
    }

    // Quick constructor for success responses with custom message
    public static <T> EntityResponse<T> success(String message, T data) {
        return new EntityResponse<>(true, message, null, data, null, "All systems operational, with a dash of wit!");
    }

    // Quick constructor for error responses
    public static <T> EntityResponse<T> error(String message) {
        return new EntityResponse<>(false, message, "GENERAL_ERROR", null, Collections.singletonList(message), "Oops, something went wrong. Please try again later!");
    }

    // Quick constructor for error responses with error code
    public static <T> EntityResponse<T> error(String message, String errorCode) {
        return new EntityResponse<>(false, message, errorCode, null, Collections.singletonList(message), "Something went awry, but hey, it's not the end of the world!");
    }

    // Constructor for validation errors
    public static <T> EntityResponse<T> validationError(List<String> errors) {
        return new EntityResponse<>(false, "Validation failed", "VALIDATION_ERROR", null, errors, "Don't worry, it's just a minor setback!");
    }

    // New custom response that accepts both formal and funny messages
    public static <T> EntityResponse<T> customResponse(String formalMessage, String funnyMessage, T data) {
        return new EntityResponse<>(true, formalMessage, null, data, null, funnyMessage);
    }
}
