/*
 * Copyright (c) 2021. Cisco Systems, Inc and its affiliates
 * All Rights reserved
 */

package com.cisco.msx.integration;

import lombok.Data;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "integration.http-client")
@Validated
public class MsxHttpClientProperties {

    private RequestConfigProperties request = new RequestConfigProperties();
    private ConnectionPoolProperties connections = new ConnectionPoolProperties();

    /**
     * See {@link RequestConfig}
     */
    @Data
    public static class RequestConfigProperties {
        /**
         * The timeout used when requesting a connection from the connection manager.
         * A timeout value of zero is interpreted as an infinite timeout.
         */
        private Duration connectionRequestTimeout = Duration.ofMillis(10000);

        /**
         * Determines the timeout until a connection is established.
         * A timeout value of zero is interpreted as an infinite timeout.
         */
        private Duration connectTimeout = Duration.ofMillis(10000);

        /**
         * Defines the socket timeout ({@code SO_TIMEOUT}),
         * which is the timeout for waiting for data or, put differently,
         * a maximum period inactivity between two consecutive data packets).
         */
        private Duration socketTimeout = Duration.ofMillis(10000);
    }

    /**
     * See {@link PoolingHttpClientConnectionManager}
     */
    @Data
    public static class ConnectionPoolProperties {
        private int maxTotal = 100;
        private int maxPerRoute = 50;
    }
}
