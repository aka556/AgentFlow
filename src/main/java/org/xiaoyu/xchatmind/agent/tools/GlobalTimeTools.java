package org.xiaoyu.xchatmind.agent.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class GlobalTimeTools implements Tool {
    @Override
    public String getName() {
        return "globalTimeTool";
    }

    @Override
    public String getDescription() {
        return "提供全球时间查询工具，用于提供不同地区的时间信息";
    }

    @Override
    public ToolType getType() {
        return ToolType.FIXED;
    }

    @org.springframework.ai.tool.annotation.Tool(
            name = "globalTimeTool",
            description = "提供全球时间查询工具，用于提供不同地区的时间信息")
    public String globalTimeTool(String city) {
        String apiUrl =
                "https://uapis.cn/api/v1/misc/worldtime?city=" + city;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            // 提取事件信息等
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(sb.toString());

            String timezone = jsonNode.get("timezone").asText();
            String datetime = jsonNode.get("datetime").asText();
            String weekday = jsonNode.get("weekday").asText();

            return String.format(
                    "当前城市：%s\n时区：%s\n时间：%s\n星期：%s",
                    city, timezone, datetime, weekday
            );
        } catch (Exception e) {
            e.printStackTrace();
            return "获取时间信息失败，请检查城市名称是否正确";
        }
    }
}
