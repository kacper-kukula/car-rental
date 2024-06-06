package com.carrental.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.carrental.dto.rental.RentalCreateRequestDto;
import com.carrental.dto.rental.RentalResponseDto;
import com.carrental.mapper.RentalMapper;
import com.carrental.model.Car;
import com.carrental.model.Rental;
import com.carrental.model.User;
import com.carrental.repository.CarRepository;
import com.carrental.repository.RentalRepository;
import com.carrental.security.util.AuthenticationUtil;
import com.carrental.service.impl.RentalServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private AuthenticationUtil authenticationUtil;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("Verify that createRental() method works with valid request")
    void createRental_ValidRequest_ReturnsCorrectResponse() {
        // Given
        RentalCreateRequestDto requestDto =
                new RentalCreateRequestDto(1L, LocalDate.now().plusDays(7));
        Car car = getDummyCar();
        User user = getDummyUser();
        Rental rental = new Rental();
        rental.setCar(car);
        rental.setUser(user);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(requestDto.returnDate());
        RentalResponseDto expectedResponseDto =
                new RentalResponseDto(1L, rental.getUser().getId(), rental.getCar().getId(),
                        rental.getRentalDate(), rental.getReturnDate(), null);

        when(carRepository.findById(requestDto.carId())).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(authenticationUtil.getCurrentUserFromDb()).thenReturn(user);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(expectedResponseDto);
        doNothing().when(notificationService).sendNotification(anyString());

        // When
        RentalResponseDto actual = rentalService.createRental(requestDto);

        // Then
        assertThat(actual).isEqualTo(expectedResponseDto);
        verify(carRepository, times(1)).findById(requestDto.carId());
        verify(rentalRepository, times(1)).save(rental);
        verify(rentalMapper, times(1)).toDto(rental);
        verify(notificationService, times(1)).sendNotification(anyString());
        verifyNoMoreInteractions(carRepository, rentalRepository, rentalMapper);
    }

    @Test
    @DisplayName("Verify that findRentalsByUserIdAndStatus() method works")
    void findRentalsByUserIdAndStatus_ReturnsRentals() {
        // Given
        final Long userId = 1L;
        final boolean isActive = true;
        Rental rental1 = getDummyRental();
        rental1.setId(1L);
        Rental rental2 = getDummyRental();
        rental2.setId(2L);
        Car car = getDummyCar();
        User user = getDummyUser();
        rental1.setCar(car);
        rental1.setUser(user);
        rental2.setCar(car);
        rental2.setUser(user);

        RentalResponseDto responseDto1 = new RentalResponseDto(1L, rental1.getUser().getId(),
                rental1.getCar().getId(), rental1.getRentalDate(), rental1.getReturnDate(), null);
        RentalResponseDto responseDto2 = new RentalResponseDto(2L, rental2.getUser().getId(),
                rental2.getCar().getId(), rental2.getRentalDate(), rental2.getReturnDate(), null);

        List<Rental> rentals = List.of(rental1, rental2);
        List<RentalResponseDto> expectedResponse = List.of(responseDto1, responseDto2);

        when(rentalRepository.findByUserIdAndStatus(userId, Rental.Status.ACTIVE))
                .thenReturn(rentals);
        when(rentalMapper.toDto(rental1)).thenReturn(responseDto1);
        when(rentalMapper.toDto(rental2)).thenReturn(responseDto2);
        when(authenticationUtil.isManager()).thenReturn(true);

        // When
        List<RentalResponseDto> actual =
                rentalService.findRentalsByUserIdAndStatus(userId, isActive);

        // Then
        assertThat(actual).containsExactlyElementsOf(expectedResponse);
        verify(rentalRepository, times(1)).findByUserIdAndStatus(userId, Rental.Status.ACTIVE);
        verify(rentalMapper, times(1)).toDto(rental1);
        verify(rentalMapper, times(1)).toDto(rental2);
        verifyNoMoreInteractions(rentalRepository, rentalMapper);
    }

    @Test
    @DisplayName("Verify that findRentalById() method works")
    void findRentalById_ValidId_ReturnsRental() {
        // Given
        Long rentalId = 1L;
        Rental rental = getDummyRental();
        rental.setId(rentalId);
        Car car = getDummyCar();
        User user = getDummyUser();
        rental.setCar(car);
        rental.setUser(user);
        RentalResponseDto expectedResponseDto =
                new RentalResponseDto(rentalId, rental.getUser().getId(), rental.getCar().getId(),
                        rental.getRentalDate(), rental.getReturnDate(), null);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(authenticationUtil.getCurrentUserFromDb()).thenReturn(user);
        when(rentalMapper.toDto(rental)).thenReturn(expectedResponseDto);

        // When
        RentalResponseDto actual = rentalService.findRentalById(rentalId);

        // Then
        assertThat(actual).isEqualTo(expectedResponseDto);
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(rentalMapper, times(1)).toDto(rental);
        verifyNoMoreInteractions(rentalRepository, rentalMapper);
    }

    @Test
    @DisplayName("Verify that returnRental() method works")
    void returnRental_ValidId_RentalReturnedSuccessfully() {
        // Given
        Long rentalId = 1L;
        Rental rental = getDummyRental();
        rental.setId(rentalId);
        Car car = getDummyCar();
        User user = getDummyUser();
        rental.setCar(car);
        rental.setUser(user);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(authenticationUtil.getCurrentUserFromDb()).thenReturn(user);
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        // When
        rentalService.returnRental(rentalId);

        // Then
        assertThat(rental.getStatus()).isEqualTo(Rental.Status.RETURNED);
        assertThat(car.getInventory()).isEqualTo(11);
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, times(1)).findById(car.getId());
        verify(rentalRepository, times(1)).save(rental);
        verify(carRepository, times(1)).save(car);
        verifyNoMoreInteractions(rentalRepository, carRepository);
    }

    private Rental getDummyRental() {
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(LocalDate.now().plusDays(7));
        rental.setStatus(Rental.Status.ACTIVE);

        return rental;
    }

    private User getDummyUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(User.Role.CUSTOMER);

        return user;
    }

    private Car getDummyCar() {
        Car car = new Car();
        car.setId(1L);
        car.setModel("A4");
        car.setBrand("Audi");
        car.setInventory(10);
        car.setDailyFee(BigDecimal.valueOf(50));
        car.setType(Car.Type.SEDAN);

        return car;
    }
}
