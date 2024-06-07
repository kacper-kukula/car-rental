package com.carrental.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrental.dto.car.CarRequestDto;
import com.carrental.dto.car.CarResponseDto;
import com.carrental.model.Car;
import com.carrental.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Autowired
    private ObjectMapper objectMapper;

    private CarRequestDto carRequestDto;
    private CarResponseDto carResponseDto;

    @BeforeEach
    void setUp() {
        carRequestDto = new CarRequestDto("Camry", "Toyota", 5,
                BigDecimal.valueOf(50.00), Car.Type.SEDAN);
        carResponseDto = new CarResponseDto(1L, "Camry", "Toyota", 5,
                BigDecimal.valueOf(50.00), Car.Type.SEDAN);
    }

    @Test
    @DisplayName("Create a new car successfully")
    @WithMockUser(roles = {"MANAGER"})
    void createCar_NewCarCreated_ReturnsCarResponseDto() throws Exception {
        // Given
        Mockito.when(carService.createCar(Mockito.any(CarRequestDto.class)))
                .thenReturn(carResponseDto);

        // When & Then
        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(carResponseDto.id()))
                .andExpect(jsonPath("$.brand").value(carResponseDto.brand()))
                .andExpect(jsonPath("$.model").value(carResponseDto.model()))
                .andExpect(jsonPath("$.inventory").value(carResponseDto.inventory()))
                .andExpect(jsonPath("$.dailyFee").value(carResponseDto.dailyFee()))
                .andExpect(jsonPath("$.type").value(carResponseDto.type().toString()));
    }

    @Test
    @DisplayName("Get all cars successfully")
    void findAllCars_CarsRetrieved_ReturnsListOfCarResponseDto() throws Exception {
        // Given
        List<CarResponseDto> carList = Arrays.asList(carResponseDto, carResponseDto);
        Pageable pageable = PageRequest.of(0, 2);

        Mockito.when(carService.findAllCars(pageable)).thenReturn(carList);

        // When & Then
        mockMvc.perform(get("/cars")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(carResponseDto.id()))
                .andExpect(jsonPath("$[0].brand").value(carResponseDto.brand()))
                .andExpect(jsonPath("$[0].model").value(carResponseDto.model()))
                .andExpect(jsonPath("$[0].inventory").value(carResponseDto.inventory()))
                .andExpect(jsonPath("$[0].dailyFee").value(carResponseDto.dailyFee()))
                .andExpect(jsonPath("$[0].type").value(carResponseDto.type().toString()))
                .andExpect(jsonPath("$[1].id").value(carResponseDto.id()))
                .andExpect(jsonPath("$[1].brand").value(carResponseDto.brand()))
                .andExpect(jsonPath("$[1].model").value(carResponseDto.model()))
                .andExpect(jsonPath("$[1].inventory").value(carResponseDto.inventory()))
                .andExpect(jsonPath("$[1].dailyFee").value(carResponseDto.dailyFee()))
                .andExpect(jsonPath("$[1].type").value(carResponseDto.type().toString()));

        verify(carService, times(1)).findAllCars(pageable);
    }

    @Test
    @DisplayName("Get car by ID successfully")
    void findCarById_CarRetrieved_ReturnsCarResponseDto() throws Exception {
        // Given
        Mockito.when(carService.findCarById(Mockito.anyLong()))
                .thenReturn(carResponseDto);

        // When & Then
        mockMvc.perform(get("/cars/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carResponseDto.id()))
                .andExpect(jsonPath("$.brand").value(carResponseDto.brand()))
                .andExpect(jsonPath("$.model").value(carResponseDto.model()))
                .andExpect(jsonPath("$.inventory").value(carResponseDto.inventory()))
                .andExpect(jsonPath("$.dailyFee").value(carResponseDto.dailyFee()))
                .andExpect(jsonPath("$.type").value(carResponseDto.type().toString()));
    }

    @Test
    @DisplayName("Update car by ID successfully")
    @WithMockUser(roles = {"MANAGER"})
    void updateCarById_CarUpdated_ReturnsCarResponseDto() throws Exception {
        // Given
        Mockito.when(carService.updateCarById(Mockito.anyLong(), Mockito.any(CarRequestDto.class)))
                .thenReturn(carResponseDto);

        // When & Then
        mockMvc.perform(patch("/cars/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carResponseDto.id()))
                .andExpect(jsonPath("$.brand").value(carResponseDto.brand()))
                .andExpect(jsonPath("$.model").value(carResponseDto.model()))
                .andExpect(jsonPath("$.inventory").value(carResponseDto.inventory()))
                .andExpect(jsonPath("$.dailyFee").value(carResponseDto.dailyFee()))
                .andExpect(jsonPath("$.type").value(carResponseDto.type().toString()));
    }

    @Test
    @DisplayName("Delete car by ID successfully")
    @WithMockUser(roles = {"MANAGER"})
    void deleteCarById_CarDeleted_ReturnsSuccessMessage() throws Exception {
        // When & Then
        mockMvc.perform(delete("/cars/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Car (ID: 1) successfully deleted."));
    }
}
