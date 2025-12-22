package org.example.payment_service_app.util;

import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;

@UtilityClass
public class TimeUtil {

    public static OffsetDateTime getNow() {
        return OffsetDateTime.now();
    }
}
