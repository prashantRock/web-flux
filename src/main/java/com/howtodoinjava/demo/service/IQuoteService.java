package com.howtodoinjava.demo.service;

import reactor.core.publisher.Flux;

public interface IQuoteService {

    Flux<String> streamData();

}