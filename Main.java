package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int numOfPlayers = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Server server = new Server(executorService);
        server.start();

        List<Callable<Integer>> callables = new ArrayList<>();
        for (int i = 0; i < numOfPlayers; i++) {
            Player player = new Player(Integer.toString(i));
            callables.add(server.connect(player));
        }
        List<Future<Integer>> futures = executorService.invokeAll(callables, 15, TimeUnit.SECONDS);
        boolean anyCancelled = futures.stream()
                .anyMatch(Future::isCancelled);
        if (anyCancelled) {
            System.out.println("Lost connection to server");
        } else {
            System.out.println("Game is started");
        }
    }

    private static class Server {
        ExecutorService executorService;
        public Server(ExecutorService executorService) {
            this.executorService = executorService;
        }

        void start() {
            System.out.println("Server is started");
        }

        Callable<Integer> connect(Player player) {
            return () -> {
                int delay = new Random().nextInt(15) + 5;
                TimeUnit.SECONDS.sleep(delay);
                System.out.println("Player " + player.name + " is connected in " + delay + " seconds");
                return delay;
            };
        }

    }

    private static class Player {       String name;
        public Player(String name) {
            this.name = name;
        }
    }
}
