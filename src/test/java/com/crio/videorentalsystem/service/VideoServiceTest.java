package com.crio.videorentalsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crio.videorentalsystem.dto.VideoRequest;
import com.crio.videorentalsystem.entity.Video;
import com.crio.videorentalsystem.exception.ResourceNotFoundException;
import com.crio.videorentalsystem.repository.VideoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService;

    @Test
    void createVideo_WhenAvailableIsNull_DefaultsToTrue() {
        VideoRequest request = new VideoRequest("Inception", "Christopher Nolan", "Sci-Fi", null);
        Video savedVideo = Video.builder()
            .id(1L)
            .title("Inception")
            .director("Christopher Nolan")
            .genre("Sci-Fi")
            .available(true)
            .build();

        when(videoRepository.save(any(Video.class))).thenReturn(savedVideo);

        Video result = videoService.createVideo(request);

        assertTrue(result.isAvailable());
        assertEquals("Inception", result.getTitle());
    }

    @Test
    void getAllAvailableVideos_ReturnsAvailableList() {
        Video video = Video.builder().id(1L).title("Interstellar").available(true).build();
        when(videoRepository.findByAvailableTrue()).thenReturn(List.of(video));

        List<Video> results = videoService.getAllAvailableVideos();

        assertEquals(1, results.size());
        assertEquals("Interstellar", results.get(0).getTitle());
    }

    @Test
    void getVideoById_WhenNotFound_ThrowsResourceNotFoundException() {
        when(videoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> videoService.getVideoById(99L));
    }

    @Test
    void deleteVideo_WhenNotFound_ThrowsResourceNotFoundException() {
        when(videoRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> videoService.deleteVideo(99L));
        verify(videoRepository, never()).deleteById(99L);
    }
}