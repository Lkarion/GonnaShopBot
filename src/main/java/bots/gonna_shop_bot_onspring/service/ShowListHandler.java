package bots.gonna_shop_bot_onspring.service;

import bots.gonna_shop_bot_onspring.repository.ProductRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ShowListHandler {

    @Autowired
    private ProductRepository productRepository;

    @SneakyThrows
    public SendMessage showList(Message message) {

        var shoppingList = productRepository.findAllByChatId(message.getChatId());

        if (shoppingList.isEmpty()) {
            return SendMessage.builder()
                    .text("Your shopping list is empty")
                    .chatId(message.getChatId())
                    .build();
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        int i = 0;
        while (i < shoppingList.size()) {
            String p;
            if (i == shoppingList.size() - 1) {
                p = " ";
            } else {
                p = shoppingList.get(i + 1).getName();
            }
            buttons.add(
                    Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text(shoppingList.get(i).getName())
                                    .callbackData(shoppingList.get(i).getName())
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text(p)
                                    .callbackData(p)
                                    .build())
            );
            i += 2;
        }
        return SendMessage.builder()
                .text("Your List:")
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build();

    }
}
