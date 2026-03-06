package com.bankapp.gateway.proxy;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.net.URI;

/**
 * Simple reverse-proxy controller: forwards incoming /api/** requests
 * to the appropriate backend micro-service.
 */
@RestController
public class ProxyController {

    private static final Set<String> EXCLUDED_HEADERS = Set.of(
        "host", "connection", "content-length", "transfer-encoding");

    private final RestClient authClient;
    private final RestClient transactionClient;
    private final RestClient paymentClient;

    @Value("${services.auth.url}")
    private String authUrl;

    @Value("${services.transaction.url}")
    private String transactionUrl;

    @Value("${services.payment.url}")
    private String paymentUrl;

    public ProxyController() {
        this.authClient = RestClient.builder().build();
        this.transactionClient = RestClient.builder().build();
        this.paymentClient = RestClient.builder().build();
    }

    @RequestMapping("/api/auth/**")
    public ResponseEntity<byte[]> proxyAuth(HttpServletRequest request) throws IOException {
        return forward(authClient, authUrl, request);
    }

    @RequestMapping("/api/transactions/**")
    public ResponseEntity<byte[]> proxyTransactions(HttpServletRequest request) throws IOException {
        return forward(transactionClient, transactionUrl, request);
    }

    @RequestMapping("/api/payments/**")
    public ResponseEntity<byte[]> proxyPayments(HttpServletRequest request) throws IOException {
        return forward(paymentClient, paymentUrl, request);
    }

    private ResponseEntity<byte[]> forward(RestClient client, String baseUrl, HttpServletRequest request)
            throws IOException {
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String fullUrl = baseUrl + uri;
        if (query != null) {
            fullUrl += "?" + query;
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        return client.method(method)
                .uri(URI.create(fullUrl))
                .headers(headers -> {
                    // Forward original request headers
                    Enumeration<String> names = request.getHeaderNames();
                    while (names.hasMoreElements()) {
                        String name = names.nextElement();
                        if (!EXCLUDED_HEADERS.contains(name.toLowerCase())) {
                            headers.set(name, request.getHeader(name));
                        }
                    }
                    // Add user context headers from AuthFilter
                    addIfPresent(request, headers, "X-User-Id");
                    addIfPresent(request, headers, "X-User-Email");
                    addIfPresent(request, headers, "X-User-Role");
                })
                .body(request.getInputStream().readAllBytes())
                .exchange((req, res) -> {
                    byte[] body = res.getBody().readAllBytes();
                    HttpHeaders responseHeaders = new HttpHeaders();
                    res.getHeaders().forEach((name, values) -> {
                        if (!EXCLUDED_HEADERS.contains(name.toLowerCase())) {
                            responseHeaders.put(name, values);
                        }
                    });
                    return ResponseEntity
                            .status(res.getStatusCode())
                            .headers(responseHeaders)
                            .body(body);
                });
    }

    private void addIfPresent(HttpServletRequest request, HttpHeaders headers, String name) {
        Object value = request.getAttribute(name);
        if (value != null) {
            headers.set(name, value.toString());
        }
    }
}
