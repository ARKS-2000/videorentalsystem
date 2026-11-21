package com.crio.videorentalsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crio.videorentalsystem.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByAvailableTrue();
}