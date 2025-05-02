package com.service.powercrm.util;

import java.math.BigDecimal;

public class MonetaryConverter {

    private MonetaryConverter() {
    }

    public static BigDecimal convertFromMonetaryFormat(String value) {
        if (value == null || value.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            value = value.replace("R$", "").trim();
            value = value.replace(".", "");
            value = value.replace(",", ".");

            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Erro ao converter valor monetário: " + value, e);
        }
    }
}
