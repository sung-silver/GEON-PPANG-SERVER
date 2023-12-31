package com.org.gunbbang.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NutrientType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long nutrientTypeId;

  @NotNull private String nutrientTypeName;

  @NotNull private boolean isNutrientOpen; // 영양성분 공개

  @NotNull private boolean isIngredientOpen; // 원재료 공개

  @NotNull private boolean isNotOpen; // 비공개

  public boolean getIsNutrientOpen() {
    return isNutrientOpen;
  }

  public boolean getIsIngredientOpen() {
    return isIngredientOpen;
  }

  public boolean getIsNotOpen() {
    return isNotOpen;
  }
}
