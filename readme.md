# Select Bot
Бот для голосования в компании

# Правила
- каждый имеет 10 голосов
- можно предлагать вариант, просто отправив его название боту
- командой /get можно вывести все варианты и проголосовать

# Пример работы
![Пример](https://github.com/UserNameMax/voitBot/blob/main/assets/Screenshot%202025-01-26%20at%2020.13.02.png?raw=true)

# local.properties
- yandex.registry.url - url для выгрузки docker image
- yandex.oauth.token - яндекс токен, можно получить [здесь](https://yandex.cloud/ru/docs/container-registry/operations/authentication)

# Env
- BOT_TOKEN - токена бота от BotFather
- CONNECTION_STRING - connection string для подключения к YDB
- AUTH_KEY_JSON - авторизационный ключ yandex. [Инструкция для получения](https://yandex.cloud/ru/docs/iam/operations/authorized-key/create)
- WEBHOOK_URL - url для хука бота