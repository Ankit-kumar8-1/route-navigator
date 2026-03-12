package com.navigation.route_navigator.service;

import com.navigation.route_navigator.exceptions.InvalidInputException;
import org.springframework.stereotype.Service;

@Service
public class ETAService {


    public Double calculateETA(Double distanceKm, double speedKmph) {

        if (distanceKm == null || distanceKm <= 0) {
            throw new InvalidInputException("Distance must be greater than 0");
        }

        if (speedKmph <= 0) {
            throw new InvalidInputException("Speed must be greater than 0");
        }

        double rawHours = distanceKm / speedKmph;
        return Math.round(rawHours * 100.0) / 100.0;
    }

    // Hours ko readable format mein convert karo
    // 2.5 → "2 hrs 30 mins"
    public String formatETA(Double hours) {

        if (hours == null || hours <= 0) {
            return "N/A";
        }

        int totalMinutes = (int) Math.round(hours * 60);
        int hrs = totalMinutes / 60;
        int mins = totalMinutes % 60;

        if (hrs == 0) {
            return mins + " mins";
        } else if (mins == 0) {
            return hrs + " hrs";
        } else {
            return hrs + " hrs " + mins + " mins";
        }
    }
}