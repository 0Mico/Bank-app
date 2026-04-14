package com.bankapp.auth.client;

import com.auth.client.AccountServiceClient;
import com.common.dto.AccountDTO;
import com.common.exception.BadRequestException;
import com.common.exception.ResourceNotFoundException;
import com.common.exception.ServiceUnavailableException;
import com.common.model.AccountModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@DisplayName("Account Service Client Test class")
class AccountServiceClientTest {

    @Autowired
    private ObjectMapper objectMapper;
    
    private MockRestServiceServer mockServer; // To simulate a server that receives requests made by the AccountServiceClient

    private AccountServiceClient accountServiceClient;
    
    private final String accountServiceUrl = "http://account-service";

    @BeforeEach
    void setup() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        accountServiceClient = new AccountServiceClient(builder.build());
        
        ReflectionTestUtils.setField(accountServiceClient, "accountServiceUrl", accountServiceUrl);
    }

    @Nested
    @DisplayName("Tests for createAccount")
    class CreateAccountTests {

        @Test
        @DisplayName("Should successfully create account and return AccountDTO")
        void shouldCreateAccountSuccessfully() throws Exception {
            Long userId = 1L;
            AccountDTO expectedResponse = new AccountDTO();
            expectedResponse.setId(10L);
            expectedResponse.setUserId(userId);
            expectedResponse.setCurrency("EUR");
            expectedResponse.setBalance(BigDecimal.ZERO);

            mockServer.expect(requestTo(accountServiceUrl + "/api/accounts"))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.currency").value("EUR"))
                    .andRespond(withSuccess(objectMapper.writeValueAsString(expectedResponse), MediaType.APPLICATION_JSON));

            AccountModel result = accountServiceClient.createAccount(userId);

            assertNotNull(result);
            assertEquals(10L, result.getId());
            assertEquals(userId, result.getUserId());
            assertEquals("EUR", result.getCurrency());
            
            mockServer.verify();
        }

        @Test
        @DisplayName("Should throw BadRequestException when account-service returns 400")
        void shouldThrowBadRequestExceptionOn400() {
            Long userId = 1L;

            mockServer.expect(requestTo(accountServiceUrl + "/api/accounts"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withBadRequest().body("{\"message\":\"Invalid request\"}")
                        .contentType(MediaType.APPLICATION_JSON));

            BadRequestException exception = assertThrows(BadRequestException.class, 
                    () -> accountServiceClient.createAccount(userId));
                    
            assertTrue(exception.getMessage().contains("account-service"));
            
            mockServer.verify();
        }

        @Test
        @DisplayName("Should throw ServiceUnavailableException when account-service returns 500")
        void shouldThrowServiceUnavailableExceptionOn500() {
            Long userId = 1L;

            mockServer.expect(requestTo(accountServiceUrl + "/api/accounts"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withServerError());

            ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class, 
                    () -> accountServiceClient.createAccount(userId));
                    
            assertTrue(exception.getMessage().contains("account-service"));
            
            mockServer.verify();
        }
    }

    @Nested
    @DisplayName("Tests for getAccountByIban")
    class GetAccountByIbanTests {

        @Test
        @DisplayName("Should returning AccountDTO when IBAN exists")
        void shouldReturnAccountByIbanSuccessfully() throws Exception {
            String iban = "IT60X0542811101000000123456";
            AccountDTO expectedResponse = new AccountDTO();
            expectedResponse.setId(10L);
            expectedResponse.setUserId(1L);
            expectedResponse.setIban(iban);
            expectedResponse.setCurrency("EUR");
            expectedResponse.setBalance(new BigDecimal("1000.00"));

            mockServer.expect(requestTo(accountServiceUrl + "/api/accounts/iban?iban=" + iban))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess(objectMapper.writeValueAsString(expectedResponse), MediaType.APPLICATION_JSON));

            AccountModel result = accountServiceClient.getAccountByIban(iban);

            assertNotNull(result);
            assertEquals(10L, result.getId());
            assertEquals(1L, result.getUserId());
            assertEquals(iban, result.getIban());
            
            mockServer.verify();
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when account-service returns 404")
        void shouldThrowResourceNotFoundExceptionOn404() {
            String iban = "UNKNOWN_IBAN";

            mockServer.expect(requestTo(accountServiceUrl + "/api/accounts/iban?iban=" + iban))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withStatus(HttpStatus.NOT_FOUND).body("{\"message\":\"Account not found\"}").contentType(MediaType.APPLICATION_JSON));

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                    () -> accountServiceClient.getAccountByIban(iban));
                    
            assertTrue(exception.getMessage().contains("account-service"));
            
            mockServer.verify();
        }

        @Test
        @DisplayName("Should throw ServiceUnavailableException when account-service returns 500")
        void shouldThrowServiceUnavailableExceptionOn500() {
            String iban = "IT60X0542811101000000123456";

            mockServer.expect(requestTo(accountServiceUrl + "/api/accounts/iban?iban=" + iban))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withServerError());

            ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class, 
                    () -> accountServiceClient.getAccountByIban(iban));
                    
            assertTrue(exception.getMessage().contains("account-service"));
            
            mockServer.verify();
        }
    }
}
