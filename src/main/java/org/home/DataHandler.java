package org.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DataHandler {

  private static final Logger logger = LoggerFactory.getLogger(DataHandler.class);

  private static final Map<String, String> dataMap = new HashMap<>();
  private static final Map<String, String> imageMap = new HashMap<>();

  static {

    String projectRoot = System.getProperty("user.dir");
    imageMap.put("cam1", Paths.get(projectRoot, "images", "cam1.jpg").toString());
    dataMap.put("cam1","Изображение с камеры 1");

    imageMap.put("cam2", Paths.get(projectRoot, "images", "cam2.jpg").toString());
    dataMap.put("cam2","Изображение с камеры 2");

    imageMap.put("cam3", Paths.get(projectRoot, "images", "cam3.jpg").toString());
    dataMap.put("cam3","Изображение с камеры 3");

    imageMap.put("cam4", Paths.get(projectRoot, "images", "cam4.jpg").toString());
    dataMap.put("cam4","Изображение с камеры 4");

    imageMap.put("cam5", Paths.get(projectRoot, "images", "cam5.jpg").toString());
    dataMap.put("cam5","Изображение с камеры 5");


    dataMap.put(
        "uk",
        """
            * Диспетчер УК Сити Дом (с 8-30 до 17-30): 248-02-09
            * Аварийная служба УК Сити Дом (с 17-30 до 8-30): 291-93-10, 226-69-50
            * Специалист: 8 909 731 78 96
            * Сантехник Илья: 8 902 805 74 02
            * Электрик Александр Сергеевич: 8 902 792 62 70
            """);

    dataMap.put(
        "hvs",
        """
            * ХВС, водоотведение /ООО «Юг-Сервис»
              Адрес: п.Ферма, ул.Некрасова д.11 кв.3
              Телефон: 230-96-02
              Режим работы:
              Пн-чт: 08-00 – 17-00; Пт: 08-00 – 16-00 (перерыв 12-00 – 13-00)

            * Показания ИПУ передавать с 15 до 22 числа каждого месяца:
              - Автоответчик: 8 922 310 69 87
              - Вопросы по начислениям: 294-60-07
              Режим работы: Пн-чт 8-00 до 17-00 (перерыв 12-00 до 13-00); Пт 8-00 до 16-00
            """);

    dataMap.put(
        "gvs",
        """
            * ГВС, тепло | ООО «ПОТОК»
              Адрес: г.Пермь, ул.Героев Хасана, 98, 4-й этаж
              Телефоны: 233-93-68, 233-93-67

            * Показания ИПУ ГВС передавать с 15 по 25 число:
              - Телефоны: 8(342)233-93-68; 233-93-72
              - Электронная почта: rccperm@yandex.ru
              - Вайбер: 8 912 98 00 750
              - Через личный кабинет
            """);

    dataMap.put(
        "energy",
        """
    * Электроэнергия | ПАО «Пермэнергосбыт» *
    Адрес: с.Фролы, ул.Весенняя, д.4, офис 3, 1 этаж
    Телефон: 8 (342) 297-80-23
    Режим работы:
    Пн-Пт: 08-00 – 18-00 (перерыв с 13-00 до 14-00)

    Показания ИПУ передавать одним из удобных способов с 20 по 25 число каждого месяца.
    """);

    dataMap.put(
        "trash",
        """
    * Региональный оператор ТКО *
    Контакты: 8 (342) 236-90-55 (многоканальный телефон)

    Обращение можно направить на официальную почту:
    АО «ПРО ТКО»: info@te-perm.ru

    По вопросам начислений и оплаты:
    Обращаться в офис ОАО "КРЦ-Прикамье" по телефону (342) 258-44-84.
    """);

    dataMap.put(
        "ant",
        """
    * Коллективная антенна | ООО АНТ *
    Телефон: 277-03-73
    Режим работы: с 9-00 до 17-00
    """);

    dataMap.put(
        "administration",
        """           
            В разработке!
            """);

    dataMap.put(
        "advanced_search",
        """
            🔍 *Поиск по таблице:*\n
            Позволяет выполнять быстрый поиск информации по следующим критериям:\n
            - Номер телефона.\n
            - Номер квартиры.\n
            - Номер автомобиля.\n
            Введите ключевые слова, чтобы начать поиск!
            В разработке!
            """);

    logger.info("DataHandler инициализирован с {} текстовыми данными и {} изображениями.",
            dataMap.size(), imageMap.size());
  }

  public static String getResponse(String callbackData) {
    String response = dataMap.getOrDefault(callbackData, "Неизвестная команда.");
    logger.info("Получен запрос на текстовые данные: callbackData={}, response={}", callbackData, response);
    return response;
  }

  public static String getImage(String callbackData) {
    String imagePath = imageMap.get(callbackData);
    if (imagePath != null && Files.exists(Paths.get(imagePath))) {
      logger.info("Изображение найдено: callbackData={}, imagePath={}", callbackData, imagePath);
      return imagePath;
    } else {
      logger.warn("Изображение не найдено: callbackData={}", callbackData);
      return null;
    }
  }
}
