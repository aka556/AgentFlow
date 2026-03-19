package org.xiaoyu.xchatmind.agent.tools;

import com.google.gson.*;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class HotSearchListTools implements Tool {
    @Override
    public String getName() {
        return "hotSearchListTool";
    }

    @Override
    public String getDescription() {
        return "用于查找各平台热搜榜单信息";
    }

    @Override
    public ToolType getType() {
        return ToolType.OPERATIONAL;
    }

    @org.springframework.ai.tool.annotation.Tool(
            name = "getHotSearchList",
            description = "用于查找各平台热搜榜单信息")
    public String getHotSearchList(String platForm) {
        String apiUrl = "https://uapis.cn/api/v1/misc/hotboard?type=" + platForm;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            // 解析并简化JSON数据
            return simplifyHotSearchData(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "获取数据失败";
        }
    }

    /**
     * 简化热榜数据，只保留重要字段
     */
    private String simplifyHotSearchData(String originalJson) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = JsonParser.parseString(originalJson).getAsJsonObject();

            // 创建简化的JSON结构
            JsonObject simplified = new JsonObject();

            // 保留顶层字段
            if (jsonObject.has("type")) {
                simplified.addProperty("type", jsonObject.get("type").getAsString());
            }
            if (jsonObject.has("update_time")) {
                simplified.addProperty("update_time", jsonObject.get("update_time").getAsString());
            }

            // 处理列表数据
            if (jsonObject.has("list") && jsonObject.get("list").isJsonArray()) {
                JsonArray originalList = jsonObject.getAsJsonArray("list");
                JsonArray simplifiedList = new JsonArray();

                for (JsonElement element : originalList) {
                    if (element.isJsonObject()) {
                        JsonObject item = element.getAsJsonObject();
                        JsonObject simplifiedItem = new JsonObject();

                        // 保留重要字段
                        if (item.has("index")) {
                            simplifiedItem.addProperty("rank", item.get("index").getAsInt());
                        }
                        if (item.has("title")) {
                            simplifiedItem.addProperty("title", item.get("title").getAsString());
                        }
                        if (item.has("url")) {
                            simplifiedItem.addProperty("url", item.get("url").getAsString());
                        }
                        if (item.has("hot_value")) {
                            simplifiedItem.addProperty("hot_value", item.get("hot_value").getAsString());
                        }

                        // 从extra中提取重要信息
                        if (item.has("extra") && item.get("extra").isJsonObject()) {
                            JsonObject extra = item.getAsJsonObject("extra");
                            JsonObject simplifiedExtra = new JsonObject();

                            if (extra.has("tname")) {
                                simplifiedExtra.addProperty("category", extra.get("tname").getAsString());
                            }
                            if (extra.has("owner")) {
                                JsonObject owner = extra.getAsJsonObject("owner");
                                if (owner.has("name")) {
                                    simplifiedExtra.addProperty("author", owner.get("name").getAsString());
                                }
                            }
                            if (extra.has("stat")) {
                                JsonObject stat = extra.getAsJsonObject("stat");
                                JsonObject simplifiedStat = new JsonObject();

                                if (stat.has("view")) {
                                    simplifiedStat.addProperty("views", stat.get("view").getAsLong());
                                }
                                if (stat.has("like")) {
                                    simplifiedStat.addProperty("likes", stat.get("like").getAsLong());
                                }
                                if (stat.has("coin")) {
                                    simplifiedStat.addProperty("coins", stat.get("coin").getAsLong());
                                }
                                if (stat.has("share")) {
                                    simplifiedStat.addProperty("shares", stat.get("share").getAsLong());
                                }
                                if (stat.has("danmaku")) {
                                    simplifiedStat.addProperty("comments", stat.get("danmaku").getAsLong());
                                }

                                simplifiedExtra.add("stats", simplifiedStat);
                            }

                            simplifiedItem.add("extra", simplifiedExtra);
                        }

                        simplifiedList.add(simplifiedItem);
                    }
                }

                simplified.add("list", simplifiedList);
                simplified.addProperty("total", simplifiedList.size());
            }

            return gson.toJson(simplified);

        } catch (Exception e) {
            e.printStackTrace();
            // 如果解析失败，返回原始数据的前500个字符
            return originalJson.length() > 500 ? originalJson.substring(0, 500) + "..." : originalJson;
        }
    }
}
