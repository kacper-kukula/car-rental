package com.carrental.service;

import com.carrental.dto.rental.RentalRequestDto;
import com.carrental.dto.rental.RentalResponseDto;
import java.util.List;

public interface RentalService {

    RentalResponseDto createRental(RentalRequestDto request);

    List<RentalResponseDto> findRentalsByUserIdAndStatus(Long userId, boolean isActive);

    RentalResponseDto findRentalById(Long id);

    void returnRental(Long id);
}
