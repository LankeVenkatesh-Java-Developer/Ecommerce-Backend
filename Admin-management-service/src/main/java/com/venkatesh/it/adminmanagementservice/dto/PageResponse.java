package com.venkatesh.it.adminmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;
    private PageableInfo pageable;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageableInfo {
        private int totalPages;
    }

    public static <T> PageResponse<T> of(List<T> content, int totalPages) {
        return PageResponse.<T>builder()
                .content(content)
                .pageable(PageableInfo.builder()
                        .totalPages(totalPages)
                        .build())
                .build();
    }
}
