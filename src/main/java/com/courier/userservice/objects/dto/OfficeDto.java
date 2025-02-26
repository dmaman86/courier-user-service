package com.courier.userservice.objects.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeDto {

  private Long id;
  private String name;
  private List<BranchDto> branches;
}
