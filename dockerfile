# Базовый образ с OpenJDK 23
FROM openjdk:23-jdk-slim

# Установка рабочей директории внутри контейнера
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY target/telegramBotHome-1.0-SNAPSHOT.jar uk_home.jar

# Указываем команду для запуска бота
CMD ["java", "-jar", "uk_home.jar"]
