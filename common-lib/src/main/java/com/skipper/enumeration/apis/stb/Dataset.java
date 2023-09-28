package com.skipper.enumeration.apis.stb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Dataset {
    Accommodation("accommodation"),
    Attractions("attractions"),
    BarsClubs("bars_clubs"),
    Cruises("cruises"),
    Events("events"),
    FoodBeverages("food_beverages"),
    Precincts("precints"),
    Shops("shops"),
    Tours("tours"),
    Venues("venues"),
    WalkingTrails("walking_trails");

    private final String value;
}
