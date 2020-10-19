package com.howtodoinjava.demo.service;

import org.springframework.stereotype.Service;

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



}