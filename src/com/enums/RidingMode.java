package com.enums;

import java.util.Arrays;
import java.util.Optional;

public enum RidingMode
{
    PARK("PARK", 0x00),
    ECO("ECO", 0x01),
    TOUR("TOUR", 0x02),
    SPORT("SPORT", 0x03),
    BOOST("BOOST", 0x04),
    REVERSE("REVERSE", 0x05),
    LIMPHOME("LIMPHOME", 0x06),
    Hyper("Hyper", 0x07),
    REGENERATION("REGENERATION", 0x08),
    DERATE("DERATE", 0x09),
    GH3("GH3", 0x0A),
    BOOST2("BOOST2", 0x0B),

    FAULT("FAULT", -1);

    private final String fullName;
    private final int code;

    RidingMode(String fullName, int code) {
        this.fullName = fullName;
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public int getCode() {
        return code;
    }

    public static Optional<RidingMode> getRidingModeByValue(String value) {
        return Arrays.stream(RidingMode.values())
                .filter(accStatus -> accStatus.fullName.equalsIgnoreCase(value) )
                .findFirst();
    }

    public static Optional<RidingMode> getRidingModeByValue(int value) {
        for(RidingMode ridingMode: RidingMode.values()){
            if( ridingMode.code == value ) return Optional.of(ridingMode);
        }
        return Optional.of(RidingMode.valueOf("Fault"));
    }
}

