package com.senjay.archat.common.user.domain.vo.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import java.util.List;

/**
 * IP 详情响应实体类
 *
 * 该类用于封装IP相关的详细信息，字段对应来自API的JSON返回值，
 * 使用@JsonNaming注解实现JSON中下划线命名与Java驼峰字段的自动映射，
 * 内部包含多个嵌套静态类表示复杂结构。
 *
 * 时间：2025-07-10 14:14:08
 */
// 该类及内部类使用SnakeCase策略映射字段名
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data // Lombok注解，自动生成getter/setter、toString等方法
public class IpDetail {
    /**
     * IP地址
     */
    private String ip;

    /**
     * 主机名
     */
    private String hostname;

    /**
     * IP类型，如 ipv4 或 ipv6
     */
    private String type;

    /**
     * 大洲代码，例如 NA
     */
    private String continentCode;

    /**
     * 大洲名称，例如 North America
     */
    private String continentName;

    /**
     * 国家代码，例如 US
     */
    private String countryCode;

    /**
     * 国家名称，例如 United States
     */
    private String countryName;

    /**
     * 省/州代码，例如 CA
     */
    private String regionCode;

    /**
     * 省/州名称，例如 California
     */
    private String regionName;

    /**
     * 城市名称，例如 Los Angeles
     */
    private String city;

    /**
     * 邮政编码，例如 90013
     */
    private String zip;

    /**
     * 纬度，例如 34.0655
     */
    private Double latitude;

    /**
     * 经度，例如 -118.2405
     */
    private Double longitude;

    /**
     * 大都市统计区代码（MSA）
     */
    private String msa;

    /**
     * 设计市场区域代码（DMA）
     */
    private String dma;

    /**
     * 半径，类型不固定，可能为null
     */
    private Object radius;

    /**
     * IP路由类型，类型不固定
     */
    private Object ipRoutingType;

    /**
     * 连接类型，类型不固定
     */
    private Object connectionType;

    /**
     * 详细地理位置信息，复杂对象
     */
    private Location location;

    /**
     * 时区信息，复杂对象
     */
    private TimeZoneInfo timeZone;

    /**
     * 货币信息，复杂对象
     */
    private Currency currency;

    /**
     * 网络连接信息，复杂对象
     */
    private Connection connection;

    /**
     * 安全相关信息，复杂对象
     */
    private Security security;

    /**
     * 地理位置信息内部类
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class Location {
        /**
         * 地理名称ID
         */
        private Long geonameId;

        /**
         * 首都名称
         */
        private String capital;

        /**
         * 语言列表
         */
        private List<Language> languages;

        /**
         * 国家旗帜图片URL
         */
        private String countryFlag;

        /**
         * 国家旗帜Emoji
         */
        private String countryFlagEmoji;

        /**
         * 国家旗帜Emoji Unicode编码
         */
        private String countryFlagEmojiUnicode;

        /**
         * 国际电话区号
         */
        private String callingCode;

        /**
         * 是否欧盟成员国
         */
        private Boolean isEu;
    }

    /**
     * 语言信息内部类
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class Language {
        /**
         * 语言代码，如 en
         */
        private String code;

        /**
         * 语言名称，如 English
         */
        private String name;

        /**
         * 语言本地名称，字段名为nativeLang，映射JSON的native
         */
        private String nativeLang;
    }

    /**
     * 时区信息内部类
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class TimeZoneInfo {
        /**
         * 时区ID，如 America/Los_Angeles
         */
        private String id;

        /**
         * 当前时间，ISO8601格式
         */
        private String currentTime;

        /**
         * GMT偏移秒数
         */
        private Integer gmtOffset;

        /**
         * 时区简称，如 PDT
         */
        private String code;

        /**
         * 是否夏令时
         */
        private Boolean isDaylightSaving;
    }

    /**
     * 货币信息内部类
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class Currency {
        /**
         * 货币代码，如 USD
         */
        private String code;

        /**
         * 货币名称，如 US Dollar
         */
        private String name;

        /**
         * 货币复数形式，如 US dollars
         */
        private String plural;

        /**
         * 货币符号，如 $
         */
        private String symbol;

        /**
         * 本地货币符号
         */
        private String symbolNative;
    }

    /**
     * 连接信息内部类
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class Connection {
        /**
         * 自治系统号(ASN)
         */
        private Integer asn;

        /**
         * 互联网服务提供商名称
         */
        private String isp;

        /**
         * 第二级域名
         */
        private String sld;

        /**
         * 顶级域名
         */
        private String tld;

        /**
         * 运营商名称
         */
        private String carrier;

        /**
         * 主页，通常为空
         */
        private Object home;

        /**
         * 组织类型
         */
        private Object organizationType;

        /**
         * ISIC行业代码
         */
        private Object isicCode;

        /**
         * NAICS行业代码
         */
        private Object naicsCode;
    }

    /**
     * 安全信息内部类
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class Security {
        /**
         * 是否为代理IP
         */
        private Boolean isProxy;

        /**
         * 代理类型
         */
        private Object proxyType;

        /**
         * 是否为爬虫
         */
        private Boolean isCrawler;

        /**
         * 爬虫名称
         */
        private Object crawlerName;

        /**
         * 爬虫类型
         */
        private Object crawlerType;

        /**
         * 是否为Tor网络节点
         */
        private Boolean isTor;

        /**
         * 威胁等级，如 low
         */
        private String threatLevel;

        /**
         * 威胁类型
         */
        private Object threatTypes;

        /**
         * 代理最后检测时间
         */
        private Object proxyLastDetected;

        /**
         * 代理级别
         */
        private Object proxyLevel;

        /**
         * VPN服务标识
         */
        private Object vpnService;

        /**
         * 匿名状态
         */
        private Object anonymizerStatus;

        /**
         * 是否为托管设施IP
         */
        private Boolean hostingFacility;
    }
}
