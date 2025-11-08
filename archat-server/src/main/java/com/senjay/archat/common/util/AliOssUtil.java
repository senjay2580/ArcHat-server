package com.senjay.archat.common.util;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ClientException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.ByteArrayInputStream;

/**
 * 阿里云OSS工具类
 * <p>
 * 提供文件上传到阿里云OSS的功能，支持字节数组上传。
 * 需要配置相应的访问密钥和存储桶信息。
 * </p>
 *
 * @author senjay
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {
    /**
     * OSS访问端点
     */
    private String endpoint;
    
    /**
     * 访问密钥ID
     */
    private String accessKeyId;
    
    /**
     * 访问密钥
     */
    private String accessKeySecret;
    
    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 上传文件到OSS
     *
     * @param bytes      文件字节数组
     * @param objectName 对象名称（文件名）
     * @return 文件访问地址
     */
    public String upload(byte[] bytes, String objectName) {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建PutObject请求
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            log.error("OSS上传异常：请求已发送到OSS，但被拒绝");
            log.error("错误信息：{}", oe.getErrorMessage());
            log.error("错误代码：{}", oe.getErrorCode());
            log.error("请求ID：{}", oe.getRequestId());
            log.error("主机ID：{}", oe.getHostId());
            throw new RuntimeException("文件上传失败", oe);
        } catch (ClientException ce) {
            log.error("OSS客户端异常：客户端与OSS通信时遇到严重内部问题");
            log.error("错误信息：{}", ce.getMessage());
            throw new RuntimeException("文件上传失败", ce);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 文件访问路径规则 https://BucketName.Endpoint/ObjectName
        String fileUrl = String.format("https://%s.%s/%s", bucketName, endpoint, objectName);
        log.info("文件上传成功，访问地址：{}", fileUrl);

        return fileUrl;
    }
}

