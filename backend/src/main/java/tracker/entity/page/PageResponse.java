package tracker.entity.page;

import java.util.List;

public record PageResponse<T>(
        List<T> transactions,
        PageMeta metadata) {
}
