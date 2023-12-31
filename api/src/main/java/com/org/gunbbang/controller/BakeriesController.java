package com.org.gunbbang.controller;

import com.org.gunbbang.AOP.annotation.BakeryIdApiLog;
import com.org.gunbbang.common.dto.ApiResponse;
import com.org.gunbbang.controller.DTO.response.BakeryDetailResponseDTO;
import com.org.gunbbang.controller.DTO.response.BakeryListResponseDTO;
import com.org.gunbbang.controller.DTO.response.ReviewListResponseDTO;
import com.org.gunbbang.errorType.SuccessType;
import com.org.gunbbang.service.BakeryService;
import com.org.gunbbang.service.ReviewService;
import com.org.gunbbang.util.security.SecurityUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bakeries")
public class BakeriesController {

  private final BakeryService bakeryService;
  private final ReviewService reviewService;

  @GetMapping("")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<List<BakeryListResponseDTO>> getBakeryList(
      @RequestParam("sort") String sort,
      @RequestParam("isHard") boolean isHard,
      @RequestParam("isDessert") boolean isDessert,
      @RequestParam("isBrunch") boolean isBrunch) {
    Long memberId = SecurityUtil.getLoginMemberId();
    List<BakeryListResponseDTO> bakeryListResponseDto =
        bakeryService.getBakeryList(memberId, sort, isHard, isDessert, isBrunch);
    return ApiResponse.success(SuccessType.GET_BAKERY_LIST_SUCCESS, bakeryListResponseDto);
  }

  @GetMapping("/{bakeryId}")
  @ResponseStatus(HttpStatus.OK)
  @BakeryIdApiLog
  public ApiResponse<BakeryDetailResponseDTO> getBakeryDetail(
      @PathVariable("bakeryId") Long bakeryId) {
    Long memberId = SecurityUtil.getLoginMemberId();
    BakeryDetailResponseDTO bakeryDetailResponseDTO =
        bakeryService.getBakeryDetail(memberId, bakeryId);
    return ApiResponse.success(SuccessType.GET_BAKERY_DETAIL_SUCCESS, bakeryDetailResponseDTO);
  }

  @GetMapping("/{bakeryId}/reviews")
  @ResponseStatus(HttpStatus.OK)
  @BakeryIdApiLog
  public ApiResponse<ReviewListResponseDTO> getBakeryReviewList(
      @PathVariable("bakeryId") Long bakeryId) {
    ReviewListResponseDTO reviewListResponseDto = reviewService.getBakeryReviewList(bakeryId);
    return ApiResponse.success(SuccessType.GET_BAKERY_REVIEW_LIST_SUCCESS, reviewListResponseDto);
  }
}
