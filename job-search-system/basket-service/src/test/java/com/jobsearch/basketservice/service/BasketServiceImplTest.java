package com.jobsearch.basketservice.service;

import com.jobsearch.basketservice.dto.BasketRequest;
import com.jobsearch.basketservice.dto.BasketResponse;
import com.jobsearch.basketservice.entity.JobBasket;
import com.jobsearch.basketservice.exception.BasketItemNotFoundException;
import com.jobsearch.basketservice.exception.DuplicateBasketException;
import com.jobsearch.basketservice.repository.JobBasketRepository;
import com.jobsearch.basketservice.service.impl.BasketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceImplTest {

    @Mock
    private JobBasketRepository basketRepository;

    @InjectMocks
    private BasketServiceImpl basketService;

    private BasketRequest sampleRequest;
    private JobBasket sampleBasket;

    @BeforeEach
    void setUp() {
        sampleRequest = BasketRequest.builder()
                .jobSeekerId(101L)
                .jobId(202L)
                .build();

        sampleBasket = JobBasket.builder()
                .basketId(1L)
                .jobSeekerId(101L)
                .jobId(202L)
                .addedDate(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("addToBasket - success saves and returns basket response")
    void testAddToBasket_Success() {
        when(basketRepository.existsByJobSeekerIdAndJobId(101L, 202L)).thenReturn(false);
        when(basketRepository.save(any(JobBasket.class))).thenReturn(sampleBasket);

        BasketResponse response = basketService.addToBasket(sampleRequest);

        assertNotNull(response);
        assertEquals(1L, response.getBasketId());
        assertEquals(101L, response.getJobSeekerId());
        assertEquals(202L, response.getJobId());
        assertNotNull(response.getAddedDate());

        verify(basketRepository, times(1)).existsByJobSeekerIdAndJobId(101L, 202L);
        verify(basketRepository, times(1)).save(any(JobBasket.class));
    }

    @Test
    @DisplayName("addToBasket - duplicate throws DuplicateBasketException")
    void testAddToBasket_DuplicateThrowsDuplicateBasketException() {
        when(basketRepository.existsByJobSeekerIdAndJobId(101L, 202L)).thenReturn(true);

        DuplicateBasketException exception = assertThrows(
                DuplicateBasketException.class,
                () -> basketService.addToBasket(sampleRequest)
        );

        assertTrue(exception.getMessage().contains("already in the basket"));
        verify(basketRepository, times(1)).existsByJobSeekerIdAndJobId(101L, 202L);
        verify(basketRepository, never()).save(any(JobBasket.class));
    }

    @Test
    @DisplayName("removeFromBasket - not found throws BasketItemNotFoundException")
    void testRemoveFromBasket_NotFoundThrowsBasketItemNotFoundException() {
        when(basketRepository.findById(999L)).thenReturn(Optional.empty());

        BasketItemNotFoundException exception = assertThrows(
                BasketItemNotFoundException.class,
                () -> basketService.removeFromBasket(999L)
        );

        assertTrue(exception.getMessage().contains("Basket item not found with id: 999"));
        verify(basketRepository, times(1)).findById(999L);
        verify(basketRepository, never()).delete(any(JobBasket.class));
    }

    @Test
    @DisplayName("removeFromBasket - success deletes item")
    void testRemoveFromBasket_Success() {
        when(basketRepository.findById(1L)).thenReturn(Optional.of(sampleBasket));
        doNothing().when(basketRepository).delete(sampleBasket);

        assertDoesNotThrow(() -> basketService.removeFromBasket(1L));

        verify(basketRepository, times(1)).findById(1L);
        verify(basketRepository, times(1)).delete(sampleBasket);
    }

    @Test
    @DisplayName("getBasketBySeeker - returns list of basket items")
    void testGetBasketBySeeker_Success() {
        when(basketRepository.findByJobSeekerId(101L)).thenReturn(List.of(sampleBasket));

        List<BasketResponse> responses = basketService.getBasketBySeeker(101L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getBasketId());
        assertEquals(101L, responses.get(0).getJobSeekerId());
        assertEquals(202L, responses.get(0).getJobId());

        verify(basketRepository, times(1)).findByJobSeekerId(101L);
    }

    @Test
    @DisplayName("removeByJobAndSeeker - success deletes item")
    void testRemoveByJobAndSeeker_Success() {
        when(basketRepository.findByJobSeekerIdAndJobId(101L, 202L)).thenReturn(Optional.of(sampleBasket));
        doNothing().when(basketRepository).delete(sampleBasket);

        assertDoesNotThrow(() -> basketService.removeByJobAndSeeker(101L, 202L));

        verify(basketRepository, times(1)).findByJobSeekerIdAndJobId(101L, 202L);
        verify(basketRepository, times(1)).delete(sampleBasket);
    }

    @Test
    @DisplayName("removeByJobAndSeeker - not found throws BasketItemNotFoundException")
    void testRemoveByJobAndSeeker_NotFoundThrowsBasketItemNotFoundException() {
        when(basketRepository.findByJobSeekerIdAndJobId(101L, 999L)).thenReturn(Optional.empty());

        assertThrows(
                BasketItemNotFoundException.class,
                () -> basketService.removeByJobAndSeeker(101L, 999L)
        );

        verify(basketRepository, times(1)).findByJobSeekerIdAndJobId(101L, 999L);
        verify(basketRepository, never()).delete(any(JobBasket.class));
    }
}
