package com.crio.videorentalsystem.controller;

import com.crio.videorentalsystem.dto.VideoRequest;
import com.crio.videorentalsystem.entity.Video;
import com.crio.videorentalsystem.service.VideoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    // CUSTOMER or ADMIN
    @GetMapping
    public ResponseEntity<List<Video>> getAllAvailableVideos() {
        return ResponseEntity.ok(videoService.getAllAvailableVideos());
    }

    // CUSTOMER or ADMIN
    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideoById(id));
    }

    // ADMIN only
    @PostMapping
    public ResponseEntity<Video> createVideo(@Valid @RequestBody VideoRequest request) {
        Video created = videoService.createVideo(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ADMIN only
    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @Valid @RequestBody VideoRequest request) {
        return ResponseEntity.ok(videoService.updateVideo(id, request));
    }

    // ADMIN only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}