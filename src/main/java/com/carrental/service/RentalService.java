package com.carrental.service;

import com.carrental.dto.rental.RentalCreateRequestDto;
import com.carrental.dto.rental.RentalResponseDto;
import java.util.List;

public interface RentalService {

    RentalResponseDto createRental(RentalCreateRequestDto request);

    List<RentalResponseDto> findRentalsByUserIdAndStatus(Long userId, boolean isActive);

    RentalResponseDto findRentalById(Long id);

    void returnRental(Long id);

    void checkOverdueRentals();
}
