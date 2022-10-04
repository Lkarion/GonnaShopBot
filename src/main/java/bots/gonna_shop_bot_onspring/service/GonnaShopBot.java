package bots.gonna_shop_bot_onspring.service;

import bots.gonna_shop_bot_onspring.config.BotConfig;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

@Component
public class GonnaShopBot extends TelegramLongPollingBot {
    @Autowired
    private BotConfig config;

    @Autowired
    private CommandHandler commandHandler;

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallBackQuery(update.getMessage());
        } else {
            handleMessage(update.getMessage());
        }
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        if (message.hasText()) {
            if (message.hasEntities()) {
                Optional<MessageEntity> commandEntity =
                        message.getEntities().stream()
                                .filter(e -> "bot_command".equals(e.getType())).findFirst();
                if (commandEntity.isPresent()) {
                    String command = message.getText()
                            .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                    execute(commandHandler.handleCommand(message, command));
                }
                } else {
                execute(commandHandler.handleText(message));
            }
        }
    }

    @SneakyThrows
    private void handleCallBackQuery(Message message) {
    }
}