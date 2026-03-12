package com.navigation.route_navigator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistorySaveRequest {
    @NotNull(message = "User id cannot be null")
    private Long userId;

    @NotBlank(message = "From location cannot be empty")
    private String fromLocation;

    @NotBlank(message = "To location cannot be empty")
    private String toLocation;
}
