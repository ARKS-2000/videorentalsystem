package com.crio.videorentalsystem.service;

import com.crio.videorentalsystem.dto.VideoRequest;
import com.crio.videorentalsystem.entity.Video;
import com.crio.videorentalsystem.exception.ResourceNotFoundException;
import com.crio.videorentalsystem.repository.VideoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    @Transactional(readOnly = true)
    public List<Video> getAllAvailableVideos() {
        return videoRepository.findByAvailableTrue();
    }

    @Transactional(readOnly = true)
    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));
    }

    @Transactional
    public Video createVideo(VideoRequest request) {
        boolean isAvailable = (request.available() != null) ? request.available() : true;

        Video video = Video.builder()
            .title(request.title())
            .director(request.director())
            .genre(request.genre())
            .available(isAvailable)
            .build();

        return videoRepository.save(video);
    }

    @Transactional
    public Video updateVideo(Long id, VideoRequest request) {
        Video existingVideo = getVideoById(id);

        existingVideo.setTitle(request.title());
        existingVideo.setDirector(request.director());
        existingVideo.setGenre(request.genre());
        if (request.available() != null) {
            existingVideo.setAvailable(request.available());
        }

        return videoRepository.save(existingVideo);
    }

    @Transactional
    public void deleteVideo(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Video not found with id: " + id);
        }
        videoRepository.deleteById(id);
    }
}