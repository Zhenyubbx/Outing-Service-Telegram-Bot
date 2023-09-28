package com.skipper.enumeration.apis.gmaps;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Mode {
    Driving("driving"),
    Walking("walking"),
    Bicycling("bicycling"),
    Transit("transit");

    private final String value;
}
