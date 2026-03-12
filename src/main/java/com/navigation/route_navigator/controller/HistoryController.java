package com.navigation.route_navigator.controller;


import com.navigation.route_navigator.dto.ApiResponse;
import com.navigation.route_navigator.dto.HistorySaveRequest;
import com.navigation.route_navigator.entities.SearchHistoryEntity;
import com.navigation.route_navigator.service.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    // History save karo
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<SearchHistoryEntity>> saveHistory(
            @Valid @RequestBody HistorySaveRequest request) {

        SearchHistoryEntity saved = historyService.saveHistory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "History saved successfully", saved));
    }

    // User ki saari history fetch karo
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<SearchHistoryEntity>>> getUserHistory(
            @PathVariable Long userId) {

        List<SearchHistoryEntity> historyList =
                historyService.getUserHistory(userId);

        return ResponseEntity.ok(ApiResponse.success(
                "History fetched successfully", historyList));
    }

    // Single history delete karo
    @DeleteMapping("/{historyId}")
    public ResponseEntity<ApiResponse<?>> deleteHistory(
            @PathVariable Long historyId) {

        historyService.deleteHistory(historyId);

        return ResponseEntity.ok(ApiResponse.success(
                "History deleted successfully", null));
    }

    // User ki saari history clear karo
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<ApiResponse<?>> clearAllHistory(
            @PathVariable Long userId) {

        historyService.clearAllHistory(userId);

        return ResponseEntity.ok(ApiResponse.success(
                "All history cleared successfully", null));
    }
}