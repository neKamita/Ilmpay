package uz.pdp.ilmpay.dto;

import lombok.Data;

/**
 * 🔄 Reorder Item DTO
 * For when we need to shuffle things around! 🎯
 */
@Data
public class ReorderItemDTO {
    private Long id;
    private Integer displayOrder; // Changed from order to displayOrder to match frontend
}
