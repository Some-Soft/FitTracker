package com.fittracker.fittracker.dataprovider;

import com.fittracker.fittracker.entity.User;
import com.fittracker.fittracker.entity.UserDetails;
import com.fittracker.fittracker.entity.Weight;
import java.time.LocalDate;
import java.util.UUID;

public class Entity {

    public static User user() {
        return new User(UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"), "user", "user@example.com",
            "$2a$10$2gvLjc6wUEgM42M73tQ9ieI2jrAwfxap3X7XsEt//swQvJXyMpVJ6");
    }

    public static User userWithPassword(String password) {
        return new User(UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"), "user", "user@example.com", password);
    }

    public static UserDetails userDetails() {
        return new UserDetails(UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"), "user",
            "$2a$10$2gvLjc6wUEgM42M73tQ9ieI2jrAwfxap3X7XsEt//swQvJXyMpVJ6");
    }

    public static Weight weight() {
        return new Weight(LocalDate.of(2023, 10, 10), 100.1, UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"));
    }

    public static Weight weightWithValue(Double value) {
        return new Weight(LocalDate.of(2023, 10, 10), value, UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"));
    }

    public static Weight weightWithDate(LocalDate date) {
        return new Weight(date, 100.1, UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"));
    }

}
