package com.account.factory;

import com.account.entity.Card;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Component
public class ConcreteCardFactory implements CardFactory {

    private final Random random = new Random();

    private String generateCardNumber() {
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

    @Override
    public Card create(Long accountId){
        Card card = new Card();
        card.setAccountId(accountId);
        card.setCardNumber(generateCardNumber());
        card.setExpiration(LocalDate.now().plusYears(5));
        return card;
    }
}
