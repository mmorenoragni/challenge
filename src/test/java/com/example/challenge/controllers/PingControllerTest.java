package com.example.challenge.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PingController.class)
@ExtendWith(MockitoExtension.class)
class PingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPingController() throws Exception {
        ResultActions response = mockMvc.perform(get("/ping"));

        response.andExpect(status().isOk());
    }
}