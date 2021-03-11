/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.integration;

import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MsxHttpClientFactoryTest {

    private MsxHttpClientFactory msxHttpClientFactory;

    @Mock
    private MsxHttpClientProperties msxHttpClientProperties;

    @BeforeEach
    public void setUp() {
        msxHttpClientFactory = new MsxHttpClientFactory(msxHttpClientProperties);
    }

    @Test
    public void createHttpClient() {
        // given
        Duration connectionRequestTimeout = Duration.ofMillis(10000);
        Duration connectTimeout = Duration.ofMillis(10000);
        Duration socketTimeout = Duration.ofMillis(10000);
        int maxTotal = 100;
        int maxPerRoute = 50;

        MsxHttpClientProperties.RequestConfigProperties request = mock(MsxHttpClientProperties.RequestConfigProperties.class);
        MsxHttpClientProperties.ConnectionPoolProperties connections = mock(MsxHttpClientProperties.ConnectionPoolProperties.class);
        given(msxHttpClientProperties.getRequest()).willReturn(request);
        given(msxHttpClientProperties.getConnections()).willReturn(connections);

        given(request.getConnectionRequestTimeout()).willReturn(connectionRequestTimeout);
        given(request.getConnectTimeout()).willReturn(connectTimeout);
        given(request.getSocketTimeout()).willReturn(socketTimeout);

        given(connections.getMaxPerRoute()).willReturn(maxPerRoute);
        given(connections.getMaxTotal()).willReturn(maxTotal);

        // when
        HttpClient httpClient = msxHttpClientFactory.createHttpClient();

        // then
        assertNotNull(httpClient);
        then(msxHttpClientProperties).should(times(3)).getRequest();
        then(msxHttpClientProperties).should(times(2)).getConnections();
    }
}
