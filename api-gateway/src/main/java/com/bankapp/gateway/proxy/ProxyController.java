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
import java.net.URISyntaxException;

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
    private final RestClient accountClient;

    @Value("${services.auth.url}")
    private String authUrl;

    @Value("${services.transaction.url}")
    private String transactionUrl;

    @Value("${services.payment.url}")
    private String paymentUrl;

    @Value("${services.account.url}")
    private String accountUrl;

    public ProxyController() {
        this.authClient = RestClient.builder().build();
        this.transactionClient = RestClient.builder().build();
        this.paymentClient = RestClient.builder().build();
        this.accountClient = RestClient.builder().build();
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

    @RequestMapping("/api/accounts/**")
    public ResponseEntity<byte[]> proxyAccounts(HttpServletRequest request) throws IOException {
        return forward(accountClient, accountUrl, request);
    }

    private URI assembleUri(String baseUrl, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String normalizedPath = URI.create(uri).normalize().getPath();
        URI base = URI.create(baseUrl);
        try {
            URI finalUri = new URI(
                base.getScheme(),
                base.getAuthority(),
                normalizedPath,
                query,
                null
            );
            return finalUri;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Error assembling requests uri: " + e.getMessage(), e);
        }
    }

    private ResponseEntity<byte[]> forward(RestClient client, String baseUrl, HttpServletRequest request)
            throws IOException {

        URI uri = assembleUri(baseUrl, request); 
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        return client.method(method)
                .uri(uri)
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
