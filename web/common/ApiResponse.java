package web.common;

public class ApiResponse<T> {
    private final String status;
    private String message;
    private T data;

    // Success with data only
    private ApiResponse(T data) {
        this.status = "success";
        this.data = data;
    }

    // Success with message and data
    private ApiResponse(String message, T data) {
        this.status = "success";
        this.message = message;
        this.data = data;
    }

    // Error with message only
    private ApiResponse(String message) {
        this.status = "error";
        this.message = message;
    }

    // ===== FACTORY METHODS =====

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message);
    }

    // Getters needed for Jackson
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}