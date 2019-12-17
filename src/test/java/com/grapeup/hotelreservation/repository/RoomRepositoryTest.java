package com.grapeup.hotelreservation.repository;

import com.grapeup.hotelreservation.model.Room;
import com.grapeup.hotelreservation.model.RoomType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DataJpaTest
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Test
    public void shouldFindAllBasicTypeRooms() {
        List<Room> rooms = roomRepository.findRoomsWithCapacity(RoomType.BASIC);
        assertThat(rooms, hasSize(2));
    }

    @Test
    public void shouldNotFindSuiteRooms() {
        List<Room> rooms = roomRepository.findRoomsWithCapacity(RoomType.SUITE);
        assertThat(rooms, hasSize(0));
    }

    @Test
    public void shouldFindRoomById() {
        Optional<Room> room = roomRepository.findById(1L);

        assertThat(room.isPresent(), is(true));
        assertThat(room.get().getId(), is(1L));
        assertThat(room.get().getRoomType(), is(RoomType.PENTHOUSE));
        //assertThat(room.get().getRating(), is(RoomType.PENTHOUSE));
        assertThat(room.get().getReservations(), is(hasSize(2)));
    }

    @Test
    public void shouldNotFindRoomById() {
        Optional<Room> room = roomRepository.findById(99L);

        assertThat(room.isEmpty(), is(true));
    }
}
