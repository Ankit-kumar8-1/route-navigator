package com.navigation.route_navigator.controller;

import com.navigation.route_navigator.dto.ApiResponse;
import com.navigation.route_navigator.dto.RouteRequest;
import com.navigation.route_navigator.dto.RouteResponse;
import com.navigation.route_navigator.service.ETAService;
import com.navigation.route_navigator.service.FuelService;
import com.navigation.route_navigator.service.RouteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;
    private final ETAService etaService;
    private final FuelService fuelService;

    // 3 routes fetch karo
    @PostMapping("/find")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> findRoutes(
            @Valid @RequestBody RouteRequest request) {

        List<RouteResponse> routes = routeService.findRoutes(request);

        return ResponseEntity.ok(ApiResponse.success(
                "Routes fetched successfully", routes));
    }

    // ETA calculate karo
    @GetMapping("/eta")
    public ResponseEntity<ApiResponse<Map<String, String>>> getETA(
            @RequestParam Double distanceKm) {

        Map<String, String> eta = Map.of(
                "at60Kmph", etaService.formatETA(
                        etaService.calculateETA(distanceKm, 60)),
                "at80Kmph", etaService.formatETA(
                        etaService.calculateETA(distanceKm, 80)),
                "at100Kmph", etaService.formatETA(
                        etaService.calculateETA(distanceKm, 100))
        );

        return ResponseEntity.ok(ApiResponse.success(
                "ETA calculated successfully", eta));
    }

    // Fuel cost calculate karo
    @GetMapping("/fuel")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getFuelCost(
            @RequestParam Double distanceKm,
            @RequestParam Double vehicleMileage,
            @RequestParam Double fuelPrice) {

        Double fuelRequired = fuelService.calculateFuelNeeded(
                distanceKm, vehicleMileage);

        Double fuelCost = fuelService.calculateFuelCost(
                distanceKm, vehicleMileage, fuelPrice);

        Map<String, Double> fuelInfo = Map.of(
                "distanceKm", distanceKm,
                "fuelRequiredLitres", fuelRequired,
                "fuelCostRupees", fuelCost
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Fuel cost calculated successfully", fuelInfo));
    }
}