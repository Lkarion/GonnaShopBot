package bots.gonna_shop_bot_onspring.service;

import bots.gonna_shop_bot_onspring.config.BotState;
import bots.gonna_shop_bot_onspring.entity.Product;
import bots.gonna_shop_bot_onspring.repository.ProductRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Component
public class CommandHandler {
    @Autowired
    private ProductRepository productRepository;
    private final Map<Long, BotState> chatStateMap = new HashMap<>();
    private final Map<BotState, String> stateSendMessageMap = new HashMap<>();


    public CommandHandler() {
        stateSendMessageMap.put(BotState.GONNA_ADD, "Please type an option you would like to add to your shopping list");
        stateSendMessageMap.put(BotState.GONNA_DELETE, "Please type an option you would like to delete from your shopping list");
    }


    public SendMessage handleCommand(Message message, String command) {
        switch (command) {
            case "/add": {
                return handleListChanging(message, BotState.GONNA_ADD);
            }
            case "/delete": {
                return handleListChanging(message, BotState.GONNA_DELETE);
            }
            case "/show": {
                return showList(message);
            }
            default:
                return generateAnswer("Choose any command", message.getChatId());
        }

    }

    public SendMessage handleText(Message message) {
        if (chatStateMap.get(message.getChatId()).equals(BotState.GONNA_ADD)) {
            return addProductToList(message);
        } else if (chatStateMap.get(message.getChatId()).equals(BotState.GONNA_DELETE)) {
            return deleteProductFromList(message);
        } else {
            return generateAnswer("Unknown command", message.getChatId());
        }
    }


    @SneakyThrows
    public SendMessage handleListChanging(Message message, BotState botState) {
        Long chatId = message.getChatId();
        if (chatStateMap.containsKey(chatId)) {
            chatStateMap.replace(chatId, botState);
        } else {
            chatStateMap.put(chatId, botState);
        }
        return SendMessage.builder()
                .chatId(chatId)
                .text(stateSendMessageMap.get(botState))
                .build();
    }


    @SneakyThrows
    public SendMessage addProductToList(Message message) {
        Long chatId = message.getChatId();
        chatStateMap.replace(chatId, BotState.NORMAL);
        String productName = message.getText().trim().toLowerCase(Locale.ROOT);
        if (!productRepository.existsByName(productName)) {
            var product = Product.builder()
                    .chatId(message.getChatId())
                    .name(productName)
                    .isMarked(false)
                    .build();
            productRepository.save(product);
            System.out.println(productName + " is added to your list");
            return generateAnswer(productName + " is added to your shopping list", message.getChatId());
        } else {
            return generateAnswer(productName + " already exists in your shopping list", message.getChatId());
        }
    }

    @SneakyThrows
    public SendMessage deleteProductFromList(Message message) {
        Long chatId = message.getChatId();
        chatStateMap.replace(chatId, BotState.NORMAL);
        String productName = message.getText().trim().toLowerCase(Locale.ROOT);
        Product product = productRepository.findByName(productName);
        if (product != null) {
            productRepository.delete(product);
            System.out.println("Product has been deleted from your shopping list");
            return generateAnswer("Product has been deleted from your shopping list", message.getChatId());
        }
        return generateAnswer("Product was not found in your shopping list", message.getChatId());
    }

    @SneakyThrows
    public SendMessage showList(Message message) {

        var shoppingList = productRepository.findAllByChatId(message.getChatId());

        if (shoppingList.isEmpty()) {
            return generateAnswer("Your shopping list is empty", message.getChatId());
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        shoppingList.forEach(System.out::println);

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

    private SendMessage generateAnswer(String text, Long chatId) {
        return SendMessage.builder()
                .text(text)
                .chatId(chatId.toString())
                .build();
    }
}