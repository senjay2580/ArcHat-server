package com.senjay.archat.client.user.controller;

import com.senjay.archat.common.user.domain.vo.response.IpDetail;
import com.senjay.archat.common.user.domain.vo.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * IP地址查询控制器
 * <p>
 * 提供IP地址查询和地理位置信息获取的功能。
 * 支持从请求头中获取真实IP地址，并调用第三方API获取详细信息。
 * </p>
 *
 * @author senjay
 * @since 1.0.0
 */
@RestController
@RequestMapping("/ip")
@RequiredArgsConstructor
public class IpController {
    private final RestTemplate restTemplate;

    /**
     * 查询IP地址信息
     * <p>
     * 从请求头中获取真实IP地址，并调用第三方API获取详细的地理位置信息。
     * 支持多种代理头的IP获取方式。
     * </p>
     *
     * @param request HTTP请求对象
     * @return IP地址详细信息
     */
    @Operation(summary = "查询ip")
    @GetMapping
    public Result<IpDetail> getIp(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };
        
        String realIp = null;
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                // 防止多个IP串，用第一个
                realIp = ip.split(",")[0].trim();
                break;
            }
        }
        
        // 如果以上方法都获取不到，使用默认IP
        if (realIp == null) {
            realIp = request.getRemoteAddr();
        }
        
        String apiKey = "a73116188191fd9b42e538ae81454d0f";
        String testIp = "112.49.193.35"; // 测试用IP
        String baseUrl = "http://api.ipstack.com/";
        
        // 调用API获取IP信息
        URI uri = UriComponentsBuilder
                .fromHttpUrl(String.format("%s%s", baseUrl, testIp))
                .queryParam("access_key", apiKey)
                .build()
                .encode() // 防止中文被破坏
                .toUri();

        return Result.success(restTemplate.getForObject(uri, IpDetail.class));
    }

}
