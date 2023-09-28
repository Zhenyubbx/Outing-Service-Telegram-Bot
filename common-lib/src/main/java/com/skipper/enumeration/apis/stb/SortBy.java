package com.skipper.enumeration.apis.stb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortBy {
    Location("location"),
    Name("name"),
    Rating("rating"),
    Source("source"),
    LastUpdatedDate("lastupdateddate ");

    private final String value;
}
