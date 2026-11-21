package com.crio.videorentalsystem.repository;

import com.crio.videorentalsystem.entity.Rental;
import com.crio.videorentalsystem.entity.User;
import com.crio.videorentalsystem.entity.Video;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    long countByUserAndActiveTrue(User user);

    Optional<Rental> findByUserAndVideoAndActiveTrue(User user, Video video);

    List<Rental> findByUserAndActiveTrue(User user);
}