package com.crio.videorentalsystem.service;

import com.crio.videorentalsystem.dto.RentalResponse;
import com.crio.videorentalsystem.entity.Rental;
import com.crio.videorentalsystem.entity.User;
import com.crio.videorentalsystem.entity.Video;
import com.crio.videorentalsystem.exception.RentalException;
import com.crio.videorentalsystem.exception.ResourceNotFoundException;
import com.crio.videorentalsystem.repository.RentalRepository;
import com.crio.videorentalsystem.repository.UserRepository;
import com.crio.videorentalsystem.repository.VideoRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalService {

    private static final int MAX_ACTIVE_RENTALS = 2;

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    @Transactional
    public RentalResponse rentVideo(Long videoId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + videoId));

        // Rule 1: Video must be available
        if (!video.isAvailable()) {
            throw new RentalException("Video '" + video.getTitle() + "' is currently unavailable for rent");
        }

        // Rule 2: User can have at most 2 active rentals
        long activeRentalsCount = rentalRepository.countByUserAndActiveTrue(user);
        if (activeRentalsCount >= MAX_ACTIVE_RENTALS) {
            throw new RentalException("Rental limit reached: a user cannot have more than " + MAX_ACTIVE_RENTALS + " active rentals");
        }

        video.setAvailable(false);
        videoRepository.save(video);

        Rental rental = Rental.builder()
            .user(user)
            .video(video)
            .rentedAt(LocalDateTime.now())
            .active(true)
            .build();

        Rental savedRental = rentalRepository.save(rental);

        return new RentalResponse(
            savedRental.getId(),
            video.getId(),
            video.getTitle(),
            user.getEmail(),
            savedRental.getRentedAt(),
            null,
            true,
            "Video rented successfully"
        );
    }

    @Transactional
    public RentalResponse returnVideo(Long videoId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + videoId));

        Rental activeRental = rentalRepository.findByUserAndVideoAndActiveTrue(user, video)
            .orElseThrow(() -> new RentalException("No active rental found for this user and video"));

        activeRental.setActive(false);
        activeRental.setReturnedAt(LocalDateTime.now());
        rentalRepository.save(activeRental);

        video.setAvailable(true);
        videoRepository.save(video);

        return new RentalResponse(
            activeRental.getId(),
            video.getId(),
            video.getTitle(),
            user.getEmail(),
            activeRental.getRentedAt(),
            activeRental.getReturnedAt(),
            false,
            "Video returned successfully"
        );
    }
}