package com.org.gunbbang.controller.DTO.response;

import com.org.gunbbang.controller.DTO.response.BaseDTO.BaseBakeryResponseDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class BakeryListReviewedByMemberDto extends BaseBakeryResponseDTO {
    private BreadTypeResponseDto breadTypeResponseDto;
    private Long reviewId;
    private String createdAt;

    public BakeryListReviewedByMemberDto(Long bakeryId, String bakeryName, String bakeryPicture, Boolean isHACCP, Boolean isVegan, Boolean isNonGMO, BreadTypeResponseDto breadTypeResponseDto, String firstNearStation, String secondNearStation, Long reviewId, String createdAt) {
        super(bakeryId, bakeryName, bakeryPicture, isHACCP, isVegan,
                isNonGMO, firstNearStation, secondNearStation);
        this.breadTypeResponseDto = breadTypeResponseDto;
        this.reviewId = reviewId;
        this.createdAt = createdAt;
    }
}
