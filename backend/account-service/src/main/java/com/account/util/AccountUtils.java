package com.account.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountUtils {

    private final Random random = new Random();

    public String createMockIban() {
        String bankCode = "NEXS";
        String countryCode = "IT";
        String checkDigits = String.format("%02d", random.nextInt(100));
        StringBuilder bban = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            bban.append(random.nextInt(10));
        }
        return countryCode + checkDigits + bankCode + bban.toString().substring(0, 18);
    }

    public String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int block = random.nextInt(10000);
            cardNumber.append(String.format("%04d", block));
            if (i < 3) {
                cardNumber.append("-");
            }
        }
        return cardNumber.toString();
    }
}
