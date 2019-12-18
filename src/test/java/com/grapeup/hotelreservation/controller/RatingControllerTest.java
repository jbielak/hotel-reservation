package com.grapeup.hotelreservation.controller;

import com.grapeup.hotelreservation.api.RoomRatingServiceClient;
import com.grapeup.hotelreservation.dto.RoomRatingDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RatingControllerTest {

    @MockBean
    private RoomRatingServiceClient roomRatingServiceClient;

    @Autowired
    private MockMvc mockMvc;

    private static RoomRatingDto roomRatingDto1;
    private static RoomRatingDto roomRatingDto2;

    @BeforeAll
    public static void setup() {
        roomRatingDto1 = new RoomRatingDto(1L, 4.5);
        roomRatingDto2 = new RoomRatingDto(2L, 3.5);
    }

    @Test
    @DisplayName("GET /ratings - Success - empty list")
    public void shouldReturnEmptyListWhenNoRatings() throws Exception {
        when(roomRatingServiceClient.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/ratings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    @DisplayName("GET /ratings - Success")
    public void shouldReturnAllRatings() throws Exception {
        when(roomRatingServiceClient.getAll()).thenReturn(List.of(roomRatingDto1, roomRatingDto2));

        mockMvc.perform(get("/ratings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].rating", is(roomRatingDto1.getRating())));
    }

    @Test
    @DisplayName("GET /ratings/1 - Found")
    void shouldFindRatingByRoomId() throws Exception {
        when(roomRatingServiceClient.getByRoomId(anyLong())).thenReturn(Optional.of(roomRatingDto1));

        mockMvc.perform(get("/ratings/{roomId}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, "/ratings/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.rating", is(roomRatingDto1.getRating())));
    }

    @Test
    @DisplayName("GET /ratings/1 - Not Found")
    void shouldNotFindRatingByRoomId() throws Exception {
        when(roomRatingServiceClient.getByRoomId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/ratings/{roomId}", 99))
                .andExpect(status().isNotFound());
    }
}
