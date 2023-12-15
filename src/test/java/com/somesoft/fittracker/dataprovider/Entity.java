package com.somesoft.fittracker.dataprovider;

import com.somesoft.fittracker.entity.Product;
import com.somesoft.fittracker.entity.User;
import com.somesoft.fittracker.entity.UserDetails;
import com.somesoft.fittracker.entity.Weight;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public static Product product() {
        return new Product(UUID.fromString("382cf280-8b7a-11ee-b9d1-0242ac120002"), 0, "bread", 245, 58, 8, 0,
            UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"),
            LocalDateTime.of(2023, 10, 10, 4, 20), true);
    }


    public static Product productWithName(String name) {
        return new Product(UUID.fromString("382cf280-8b7a-11ee-b9d1-0242ac120002"), 0, name, 245, 58, 8, 0,
            UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"),
            LocalDateTime.of(2023, 10, 10, 4, 20), true);
    }


    public static Product productWithKcal(int kcal) {
        return new Product(UUID.fromString("382cf280-8b7a-11ee-b9d1-0242ac120002"), 0, "bread", kcal, 58, 8, 0,
            UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"),
            LocalDateTime.of(2023, 10, 10, 4, 20), true);
    }

    public static Product productWithCarbs(int carbs) {
        return new Product(UUID.fromString("382cf280-8b7a-11ee-b9d1-0242ac120002"), 0, "bread", 245, carbs, 8, 0,
            UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"),
            LocalDateTime.of(2023, 10, 10, 4, 20), true);
    }

    public static Product productWithProtein(int protein) {
        return new Product(UUID.fromString("382cf280-8b7a-11ee-b9d1-0242ac120002"), 0, "bread", 245, 58, protein, 0,
            UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"),
            LocalDateTime.of(2023, 10, 10, 4, 20), true);
    }

    public static Product productWithFat(int fat) {
        return new Product(UUID.fromString("382cf280-8b7a-11ee-b9d1-0242ac120002"), 0, "bread", 245, 58, 8, fat,
            UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"),
            LocalDateTime.of(2023, 10, 10, 4, 20), true);
    }


    public static Product productWithActive(boolean active) {
        return new Product(UUID.fromString("382cf280-8b7a-11ee-b9d1-0242ac120002"), 0, "bread", 245, 58, 8, 0,
            UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"),
            LocalDateTime.of(2023, 10, 10, 4, 20), active);
    }
}
