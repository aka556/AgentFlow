package org.xiaoyu.xchatmind.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 移除原有的转换器配置，添加更精确的配置
        // 添加 MappingJackson2HttpMessageConverter 并确保它支持所有必要的媒体类型
        for (int i = converters.size() - 1; i >= 0; i--) {
            HttpMessageConverter<?> converter = converters.get(i);
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jacksonConverter = (MappingJackson2HttpMessageConverter) converter;
                List<MediaType> supportedMediaTypes = jacksonConverter.getSupportedMediaTypes();
                if (!supportedMediaTypes.contains(MediaType.TEXT_EVENT_STREAM)) {
                    supportedMediaTypes = new java.util.ArrayList<>(supportedMediaTypes);
                    supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
                    jacksonConverter.setSupportedMediaTypes(supportedMediaTypes);
                }
            }
        }
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 设置异步请求的默认超时时间为60分钟（3600000毫秒）
        // 对于SSE流等长时间运行的操作，这很重要
        // 这个值应该大于SseEmitter创建时设置的超时时间（30分钟）
        configurer.setDefaultTimeout(3600000L); // 60分钟
    }
}