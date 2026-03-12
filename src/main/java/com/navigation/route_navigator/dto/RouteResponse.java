package com.navigation.route_navigator.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteResponse {

    private int routeNumber;


    private Double totalDistanceKm;


    private List<String> citiesOnRoute;


    private String etaAt60Kmph;
    private String etaAt80Kmph;
    private String etaAt100Kmph;


    private Double estimatedFuelCost;

    // Route : "Fastest", "Shortest", "Alternate"
    private String routeType;
}
