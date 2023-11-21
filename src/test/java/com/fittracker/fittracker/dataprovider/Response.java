package com.fittracker.fittracker.dataprovider;

import com.fittracker.fittracker.response.LoginResponse;
import com.fittracker.fittracker.response.RegisterResponse;
import com.fittracker.fittracker.response.WeightResponse;
import java.time.LocalDate;
import java.util.UUID;

public class Response {

    public static RegisterResponse registerResponse() {
        return new RegisterResponse(UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"),
            "user",
            "user@example.com");
    }

    public static LoginResponse loginResponse() {
        return new LoginResponse("token");
    }

    public static WeightResponse weightResponse() {
        return new WeightResponse(LocalDate.of(2023, 10, 10), 100.1);
    }

    public static WeightResponse weightResponseWithValue(Double value) {
        return new WeightResponse(LocalDate.of(2023, 10, 10), value);
    }

    public static WeightResponse weightResponseWithDate(LocalDate date) {
        return new WeightResponse(date, 100.1);
    }


}
