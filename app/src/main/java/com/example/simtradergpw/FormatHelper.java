package com.example.simtradergpw;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public final class FormatHelper {
    public static String doubleToTwoDecimal(Double number) {
        // Format Double to two decimal places
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(number);
    }

    public static String doubleToFourDecimal(Double number) {
        // Format Double to two decimal places
        DecimalFormat df = new DecimalFormat("0.0000");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(number);
    }

    public static String cutAfterDot(Double number) {
        DecimalFormat df = new DecimalFormat("0");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(number);
    }

    public static String doubleToPercent(Double decimal) {
        decimal = decimal * 100;
        return decimal.toString() + "%";
    }
}
