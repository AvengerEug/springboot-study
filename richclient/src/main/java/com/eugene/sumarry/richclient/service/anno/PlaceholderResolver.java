package com.eugene.sumarry.richclient.service.anno;

import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Map;

/**
 * @author muyang
 */
public class PlaceholderResolver {

    private static final PropertyPlaceholderHelper defaultHelper = new PropertyPlaceholderHelper("${", "}");
    private static PlaceholderResolver defaultResolver = new PlaceholderResolver();

    private PropertyPlaceholderHelper propertyPlaceholderHelper;


    private PlaceholderResolver() {
        propertyPlaceholderHelper = defaultHelper;
    }

    private PlaceholderResolver(String placeholderPrefix, String placeholderSuffix) {
        propertyPlaceholderHelper = new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix);
    }

    /**
     * 获取默认的占位符解析器，即占位符前缀为"${", 后缀为"}"
     *
     * @return
     */
    public static PlaceholderResolver getDefaultResolver() {
        return defaultResolver;
    }

    public static PlaceholderResolver getResolver(String placeholderPrefix, String placeholderSuffix) {
        return new PlaceholderResolver(placeholderPrefix, placeholderSuffix);
    }

    /**
     * 解析带有指定占位符的模板字符串
     * 如：content =  ${0}今年${1}岁<br/>
     * values = {"xiaoming", "18"}<br/>
     * result 小明今年18岁<br/>
     *
     * @param content 要解析的带有占位符的模板字符串
     * @param values  按照模板占位符索引位置设置对应的值
     * @return
     */
    public String resolve(String content, String... values) {
        return propertyPlaceholderHelper.replacePlaceholders(content, placeholderName -> {
            return values[Integer.valueOf(placeholderName)];
        });
    }

    /**
     * 解析带有指定占位符的模板字符串
     * 如：content =  ${0}今年${1}岁<br/>
     * values = {"xiaoming", "18"}<br/>
     * result 小明今年18岁<br/>
     *
     * @param content 要解析的带有占位符的模板字符串
     * @param values  按照模板占位符索引位置设置对应的值
     * @return
     */
    public String resolve(String content, Object[] values) {
        return propertyPlaceholderHelper.replacePlaceholders(content, placeholderName -> {
            return String.valueOf(values[Integer.valueOf(placeholderName)]);
        });
    }

    /**
     * 根据替换规则来替换指定模板中的占位符值
     *
     * @param content             要解析的字符串
     * @param placeholderResolver 解析规则回调
     * @return
     */
    public String resolveByRule(String content, PropertyPlaceholderHelper.PlaceholderResolver placeholderResolver) {
        return propertyPlaceholderHelper.replacePlaceholders(content, placeholderResolver);
    }

    /**
     * 替换模板中占位符内容，占位符的内容即为map key对应的值，key为占位符中的内容。<br/><br/>
     * 如：content = ${name}今年${age}岁<br/>
     * valueMap = name -> 小明; age -> 18<br/>
     * result 小明今年18岁<br/>
     *
     * @param content  模板内容。
     * @param valueMap 值映射
     * @return 替换完成后的字符串。
     */
    public String resolveByMap(String content, final Map<String, Object> valueMap) {
        return propertyPlaceholderHelper.replacePlaceholders(content, placeholderName -> {
            return String.valueOf(valueMap.get(placeholderName));
        });
    }

}
