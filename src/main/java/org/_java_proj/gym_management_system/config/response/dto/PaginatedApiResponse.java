package org._java_proj.gym_management_system.config.response.dto;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginatedApiResponse<T>  {
    private int success;
    private int code;
    private String message;
    private PaginationMeta meta;
    private List<T> data;
}
