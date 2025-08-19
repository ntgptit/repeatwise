package com.spacedlearning.mapper;

import com.spacedlearning.dto.review.ReviewHistoryCreateRequest;
import com.spacedlearning.dto.review.ReviewHistoryResponse;
import com.spacedlearning.dto.review.ReviewHistoryUpdateRequest;
import com.spacedlearning.entity.ReviewHistory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewHistoryMapper {

    @Autowired
    private ModelMapper modelMapper;

    public ReviewHistory toEntity(ReviewHistoryCreateRequest request) {
        return modelMapper.map(request, ReviewHistory.class);
    }

    public void updateEntityFromRequest(ReviewHistoryUpdateRequest request, ReviewHistory entity) {
        if (request.getScore() != null) {
            entity.setScore(request.getScore());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getNote() != null) {
            entity.setNote(request.getNote());
        }
    }

    public ReviewHistoryResponse toResponse(ReviewHistory entity) {
        ReviewHistoryResponse response = modelMapper.map(entity, ReviewHistoryResponse.class);
        
        // Set additional fields
        if (entity.getSet() != null) {
            response.setSetId(entity.getSet().getId());
            response.setSetName(entity.getSet().getName());
        }
        
        return response;
    }

    public List<ReviewHistoryResponse> toResponseList(List<ReviewHistory> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
