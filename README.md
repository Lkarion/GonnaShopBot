# Description
This is the code for Telegram Bot https://t.me/gonna_shop_bot which allows you to make your own simple shopping list.

In menu there are three main functions:
- add (adds a typed product to the list)
- delete (deletes a typed product from the list)
- show (shows the entire shopping list)

### Stack:
- Telegram Bot API
- PostgreSQL
- Spring

# Structure
#### Service
1. GonnaShopBot Service, which receives and handling updates
2. CommandHandler Service, which handles all menu commands and list changing
3. ShowListHandler Service, which handles list showing
#### Entity and its Repository
1. Product + ProductRepository
#### Configuration
1. BotConfig class reads a unique bot **name** and bot **token** from the application.properties file
2. BotInitializer Service initializes the GonnaShopBot by launching
3. BotState declares all possible GonnaShopBot states