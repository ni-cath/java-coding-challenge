package com.crewmeister.cmcodingchallenge.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigDecimalRoundingHelper {

    //todo: make configurable
    private static final int PRECISION = 4;

    public static BigDecimal round(BigDecimal num) {
        return num.setScale(PRECISION, RoundingMode.HALF_EVEN);
    }

    public static MathContext getMathContext() {
        return MathContext.DECIMAL128;
    }
}
