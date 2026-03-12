package com.navigation.route_navigator.service;

import com.navigation.route_navigator.exceptions.InvalidInputException;
import org.springframework.stereotype.Service;

@Service
public class FuelService {

    // Fuel cost calculate karo
    // Formula → Fuel Needed = Distance / Mileage
    //            Cost = Fuel Needed × Price Per Litre
    public Double calculateFuelCost(Double distanceKm,
                                    Double vehicleMileage,
                                    Double fuelPricePerLitre) {

        if (distanceKm == null || distanceKm <= 0) {
            throw new InvalidInputException(
                    "Distance must be greater than 0");
        }

        if (vehicleMileage == null || vehicleMileage <= 0) {
            throw new InvalidInputException(
                    "Vehicle mileage must be greater than 0");
        }

        if (fuelPricePerLitre == null || fuelPricePerLitre <= 0) {
            throw new InvalidInputException(
                    "Fuel price must be greater than 0");
        }

        double fuelNeeded = distanceKm / vehicleMileage;
        double totalCost = fuelNeeded * fuelPricePerLitre;

        return Math.round(totalCost * 100.0) / 100.0;
    }

    // Fuel litres calculate karo sirf
    public Double calculateFuelNeeded(Double distanceKm,
                                      Double vehicleMileage) {

        if (distanceKm == null || distanceKm <= 0) {
            throw new InvalidInputException(
                    "Distance must be greater than 0");
        }

        if (vehicleMileage == null || vehicleMileage <= 0) {
            throw new InvalidInputException(
                    "Vehicle mileage must be greater than 0");
        }

        double fuelNeeded = distanceKm / vehicleMileage;
        return Math.round(fuelNeeded * 100.0) / 100.0;
    }
}