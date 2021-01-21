package com.kube.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;


@Configuration
@ConfigurationProperties("kubernetes.config")
public class KubeConfig {
    private String configPath;
    private String token;
    public String getConfigPath() {
        return configPath;
    }

    public String getToken() {
        return token;
    }

    public String getMaster() {
        return master;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    /**
     * k8s master
     */
    private String master;
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
    private static final String HTTPS_PROTOCOL = "https://";
    @Bean(name = "kubeApiClient")
    public ApiClient apiClient() {
     //   ApiClient kubeApiClient = null;
//        try {
//            kubeApiClient = Config.fromConfig(configPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        ApiClient kubeApiClient = new ClientBuilder()
                .setBasePath(String.format("%s%s", HTTPS_PROTOCOL, getMaster()))
                .setVerifyingSsl(false)
                .setAuthentication(new AccessTokenAuthentication(getTokenValue(getToken()))).build();

        kubeApiClient.setConnectTimeout(1800000);
        kubeApiClient.setReadTimeout(1800000);
        kubeApiClient.setWriteTimeout(1800000);

        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(kubeApiClient);
        return kubeApiClient;
    }
    private String getTokenValue(String path) {
        try {
            return FileUtils.readFileToString(new File(path));
        } catch (IOException e) {
            throw new RuntimeException("read token file failed.", e);
        }
    }
}
