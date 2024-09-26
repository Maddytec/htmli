package com.maddytec.htmli.controller;

import com.maddytec.htmli.model.Client;
import jakarta.validation.Valid;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("api/v1/clients")
public class ClientController {
    private HashMap<String, Client> clients = new HashMap<>();

    @GetMapping("/email/{email}")
    public Client getClient(@PathVariable String email) {
        if (ObjectUtils.isEmpty(clients.get(email))) {
            return Client.builder().build();
        }
        return clients.get(email);
    }

    @PostMapping
    public Client saveClient(@Valid @RequestBody Client client) {
        clients.put(client.getEmail(), client);
        return client;
    }

}
