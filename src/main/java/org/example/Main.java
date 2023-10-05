package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Начните вводить запросы, например \"лэти\"");
        Scanner sc = new Scanner(System.in);
        // Пишем запросы в консоль и в браузере открываются странички википедии. Нажми ctrl+D чтобы закончить ввод.
        while (sc.hasNextLine()){
            searchResults(sc.nextLine());
        }
    }

    private static void searchResults(String query) {
        try {
            // Создаем URL-объект со ссылкой на API википедии
            String searchQuery = URLEncoder.encode(query, "UTF-8");
            URL url = new URL("https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=" + searchQuery);

            // Открываем HttpURLConnection для получения JSON-ответа
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Получаем ответ в виде JSON
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Парсим JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.toString());

            // Извлекаем список результатов
            JsonNode searchResults = jsonNode.get("query").get("search");

            // Открываем каждый результат в браузере
            for (JsonNode result : searchResults) {
                String pageID = result.get("pageid").asText();
                openPageInBrowser(pageID);
            }
        } catch (IOException e){
            System.out.println("ошибка чтения текста");
        }
    }

    private static void openPageInBrowser(String pageID) throws IOException {
        String wikipediaURL = "https://ru.wikipedia.org/w/index.php?curid=" + pageID;
        URI uri = URI.create(wikipediaURL);

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(uri);
        }
    }
}