package com.grapeup.hotelreservation.controller;

import com.grapeup.hotelreservation.api.RoomRatingServiceClient;
import com.grapeup.hotelreservation.dto.RoomRatingDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    private static final String RATINGS_MAPPING = "/ratings/";

    private RoomRatingServiceClient roomRatingServiceClient;

    public RatingController(RoomRatingServiceClient roomRatingServiceClient) {
        this.roomRatingServiceClient = roomRatingServiceClient;
    }

    @GetMapping()
    public List<RoomRatingDto> getRatings() {
        return roomRatingServiceClient.getAll();
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomRating(@PathVariable Long roomId) {
        return roomRatingServiceClient.getByRoomId(roomId)
                .map(ratingDto -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .location(new URI(RATINGS_MAPPING + ratingDto.getId()))
                                .body(ratingDto);
                    } catch (URISyntaxException e ) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
