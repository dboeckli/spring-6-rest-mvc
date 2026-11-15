package ch.dboeckli.spring.restmvc.config;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServerHealthIndicatorTest {

    @Test
    void healthIsUpWhenAuthServerReportsUp() throws IOException {
        HttpServer server = startServer("{\"status\":\"UP\"}");
        try {
            AuthServerHealthIndicator indicator = new AuthServerHealthIndicator(baseUrl(server));

            assertThat(indicator.health().getStatus().getCode()).isEqualTo("UP");
        }
        finally {
            server.stop(0);
        }
    }

    @Test
    void healthIsDownWhenAuthServerReportsOtherStatus() throws IOException {
        HttpServer server = startServer("{\"status\":\"DOWN\"}");
        try {
            AuthServerHealthIndicator indicator = new AuthServerHealthIndicator(baseUrl(server));

            assertThat(indicator.health().getStatus().getCode()).isEqualTo("DOWN");
        }
        finally {
            server.stop(0);
        }
    }

    @Test
    void healthIsDownWhenAuthServerIsNotReachable() throws IOException {
        HttpServer server = startServer("{\"status\":\"UP\"}");
        int port = server.getAddress().getPort();
        server.stop(0);

        AuthServerHealthIndicator indicator = new AuthServerHealthIndicator("http://127.0.0.1:" + port);

        assertThat(indicator.health().getStatus().getCode()).isEqualTo("DOWN");
    }

    private static HttpServer startServer(String responseBody) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/actuator/health", exchange -> {
            byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(body);
            }
        });
        server.start();
        return server;
    }

    private static String baseUrl(HttpServer server) {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

}
