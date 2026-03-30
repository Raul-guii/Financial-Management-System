package com.raul.backend.controller;

import com.raul.backend.service.WebhookService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public void receive(@RequestBody Map<String, Object> payload){
        System.out.println("WEBHOOK RECEBIDO: " + payload);
        webhookService.process(payload);
    }
}
