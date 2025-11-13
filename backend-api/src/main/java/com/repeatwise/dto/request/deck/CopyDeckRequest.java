package com.repeatwise.dto.request.deck;

import java.util.UUID;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho yêu cầu sao chép bộ thẻ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopyDeckRequest {

    /**
     * Thư mục đích (null => sao chép lên cấp gốc).
     */
    private UUID destinationFolderId;

    /**
     * Tên mới nếu muốn override (tùy chọn).
     */
    @Size(min = 1, max = 100, message = "{error.deck.name.size}")
    private String newName;

    /**
     * Có tự động thêm hậu tố (copy) khi tên bị trùng hay không.
     * Mặc định true để tuân thủ BR-DECK-COPY-04.
     */
    @Builder.Default
    private boolean appendCopySuffix = true;
}

