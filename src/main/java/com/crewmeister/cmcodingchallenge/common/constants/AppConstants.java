package com.crewmeister.cmcodingchallenge.common.constants;

import java.time.format.DateTimeFormatter;

public class AppConstants {
    public static final String BASE_API = "/api/v1";

    public static final String EXCHANGE_RATE = BASE_API + "/exchange-rates";

    public static final int PRECISION = 4;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static final String FORMAT_PARAM = "format";

    public static final String DETAIL_PARAM = "detail";

    public static final String START_PERIOD_PARAM = "startPeriod";

    public static final String END_PERIOD_PARAM = "endPeriod";
}
