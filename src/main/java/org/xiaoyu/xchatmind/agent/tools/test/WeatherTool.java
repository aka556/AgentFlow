package org.xiaoyu.xchatmind.agent.tools.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.xiaoyu.xchatmind.agent.tools.Tool;
import org.xiaoyu.xchatmind.agent.tools.ToolType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class WeatherTool implements Tool {
    @Override
    public String getName() {
        return "weatherTool";
    }

    @Override
    public String getDescription() {
        return "获取天气";
    }

    @Override
    public ToolType getType() {
        return ToolType.FIXED;
    }

    @org.springframework.ai.tool.annotation.Tool(name = "weather", description = "获取天气")
    public String getWeather(String city, String date) {
        // 天气api
        String apiUrl = "https://uapis.cn//api/v1/misc/weather?city=" + city + "&extended=true";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(stringBuilder.toString());

            // 返回一些关键天气信息
            String cityName = root.get("city").asText();
            String weather = root.get("weather").asText();
            int temperature = root.get("temperature").asInt();
            int humidity = root.get("humidity").asInt();
            String windDirection = root.get("wind_direction").asText();
            String windPower = root.get("wind_power").asText();

            return String.format(
                    "%s %s 的天气查询结果：%s，温度 %d°C，湿度 %d%%，%s %s 级",
                    cityName,
                    date,
                    weather,
                    temperature,
                    humidity,
                    windDirection,
                    windPower
            );
        } catch (Exception e) {
            e.printStackTrace();
            return city + date + " 的天气查询结果失败";
        }
    }
}
