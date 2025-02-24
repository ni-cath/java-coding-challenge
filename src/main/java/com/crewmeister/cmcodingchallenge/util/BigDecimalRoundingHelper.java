package com.crewmeister.cmcodingchallenge.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static com.crewmeister.cmcodingchallenge.constants.AppConstants.PRECISION;

public class BigDecimalRoundingHelper {
    public static BigDecimal round(BigDecimal num) {
        return num.setScale(PRECISION, RoundingMode.HALF_EVEN);
    }

    public static MathContext getMathContext() {
        return MathContext.DECIMAL128;
    }
}
