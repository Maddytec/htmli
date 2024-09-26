package com.maddytec.htmli.controller;

import com.maddytec.htmli.model.UserRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Log4j2
@RestController
@RequestMapping("user")
public class HtmlIController {
    private HashMap<String, UserRequest> users = new HashMap<>();

    @PostMapping
    public void confirmacao(@RequestBody UserRequest userRequest) {
        users.put(userRequest.getEmail(), userRequest);
        log.info("User: " + userRequest);
    }
}
