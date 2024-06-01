package com.carrental.mapper;

import com.carrental.config.MapperConfig;
import com.carrental.dto.payment.PaymentResponseDto;
import com.carrental.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {

    @Mapping(target = "rentalId", source = "rental.id")
    PaymentResponseDto toDto(Payment payment);
}
