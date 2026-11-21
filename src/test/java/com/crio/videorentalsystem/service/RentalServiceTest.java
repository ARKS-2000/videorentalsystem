package com.crio.videorentalsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crio.videorentalsystem.dto.RentalResponse;
import com.crio.videorentalsystem.entity.Rental;
import com.crio.videorentalsystem.entity.Role;
import com.crio.videorentalsystem.entity.User;
import com.crio.videorentalsystem.entity.Video;
import com.crio.videorentalsystem.exception.RentalException;
import com.crio.videorentalsystem.repository.RentalRepository;
import com.crio.videorentalsystem.repository.UserRepository;
import com.crio.videorentalsystem.repository.VideoRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private RentalService rentalService;

    @Test
    void rentVideo_WhenUnderLimitAndVideoAvailable_Succeeds() {
        User user = User.builder().id(1L).email("cust@test.com").role(Role.CUSTOMER).build();
        Video video = Video.builder().id(10L).title("Inception").available(true).build();
        Rental savedRental = Rental.builder()
            .id(100L)
            .user(user)
            .video(video)
            .rentedAt(LocalDateTime.now())
            .active(true)
            .build();

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(user));
        when(videoRepository.findById(10L)).thenReturn(Optional.of(video));
        when(rentalRepository.countByUserAndActiveTrue(user)).thenReturn(1L);
        when(rentalRepository.save(any(Rental.class))).thenReturn(savedRental);

        RentalResponse response = rentalService.rentVideo(10L, "cust@test.com");

        assertFalse(video.isAvailable());
        assertTrue(response.active());
        assertEquals("Video rented successfully", response.message());
        verify(videoRepository).save(video);
    }

    @Test
    void rentVideo_WhenUserAlreadyHasTwoActiveRentals_ThrowsRentalException() {
        User user = User.builder().id(1L).email("cust@test.com").role(Role.CUSTOMER).build();
        Video video = Video.builder().id(10L).title("Inception").available(true).build();

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(user));
        when(videoRepository.findById(10L)).thenReturn(Optional.of(video));
        when(rentalRepository.countByUserAndActiveTrue(user)).thenReturn(2L);

        assertThrows(RentalException.class, () -> rentalService.rentVideo(10L, "cust@test.com"));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void returnVideo_WhenActiveRentalExists_MarksReturnedAndRestoresAvailability() {
        User user = User.builder().id(1L).email("cust@test.com").role(Role.CUSTOMER).build();
        Video video = Video.builder().id(10L).title("Inception").available(false).build();
        Rental activeRental = Rental.builder()
            .id(100L)
            .user(user)
            .video(video)
            .rentedAt(LocalDateTime.now().minusDays(1))
            .active(true)
            .build();

        when(userRepository.findByEmail("cust@test.com")).thenReturn(Optional.of(user));
        when(videoRepository.findById(10L)).thenReturn(Optional.of(video));
        when(rentalRepository.findByUserAndVideoAndActiveTrue(user, video)).thenReturn(Optional.of(activeRental));

        RentalResponse response = rentalService.returnVideo(10L, "cust@test.com");

        assertTrue(video.isAvailable());
        assertFalse(activeRental.isActive());
        assertFalse(response.active());
        verify(videoRepository).save(video);
        verify(rentalRepository).save(activeRental);
    }
}