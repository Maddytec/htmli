package com.maddytec.htmli.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maddytec.htmli.model.Client;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@Slf4j
class ClientControllerTest {

    private static final String NAME = "Madson Silva";
    private static final String EMAIL = "maddytec@gmail.com";
    private static final String XSS_EXAMPLE =
            """
                     <script>
                         let countdown = 5;
                         const colors = ['red', 'orange', 'yellow', 'green', 'blue'];
                         const interval = setInterval(() => {
                             document.body.style.backgroundColor = colors[5 - countdown];
                             document.body.innerHTML = '<h1 style="text-align: center; margin-top: 20%; color: white; font-size: 100px;">' + countdown + '</h1>';
                             countdown--;
                             if (countdown < 0) {
                                 clearInterval(interval);
                                 document.body.style.backgroundColor = 'white';
                                 document.body.innerHTML = '<h1 style="text-align: center; margin-top: 20%; color: red; font-size: 50px;">Proteja sua API contra HTML Inject Otário!!!!</h1>';
                             }
                         }, 1000);
                     </script>
             """;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach
    void before() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    void shouldCreateClientWithSuccessWhenThereIsNotHTML() throws Exception {
        Client client = clientBuilder(NAME);
        ResultActions resultActions = performPostRequest(client);
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.email", is(EMAIL)));
    }

    @Test
    void shouldCreateClientWithSuccessWhenBodyContainsXSS() throws Exception {
        Client client = clientBuilder(NAME + XSS_EXAMPLE);
        ResultActions resultActions = performPostRequest(client);
        resultActions.andReturn().getResponse().getContentAsString();
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("HTML Injection detected")));
    }

    private Client clientBuilder(String name) {
        return Client.builder()
                .name(name)
                .email(EMAIL)
                .build();
    }

    private ResultActions performPostRequest(Client client) throws Exception {
        return mockMvc.perform(
                        post("/api/v1/clients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(client)))
                .andDo(resultHandler ->
                        log.info("Response " + resultHandler.getResponse().getContentAsString())
                );
    }

}