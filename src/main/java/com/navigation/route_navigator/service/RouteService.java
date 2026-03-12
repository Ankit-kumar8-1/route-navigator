package com.navigation.route_navigator.service;

import com.navigation.route_navigator.config.AppConfig;
import com.navigation.route_navigator.dto.RouteRequest;
import com.navigation.route_navigator.dto.RouteResponse;
import com.navigation.route_navigator.exceptions.ApiCallException;
import com.navigation.route_navigator.exceptions.InvalidInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {

    private final RestTemplate restTemplate;
    private final AppConfig appConfig;
    private final ETAService etaService;
    private final FuelService fuelService;


    private static final String OSRM_URL =
            "http://router.project-osrm.org/route/v1/driving/";

    public List<RouteResponse> findRoutes(RouteRequest request) {

        // Input validation
        if (request.getFromLocation().equalsIgnoreCase(
                request.getToLocation())) {
            throw new InvalidInputException(
                    "From and To location cannot be same");
        }

        try {
            // Coordinates fetch karo locations se
            double[] fromCoords = getCoordinates(request.getFromLocation());
            double[] toCoords = getCoordinates(request.getToLocation());

            // ORS API call karo
            List<Map<String, Object>> rawRoutes =
                    callOSRMApi(fromCoords, toCoords);

            // Response parse karo
            List<RouteResponse> routes = new ArrayList<>();
            String[] routeTypes = {"Fastest", "Shortest", "Alternate"};

            for (int i = 0; i < rawRoutes.size(); i++) {
                Map<String, Object> raw = rawRoutes.get(i);

                // Distance meters to km
                Double distanceKm = parseDistance(raw);

                // Cities parse karo
                List<String> cities = parseCities(raw);

                // ETA calculate karo
                Double eta60 = etaService.calculateETA(distanceKm, 60);
                Double eta80 = etaService.calculateETA(distanceKm, 80);
                Double eta100 = etaService.calculateETA(distanceKm, 100);

                // Fuel cost (optional)
                Double fuelCost = null;
                if (request.getVehicleMileage() != null
                        && request.getFuelPrice() != null) {
                    fuelCost = fuelService.calculateFuelCost(
                            distanceKm,
                            request.getVehicleMileage(),
                            request.getFuelPrice());
                }

                routes.add(RouteResponse.builder()
                        .routeNumber(i + 1)
                        .routeType(routeTypes[i])
                        .totalDistanceKm(distanceKm)
                        .citiesOnRoute(cities)
                        .etaAt60Kmph(etaService.formatETA(
                                etaService.calculateETA(distanceKm, 60)))
                        .etaAt80Kmph(etaService.formatETA(
                                etaService.calculateETA(distanceKm, 80)))
                        .etaAt100Kmph(etaService.formatETA(
                                etaService.calculateETA(distanceKm, 100)))
                        .estimatedFuelCost(fuelCost)
                        .build());
            }

            return routes;

        } catch (InvalidInputException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Route fetch failed : {}", ex.getMessage());
            throw new ApiCallException(
                    "Failed to fetch routes from OpenRouteService");
        }
    }

    // Location name → Coordinates [lng, lat]
    private double[] getCoordinates(String location) {
        String geocodeUrl =
                "https://api.openrouteservice.org/geocode/search?api_key="
                        + appConfig.getApiKey()
                        + "&text=" + location
                        + "&size=1";

        try {
            ResponseEntity<Map> response =
                    restTemplate.getForEntity(geocodeUrl, Map.class);

            List<Map> features = (List<Map>) response.getBody()
                    .get("features");

            if (features == null || features.isEmpty()) {
                throw new InvalidInputException(
                        "Location not found : " + location);
            }

            List<Double> coords = (List<Double>)
                    ((Map) features.get(0).get("geometry"))
                            .get("coordinates");

            return new double[]{coords.get(0), coords.get(1)};

        } catch (InvalidInputException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApiCallException(
                    "Geocoding failed for location : " + location);
        }
    }

    // ORS Directions API call
    private List<Map<String, Object>> callOSRMApi(
            double[] from, double[] to) {

        // OSRM URL format → lng,lat;lng,lat
        String url = OSRM_URL
                + from[0] + "," + from[1] + ";"
                + to[0] + "," + to[1]
                + "?alternatives=3&overview=false&steps=true";

        try {
            ResponseEntity<Map> response =
                    restTemplate.getForEntity(url, Map.class);

            List<Map<String, Object>> routes =
                    (List<Map<String, Object>>) response.getBody().get("routes");

            if (routes == null || routes.isEmpty()) {
                throw new ApiCallException(
                        "No routes found between given locations");
            }

            return routes;

        } catch (ApiCallException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApiCallException(
                    "Failed to fetch routes from OSRM : " + ex.getMessage());
        }
    }

    // Distance parse karo meters → km
    private Double parseDistance(Map<String, Object> route) {
        // OSRM distance meters mein deta hai
        Double distanceMeters =
                ((Number) route.get("distance")).doubleValue();
        return Math.round((distanceMeters / 1000) * 10.0) / 10.0;
    }

    // Cities/waypoints parse karo
    private List<String> parseCities(Map<String, Object> route) {
        List<String> cities = new ArrayList<>();
        try {
            List<Map<String, Object>> legs =
                    (List<Map<String, Object>>) route.get("legs");
            if (legs != null) {
                for (Map<String, Object> leg : legs) {
                    List<Map<String, Object>> steps =
                            (List<Map<String, Object>>) leg.get("steps");
                    if (steps != null) {
                        for (Map<String, Object> step : steps) {
                            Map<String, Object> maneuver =
                                    (Map<String, Object>) step.get("maneuver");
                            String name = (String) step.get("name");
                            if (name != null && !name.isBlank()) {
                                if (!cities.contains(name)) {
                                    cities.add(name);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Could not parse cities : {}", ex.getMessage());
        }
        return cities;
    }
}