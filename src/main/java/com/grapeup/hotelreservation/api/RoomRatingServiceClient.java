package com.grapeup.hotelreservation.api;

import com.grapeup.hotelreservation.dto.RoomRatingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "rating-service")
public interface RoomRatingServiceClient {

    @GetMapping("/ratings")
    List<RoomRatingDto> getAll();

    @GetMapping("/ratings/{roomId}")
    Optional<RoomRatingDto> getByRoomId(@PathVariable("roomId") Long roomId);
}
