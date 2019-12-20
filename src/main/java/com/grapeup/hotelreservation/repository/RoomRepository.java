package com.grapeup.hotelreservation.repository;

import com.grapeup.hotelreservation.model.Room;
import com.grapeup.hotelreservation.model.RoomType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
    //JPA find by
    @Query("SELECT r FROM Room r WHERE r.roomType = :roomType")
    List<Room> findRoomsWithCapacity(@Param("roomType") RoomType roomType);
}
