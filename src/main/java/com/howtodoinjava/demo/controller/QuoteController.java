package com.howtodoinjava.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.howtodoinjava.demo.service.QuoteService;
 
import reactor.core.publisher.Flux;

@RestController
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    @GetMapping(value = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<String> flux() {
        return quoteService.streamData();
    }

}
