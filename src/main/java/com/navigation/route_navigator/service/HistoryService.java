package com.navigation.route_navigator.service;

import com.navigation.route_navigator.dto.HistorySaveRequest;
import com.navigation.route_navigator.entities.SearchHistoryEntity;
import com.navigation.route_navigator.entities.UserEntity;
import com.navigation.route_navigator.exceptions.InvalidInputException;
import com.navigation.route_navigator.exceptions.ResourceNotFoundException;
import com.navigation.route_navigator.repository.SearchHistoryRepository;
import com.navigation.route_navigator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    // History save karo
    public SearchHistoryEntity saveHistory(HistorySaveRequest request) {

        // User exist karta hai ya nahi
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id : " + request.getUserId()));

        // Same search already saved hai ya nahi
        boolean alreadyExists = searchHistoryRepository
                .existsByUserIdAndFromLocationAndToLocation(
                        request.getUserId(),
                        request.getFromLocation(),
                        request.getToLocation());

        if (alreadyExists) {
            throw new InvalidInputException(
                    "This route is already saved in your history");
        }

        SearchHistoryEntity history = SearchHistoryEntity.builder()
                .user(user)
                .fromLocation(request.getFromLocation())
                .toLocation(request.getToLocation())
                .build();

        log.info("Saving history for userId : {}", request.getUserId());
        return searchHistoryRepository.save(history);
    }

    // User ki saari history fetch karo
    public List<SearchHistoryEntity> getUserHistory(Long userId) {

        // User exist karta hai ya nahi
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id : " + userId);
        }

        List<SearchHistoryEntity> historyList = searchHistoryRepository
                .findByUserIdOrderBySearchedAtDesc(userId);

        if (historyList.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No history found for userId : " + userId);
        }

        return historyList;
    }

    // Single history delete karo
    @Transactional
    public void deleteHistory(Long historyId) {

        if (!searchHistoryRepository.existsById(historyId)) {
            throw new ResourceNotFoundException(
                    "History not found with id : " + historyId);
        }

        searchHistoryRepository.deleteById(historyId);
        log.info("Deleted history with id : {}", historyId);
    }

    // User ki saari history delete karo
    @Transactional
    public void clearAllHistory(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User not found with id : " + userId);
        }

        if (!searchHistoryRepository.existsByUserId(userId)) {
            throw new ResourceNotFoundException(
                    "No history found for userId : " + userId);
        }

        searchHistoryRepository.deleteByUserId(userId);
        log.info("Cleared all history for userId : {}", userId);
    }
}