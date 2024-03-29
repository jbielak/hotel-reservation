package com.grapeup.hotelreservation.controller;

import com.grapeup.hotelreservation.TestUtils;
import com.grapeup.hotelreservation.dto.ReservationDto;
import com.grapeup.hotelreservation.exception.AvailableRoomNotFoundException;
import com.grapeup.hotelreservation.model.Reservation;
import com.grapeup.hotelreservation.model.Room;
import com.grapeup.hotelreservation.model.RoomType;
import com.grapeup.hotelreservation.service.ReservationService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerTest {

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private MockMvc mockMvc;

    private static Reservation mockReservation;
    private static Room mockRoom;

    @BeforeAll
    public static void setup() {
        mockReservation = Reservation.builder().id(1L).username("test")
                .numberOfPeople(3).startDate(LocalDate.of(2020, 8, 1))
                .endDate(LocalDate.of(2020, 9, 1)).build();
        mockRoom = Room.builder().id(1L).roomType(RoomType.BASIC)
                .reservations(Set.of(mockReservation)).build();
        mockReservation.setRoom(mockRoom);
    }

    @Test
    @DisplayName("GET /reservations - Success - empty list")
    public void shouldReturnEmptyListWhenNoReservations() throws Exception {
        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /reservations - Success")
    public void shouldReturnAllReservations() throws Exception {
        Reservation mockReservation2 = Reservation.builder().id(2L).username("test")
                .numberOfPeople(5).startDate(LocalDate.of(2020, 7, 1))
                .endDate(LocalDate.of(2020, 9, 1)).build();
        Room room2 = Room.builder().id(2L).roomType(RoomType.SUITE).reservations(Set.of(mockReservation2)).build();
        mockReservation2.setRoom(room2);

        when(reservationService.findAll()).thenReturn(Arrays.asList(mockReservation, mockReservation2));

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(mockReservation.getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(mockReservation.getUsername())))
                .andExpect(jsonPath("$[0].numberOfPeople", is(mockReservation.getNumberOfPeople())))
                .andExpect(jsonPath("$[0].startDate", is(mockReservation.getStartDate().toString())))
                .andExpect(jsonPath("$[0].endDate", is(mockReservation.getEndDate().toString())))
                .andExpect(jsonPath("$[0].roomId", is(mockReservation.getRoom().getId().intValue())));
    }

    @Test
    @DisplayName("GET /reservations/1 - Found")
    void shouldGetReservationById() throws Exception {
        doReturn(Optional.of(mockReservation)).when(reservationService).findById(1L);

        mockMvc.perform(get("/reservations/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, "/reservations/1"))
                .andExpect(jsonPath("$.id", is(mockReservation.getId().intValue())))
                .andExpect(jsonPath("$.username", is(mockReservation.getUsername())))
                .andExpect(jsonPath("$.numberOfPeople", is(mockReservation.getNumberOfPeople())))
                .andExpect(jsonPath("$.startDate", is(mockReservation.getStartDate().toString())))
                .andExpect(jsonPath("$.endDate", is(mockReservation.getEndDate().toString())))
                .andExpect(jsonPath("$.roomId", is(mockReservation.getRoom().getId().intValue())));
    }

    @Test
    @DisplayName("GET /reservations/1 - Not Found")
    void shouldNotFindReservationById() throws Exception {
        doReturn(Optional.empty()).when(reservationService).findById(1L);

        mockMvc.perform(get("/reservations/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /reservations - Success")
    void shouldCreateReservation() throws Exception {
        ReservationDto postReservationDto = ReservationDto.builder().username("test")
                .numberOfPeople(3).startDate(LocalDate.of(2020, 8, 1))
                .endDate(LocalDate.of(2020, 9, 1)).build();

        doReturn(mockReservation).when(reservationService).save(any());

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(postReservationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, "/reservations/1"))
                .andExpect(jsonPath("$.id", is(mockReservation.getId().intValue())))
                .andExpect(jsonPath("$.username", is(mockReservation.getUsername())))
                .andExpect(jsonPath("$.numberOfPeople", is(mockReservation.getNumberOfPeople())))
                .andExpect(jsonPath("$.startDate", is(mockReservation.getStartDate().toString())))
                .andExpect(jsonPath("$.endDate", is(mockReservation.getEndDate().toString())))
                .andExpect(jsonPath("$.roomId", is(mockReservation.getRoom().getId().intValue())));
    }

    @Test
    @DisplayName("POST /reservations - Bad Request")
    void shouldReturnBadRequestWhenNoAvailableRoomForNewReservation() throws Exception {
        ReservationDto postReservationDto = ReservationDto.builder().username("test")
                .numberOfPeople(3).startDate(LocalDate.of(2020, 8, 1))
                .endDate(LocalDate.of(2020, 9, 1)).build();

        doThrow(new AvailableRoomNotFoundException()).when(reservationService).save(any());

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(postReservationDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /reservations - Bad Request - start date in the past")
    void shouldReturnBadRequestWhenCreatingReservationWithStartDateInThePast() throws Exception {
        ReservationDto postReservationDto = ReservationDto.builder().username("test")
                .numberOfPeople(3).startDate(LocalDate.of(2018, 8, 1))
                .endDate(LocalDate.of(2023, 9, 1)).build();

        doReturn(mockReservation).when(reservationService).save(any());

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(postReservationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /reservations - Bad Request - end date in the past")
    void shouldReturnBadRequestWhenCreatingReservationWithEndDateInThePast() throws Exception {
        ReservationDto postReservationDto = ReservationDto.builder().username("test")
                .numberOfPeople(3).startDate(LocalDate.of(2023, 8, 1))
                .endDate(LocalDate.of(2018, 9, 1)).build();

        doReturn(mockReservation).when(reservationService).save(any());

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(postReservationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /reservations - Bad Request - end date before start date")
    void shouldReturnBadRequestWhenCreatingReservationWithEndDateBeforeStartDate() throws Exception {
        ReservationDto postReservationDto = ReservationDto.builder().username("test")
                .numberOfPeople(3).startDate(LocalDate.of(2024, 9, 1))
                .endDate(LocalDate.of(2024, 8, 1)).build();

        doReturn(mockReservation).when(reservationService).save(any());

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(postReservationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("PUT /reservations/1 - Success")
    void shouldUpdateReservation() throws Exception {
        ReservationDto putReservationDto = ReservationDto.builder().username("test")
                .numberOfPeople(3).startDate(LocalDate.of(2020, 8, 1))
                .endDate(LocalDate.of(2020, 9, 1))
                .roomId(1L).build();
        doReturn(Optional.of(mockReservation)).when(reservationService).findById(1L);
        doReturn(Optional.of(mockReservation)).when(reservationService).update(any(), any());

        mockMvc.perform(put("/reservations/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(putReservationDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, "/reservations/1"))
                .andExpect(jsonPath("$.id", is(mockReservation.getId().intValue())))
                .andExpect(jsonPath("$.username", is(mockReservation.getUsername())))
                .andExpect(jsonPath("$.numberOfPeople", is(mockReservation.getNumberOfPeople())))
                .andExpect(jsonPath("$.startDate", is(mockReservation.getStartDate().toString())))
                .andExpect(jsonPath("$.endDate", is(mockReservation.getEndDate().toString())))
                .andExpect(jsonPath("$.roomId", is(mockReservation.getRoom().getId().intValue())));
    }

    @Test
    @DisplayName("PUT /reservations/1 - Bad Request - not available room")
    void shouldReturnBadRequestWhenNoAvailableRoomForUpdatedReservation() throws Exception {
        ReservationDto putReservationDto = ReservationDto.builder().id(1L).username("test")
                .numberOfPeople(5).startDate(LocalDate.of(2020, 8, 1))
                .endDate(LocalDate.of(2020, 9, 1))
                .roomId(1L).build();

        doThrow(new AvailableRoomNotFoundException()).when(reservationService).save(any());

        mockMvc.perform(put("/reservations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(putReservationDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /reservations/1 - Bad Request - start date in the past")
    void shouldReturnBadRequestWhenUpdatingReservationWithStartDateInThePast() throws Exception {
        ReservationDto putReservationDto = ReservationDto.builder().username("test")
                .numberOfPeople(3).startDate(LocalDate.of(2018, 8, 1))
                .endDate(LocalDate.of(2023, 9, 1))
                .roomId(1L).build();
        doReturn(Optional.of(mockReservation)).when(reservationService).findById(1L);
        doReturn(Optional.of(mockReservation)).when(reservationService).update(any(), any());

        mockMvc.perform(put("/reservations/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(putReservationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("PUT /reservations/1 - Bad Request - end date in the past")
    void shouldReturnBadRequestWhenUpdatingReservationWithEndDateInThePast() throws Exception {
        ReservationDto putReservationDto = ReservationDto.builder().username("test")
                .numberOfPeople(3).startDate(LocalDate.of(2023, 8, 1))
                .endDate(LocalDate.of(2018, 9, 1))
                .roomId(1L).build();
        doReturn(Optional.of(mockReservation)).when(reservationService).findById(1L);
        doReturn(Optional.of(mockReservation)).when(reservationService).update(any(), any());

        mockMvc.perform(put("/reservations/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(putReservationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("PUT /reservations/1 - Bad Request - end date before start")
    void shouldReturnBadRequestWhenUpdatingReservationWithEndDateBeforeStartDate() throws Exception {
        ReservationDto putReservationDto = ReservationDto.builder().username("test")
                .numberOfPeople(3).startDate(LocalDate.of(2024, 9, 1))
                .endDate(LocalDate.of(2024, 8, 1))
                .roomId(1L).build();
        doReturn(Optional.of(mockReservation)).when(reservationService).findById(1L);
        doReturn(Optional.of(mockReservation)).when(reservationService).update(any(), any());

        mockMvc.perform(put("/reservations/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(putReservationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("PUT /reservations/1 - Not Found")
    void shouldNotUpdateReservationWhenNotExist() throws Exception {
        ReservationDto putReservationDto = ReservationDto.builder().username("test")
                .numberOfPeople(3).startDate(LocalDate.of(2020, 8, 1))
                .endDate(LocalDate.of(2020, 9, 1))
                .roomId(1L).build();
        doReturn(Optional.empty()).when(reservationService).findById(1L);

        mockMvc.perform(put("/reservations/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(putReservationDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /reservations/1 - Success")
    void shouldDeleteReservation() throws Exception {
        mockMvc.perform(delete("/reservations/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /reservations?roomNumber=5 - Success - empty list")
    void shouldReturnEmptyListWhenNoReservationsForRoom() throws Exception {
        doReturn(Collections.emptyList()).when(reservationService).findForRoom(5L);

        mockMvc.perform(get("/reservations?roomNumber={roomNumber}", 5))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /reservations?roomNumber=1 - Success")
    void shouldReturnReservationsForRoom() throws Exception {
        Reservation reservation2 = Reservation.builder().id(2L).username("tester")
                .numberOfPeople(3).startDate(LocalDate.of(2020, 7, 1))
                .endDate(LocalDate.of(2020, 4, 7))
                .room(mockRoom).build();

        doReturn(Arrays.asList(mockReservation, reservation2)).when(reservationService).findForRoom(1L);

        mockMvc.perform(get("/reservations?roomNumber={roomNumber}", 1l))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(mockReservation.getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(mockReservation.getUsername())))
                .andExpect(jsonPath("$[0].numberOfPeople", is(mockReservation.getNumberOfPeople())))
                .andExpect(jsonPath("$[0].startDate", is(mockReservation.getStartDate().toString())))
                .andExpect(jsonPath("$[0].endDate", is(mockReservation.getEndDate().toString())))
                .andExpect(jsonPath("$[0].roomId", is(mockReservation.getRoom().getId().intValue())));
    }
}
