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
public class CityTool implements Tool {
    @Override
    public String getName() {
        return "cityTool";
    }

    @Override
    public String getDescription() {
        return "获取当前城市";
    }

    @Override
    public ToolType getType() {
        return ToolType.FIXED;
    }

    @org.springframework.ai.tool.annotation.Tool(name = "getCity", description = "获取当前的城市")
    public String getCity() {
        // 城市获取api
        String apiUrl = "http://ip-api.com/json";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(sb.toString());

            // 提取city字段
            return jsonNode.get("city").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
