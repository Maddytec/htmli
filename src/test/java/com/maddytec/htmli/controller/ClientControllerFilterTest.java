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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@Slf4j
class ClientControllerFilterTest {

    private static final String NAME = "Madson";
    private static final String LASTNAME = "Silva";
    private static final String EMAIL = "maddytec@gmail.com";
    private static final String TEST = "test";
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
        ResultActions resultActions = performPostRequest(TEST, TEST, client);
        assertResult(resultActions);
    }

    @Test
    void shouldCreateClientWithSuccessWhenHeaderContainsXSS() throws Exception {
        Client client = clientBuilder(NAME);

        ResultActions resultActions = performPostRequest(XSS_EXAMPLE, TEST, client);
        assertResult(resultActions);
    }

    @Test
    void shouldCreateClientWithSuccessWhenParameterContainsXSS() throws Exception {
        Client client = clientBuilder(NAME);

        ResultActions resultActions = performPostRequest(TEST, XSS_EXAMPLE, client);
        assertResult(resultActions);
    }

    @Test
    void shouldCreateClientWithSuccessWhenBodyContainsXSS() throws Exception {
        Client client = clientBuilder(NAME + XSS_EXAMPLE);

        ResultActions resultActions = performPostRequest(TEST, TEST, client);
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(NAME + " \n")))
                .andExpect(jsonPath("$.email", is(EMAIL)));
    }

    @Test
    void shouldCreateProductWithSuccessWhenHeaderAndParameterAndBodyContainsXSS() throws Exception {
        Client client = clientBuilder(NAME + XSS_EXAMPLE);

        ResultActions resultActions = performPostRequest(XSS_EXAMPLE, XSS_EXAMPLE, client);
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(NAME + " \n")))
                .andExpect(jsonPath("$.email", is(EMAIL)));
    }

    private ResultActions assertResult(ResultActions resultActions) throws Exception {
        return resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.email", is(EMAIL)));

    }

    private Client clientBuilder(String name) {
        return Client.builder()
                .name(name)
                .lastname(LASTNAME)
                .email(EMAIL)
                .build();
    }

    private ResultActions performPostRequest(String header, String parameter,
                                             Client request) throws Exception {
        return mockMvc.perform(
                        post("/api/v1/clients")
                                .header("HEADER-TEST", header)
                                .param("PARAMETER-TEST", parameter)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(resultHandler ->
                        log.info("Response " + resultHandler.getResponse().getContentAsString())
                );
    }

}