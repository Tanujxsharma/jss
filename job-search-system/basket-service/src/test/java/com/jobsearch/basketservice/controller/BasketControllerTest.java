package com.jobsearch.basketservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobsearch.basketservice.dto.BasketRequest;
import com.jobsearch.basketservice.dto.BasketResponse;
import com.jobsearch.basketservice.exception.BasketItemNotFoundException;
import com.jobsearch.basketservice.exception.DuplicateBasketException;
import com.jobsearch.basketservice.security.JwtAuthenticationFilter;
import com.jobsearch.basketservice.security.JwtUtil;
import com.jobsearch.basketservice.service.BasketService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BasketController.class)
@AutoConfigureMockMvc(addFilters = false)
class BasketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BasketService basketService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /api/basket - success returns 201 Created")
    void testAddToBasket_Success() throws Exception {
        BasketRequest request = BasketRequest.builder()
                .jobSeekerId(101L)
                .jobId(202L)
                .build();

        BasketResponse response = BasketResponse.builder()
                .basketId(1L)
                .jobSeekerId(101L)
                .jobId(202L)
                .addedDate(LocalDateTime.now())
                .build();

        when(basketService.addToBasket(any(BasketRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/basket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.basketId").value(1L))
                .andExpect(jsonPath("$.jobSeekerId").value(101L))
                .andExpect(jsonPath("$.jobId").value(202L));
    }

    @Test
    @DisplayName("POST /api/basket - validation failure returns 400 Bad Request")
    void testAddToBasket_ValidationFailure() throws Exception {
        BasketRequest invalidRequest = BasketRequest.builder()
                .jobSeekerId(null)
                .jobId(null)
                .build();

        mockMvc.perform(post("/api/basket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.jobSeekerId").exists())
                .andExpect(jsonPath("$.validationErrors.jobId").exists());
    }

    @Test
    @DisplayName("POST /api/basket - duplicate returns 409 Conflict")
    void testAddToBasket_DuplicateConflict() throws Exception {
        BasketRequest request = BasketRequest.builder()
                .jobSeekerId(101L)
                .jobId(202L)
                .build();

        when(basketService.addToBasket(any(BasketRequest.class)))
                .thenThrow(new DuplicateBasketException("Job with ID 202 is already in the basket"));

        mockMvc.perform(post("/api/basket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Job with ID 202 is already in the basket"));
    }

    @Test
    @DisplayName("GET /api/basket/{jobSeekerId} - returns 200 OK")
    void testGetBasketBySeeker_Success() throws Exception {
        BasketResponse response = BasketResponse.builder()
                .basketId(1L)
                .jobSeekerId(101L)
                .jobId(202L)
                .addedDate(LocalDateTime.now())
                .build();

        when(basketService.getBasketBySeeker(101L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/basket/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].basketId").value(1L))
                .andExpect(jsonPath("$[0].jobSeekerId").value(101L))
                .andExpect(jsonPath("$[0].jobId").value(202L));
    }

    @Test
    @DisplayName("DELETE /api/basket/{basketId} - returns 204 No Content")
    void testRemoveFromBasket_Success() throws Exception {
        doNothing().when(basketService).removeFromBasket(1L);

        mockMvc.perform(delete("/api/basket/1"))
                .andExpect(status().isNoContent());

        verify(basketService, times(1)).removeFromBasket(1L);
    }

    @Test
    @DisplayName("DELETE /api/basket/{basketId} - not found returns 404")
    void testRemoveFromBasket_NotFound() throws Exception {
        doThrow(new BasketItemNotFoundException("Basket item not found with id: 999"))
                .when(basketService).removeFromBasket(999L);

        mockMvc.perform(delete("/api/basket/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("DELETE /api/basket/seeker/{jobSeekerId}/job/{jobId} - returns 204 No Content")
    void testRemoveByJobAndSeeker_Success() throws Exception {
        doNothing().when(basketService).removeByJobAndSeeker(101L, 202L);

        mockMvc.perform(delete("/api/basket/seeker/101/job/202"))
                .andExpect(status().isNoContent());

        verify(basketService, times(1)).removeByJobAndSeeker(101L, 202L);
    }
}
