package com.clx.ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;

public class DateTimeTools {

    // 定义一个工具，和普通方法没有什么区别，仅多了一个 @Tool 注解
    // 注意，description 是工具的描述信息，要编写准确清晰，便于AI准确发现
    @Tool(description = "获取用户所在时区的当前日期和时间")
    public String getCurrentDateTime() {
        System.out.println("->> getCurrentDateTime() 获取用户所在时区的当前日期和时间");
//        LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId());
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    @Tool(description = "获取指定位置天气")
    public String getWeather(@ToolParam(description = "经度") double longitude,
                             @ToolParam(description = "纬度") double latitude) {
        System.out.println("->> getWeather() 获取指定位置天气");
        return "天晴";
    }
}
