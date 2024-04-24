package com.pvt152.StudentLoppet.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pvt152.StudentLoppet.dto.UniversityScoreDTO;
import com.pvt152.StudentLoppet.model.University;
import com.pvt152.StudentLoppet.repository.UserRepository;

@Service
public class UniversityLeaderboardService {
    private final UserRepository userRepository;

    public UniversityLeaderboardService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UniversityScoreDTO> calculateUniversityScores() {
        return userRepository.findScoresByUniversity().stream()
                .map(result -> new UniversityScoreDTO(
                        (University) result[0],
                        ((Number) result[1]).intValue()))
                .collect(Collectors.toList());
    }
}
