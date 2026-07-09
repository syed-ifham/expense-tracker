package tracker.entity.page;

public record PageMeta(
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
