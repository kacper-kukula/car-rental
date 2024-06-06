package com.carrental.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrental.dto.rental.RentalCreateRequestDto;
import com.carrental.dto.rental.RentalResponseDto;
import com.carrental.model.User;
import com.carrental.security.util.AuthenticationUtil;
import com.carrental.service.RentalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    @MockBean
    private AuthenticationUtil authenticationUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private RentalCreateRequestDto rentalCreateRequestDto;
    private RentalResponseDto rentalResponseDto;

    @BeforeEach
    void setUp() {
        rentalCreateRequestDto = new RentalCreateRequestDto(1L, LocalDate.now().plusDays(1));
        rentalResponseDto = new RentalResponseDto(1L, 1L, 1L,
                LocalDate.now(), LocalDate.now().plusDays(1), null);
    }

    @Test
    @DisplayName("Create a new rental successfully")
    @WithMockUser(roles = {"CUSTOMER"})
    void createRental_NewRentalCreated_ReturnsRentalResponseDto() throws Exception {
        // Given
        Mockito.when(rentalService.createRental(Mockito.any(RentalCreateRequestDto.class)))
                .thenReturn(rentalResponseDto);

        // When & Then
        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalCreateRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(rentalResponseDto.id()))
                .andExpect(jsonPath("$.userId").value(rentalResponseDto.userId()))
                .andExpect(jsonPath("$.carId").value(rentalResponseDto.carId()))
                .andExpect(jsonPath("$.rentalDate").exists())
                .andExpect(jsonPath("$.returnDate").exists())
                .andExpect(jsonPath("$.actualReturnDate").doesNotExist());
    }

    @Test
    @DisplayName("Manager specifying null user ID fetches all rentals")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void findRentalsByUserIdAndStatus_ManagerDoesntSpecifyUserId_ReturnsAllRentals()
            throws Exception {
        // Given
        List<RentalResponseDto> allRentals = Arrays.asList(rentalResponseDto, rentalResponseDto);
        Mockito.when(rentalService.findRentalsByUserIdAndStatus(null, true))
                .thenReturn(allRentals);
        Mockito.when(authenticationUtil.isManager()).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(rentalResponseDto.id()))
                .andExpect(jsonPath("$[0].userId").value(rentalResponseDto.userId()))
                .andExpect(jsonPath("$[0].carId").value(rentalResponseDto.carId()))
                .andExpect(jsonPath("$[0].rentalDate").exists())
                .andExpect(jsonPath("$[0].returnDate").exists())
                .andExpect(jsonPath("$[0].actualReturnDate").doesNotExist())
                .andExpect(jsonPath("$[1].id").value(rentalResponseDto.id()))
                .andExpect(jsonPath("$[1].userId").value(rentalResponseDto.userId()))
                .andExpect(jsonPath("$[1].carId").value(rentalResponseDto.carId()))
                .andExpect(jsonPath("$[1].rentalDate").exists())
                .andExpect(jsonPath("$[1].returnDate").exists())
                .andExpect(jsonPath("$[1].actualReturnDate").doesNotExist());
    }

    @Test
    @DisplayName("Manager fetches rentals by specific user ID")
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void findRentalsByUserIdAndStatus_ManagerSpecifyUserId_ReturnsRentalsBelongingToUser()
            throws Exception {
        // Given
        Long userId = 123L;
        List<RentalResponseDto> userRentals = Arrays.asList(rentalResponseDto, rentalResponseDto);
        Mockito.when(rentalService.findRentalsByUserIdAndStatus(userId, true))
                .thenReturn(userRentals);
        Mockito.when(authenticationUtil.isManager()).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/rentals")
                        .param("user_id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(rentalResponseDto.id()))
                .andExpect(jsonPath("$[0].userId").value(rentalResponseDto.userId()))
                .andExpect(jsonPath("$[0].carId").value(rentalResponseDto.carId()))
                .andExpect(jsonPath("$[0].rentalDate").exists())
                .andExpect(jsonPath("$[0].returnDate").exists())
                .andExpect(jsonPath("$[0].actualReturnDate").doesNotExist())
                .andExpect(jsonPath("$[1].id").value(rentalResponseDto.id()))
                .andExpect(jsonPath("$[1].userId").value(rentalResponseDto.userId()))
                .andExpect(jsonPath("$[1].carId").value(rentalResponseDto.carId()))
                .andExpect(jsonPath("$[1].rentalDate").exists())
                .andExpect(jsonPath("$[1].returnDate").exists())
                .andExpect(jsonPath("$[1].actualReturnDate").doesNotExist());
    }

    @Test
    @DisplayName("Customer forbidden to specify user ID")
    @WithMockUser(username = "customer", authorities = {"CUSTOMER"})
    void findRentalsByUserIdAndStatus_CustomerSpecifyUserId_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/rentals")
                        .param("user_id", "123"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Customer fetches their own rentals")
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    void findRentalsByUserIdAndStatus_CustomerDoesntSpecifyUserId_ReturnsOnlyOwnRentals()
            throws Exception {
        // Given
        Long customerId = 123L;
        User dummyUser = new User();
        dummyUser.setId(customerId);
        List<RentalResponseDto> ownRentals = Arrays.asList(rentalResponseDto, rentalResponseDto);
        Mockito.when(rentalService.findRentalsByUserIdAndStatus(null, true))
                .thenReturn(ownRentals);

        // When & Then
        mockMvc.perform(get("/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(rentalResponseDto.id()))
                .andExpect(jsonPath("$[0].userId").value(rentalResponseDto.userId()))
                .andExpect(jsonPath("$[0].carId").value(rentalResponseDto.carId()))
                .andExpect(jsonPath("$[0].rentalDate").exists())
                .andExpect(jsonPath("$[0].returnDate").exists())
                .andExpect(jsonPath("$[0].actualReturnDate").doesNotExist())
                .andExpect(jsonPath("$[1].id").value(rentalResponseDto.id()))
                .andExpect(jsonPath("$[1].userId").value(rentalResponseDto.userId()))
                .andExpect(jsonPath("$[1].carId").value(rentalResponseDto.carId()))
                .andExpect(jsonPath("$[1].rentalDate").exists())
                .andExpect(jsonPath("$[1].returnDate").exists())
                .andExpect(jsonPath("$[1].actualReturnDate").doesNotExist());
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    @DisplayName("Find rental by ID successfully")
    void findRentalById_RentalRetrieved_ReturnsRentalResponseDto() throws Exception {
        // Given
        Mockito.when(rentalService.findRentalById(Mockito.anyLong()))
                .thenReturn(rentalResponseDto);

        // When & Then
        mockMvc.perform(get("/rentals/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rentalResponseDto.id()))
                .andExpect(jsonPath("$.userId").value(rentalResponseDto.userId()))
                .andExpect(jsonPath("$.carId").value(rentalResponseDto.carId()))
                .andExpect(jsonPath("$.rentalDate").exists())
                .andExpect(jsonPath("$.returnDate").exists())
                .andExpect(jsonPath("$.actualReturnDate").doesNotExist());
    }

    @Test
    @DisplayName("Return rental successfully")
    @WithMockUser(roles = {"CUSTOMER"})
    void returnRental_RentalReturned_ReturnsSuccessMessage() throws Exception {
        // Given

        // When & Then
        mockMvc.perform(post("/rentals/{id}/return", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Rental (ID: 1) successfully returned."));
    }
}
