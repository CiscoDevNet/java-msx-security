/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.integration;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

@RequiredArgsConstructor
public class MsxHttpClientFactory {

    private final MsxHttpClientProperties msxHttpClientProperties;

    public HttpClient createHttpClient() {

        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setSocketTimeout((int) msxHttpClientProperties.getRequest().getSocketTimeout().toMillis())
                .setConnectTimeout((int) msxHttpClientProperties.getRequest().getConnectTimeout().toMillis())
                .setConnectionRequestTimeout((int) msxHttpClientProperties.getRequest().getConnectionRequestTimeout().toMillis());

        return httpClientBuilder
                .setDefaultRequestConfig(requestConfigBuilder.build())
                .setMaxConnTotal(msxHttpClientProperties.getConnections().getMaxTotal())
                .setMaxConnPerRoute(msxHttpClientProperties.getConnections().getMaxPerRoute())
                .build();
    }
}
