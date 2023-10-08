package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
            String searchQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
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

            for (JsonNode result : searchResults) {
                String pageID = result.get("pageid").asText();
                System.out.println(result.get("title") + ": " + "https://ru.wikipedia.org/w/index.php?curid=" + pageID);
            }
        } catch (IOException e){
            System.out.println("ошибка чтения текста");
        }
    }
}