package com.howtodoinjava.demo.service;

import com.howtodoinjava.demo.config.RestTemplateResponseErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
public class QuoteService implements IQuoteService {

    public Flux<String> streamData() {
        try {
            List<CompletableFuture<String>> completableFutures = new CopyOnWriteArrayList<>();

            for (Integer i = 0; i < 4; i++) {
                Integer finalI = i+1;
                CompletableFuture<String> requestCompletableFuture = CompletableFuture
                        .supplyAsync(
                                () -> {
                                    try {
                                        TimeUnit.SECONDS.sleep(3 * finalI);
                                    } catch (InterruptedException e) {
                                    }
                                    return String.valueOf(finalI);
                                }
                        );
                completableFutures.add(requestCompletableFuture);
            }
            int size = completableFutures.size();
            return Flux.fromStream(IntStream.range(0, size).mapToObj(i -> {
                CompletableFuture<String> next = null;
                int counter = 0;
                while (completableFutures.size() > 0) {
                    CompletableFuture<String> future = completableFutures.get(counter++);
                    if (future.isDone()) {
                        next = future;
                        break;
                    }
                    if (counter == completableFutures.size()) {
                        counter = 0;
                    }
                }
                completableFutures.remove(next);

                try {
                    return "Quote " + next.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    return null;
                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private RestTemplate restTemplate;

    @Autowired
    public QuoteService(RestTemplateBuilder restTemplateBuilder) {
         this.restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    private String URL  = "https://www.zillow.com/webservice/GetDeepSearchResults.htm?zws-id=X1-ZWz17mw85rhbt7_af1tq&address=865%20Hall%20St%20NW&citystatezip=Atlanta/GA";
    public ResponseEntity restBuilder(){
        return  restTemplate.exchange("http://localhost:8080/test", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
    }

}