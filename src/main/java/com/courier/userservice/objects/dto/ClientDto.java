package com.courier.userservice.objects.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
public class ClientDto extends UserDto {

  private OfficeDto office;
  private List<BranchDto> branches;
}
