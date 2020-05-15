package com.unipd.cs.algorithms.graph;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementazione di algoritmi paralleli per grafi.
 * 
 * Questa classe implementa versioni parallele degli algoritmi di ricerca in ampiezza (BFS)
 * e in profondità (DFS) per attraversare grafi efficientemente utilizzando più thread.
 * 
 * @author Università di Padova - Corso di Algoritmi Avanzati
 * @version 1.0
 * @since Maggio 2020
 */
public class ParallelGraphAlgorithms {

    /**
     * Testa e confronta implementazioni sequenziali e parallele.
     */
    public static void main(String[] args) {
        System.out.println("Algoritmi Paralleli per Grafi - Progetto Finale CS3242");
        System.out.println("==============================================");
        
        // Parametri di test
        int[] dimensioniGrafo = {1000, 5000, 10000};
        double probabilitaArco = 0.01; // Mantiene il grafo sparso per evitare problemi di memoria
        int numThread = Runtime.getRuntime().availableProcessors();
        
        for (int dimensione : dimensioniGrafo) {
            System.out.println("\nTest con dimensione del grafo: " + dimensione);
            
            // Genera un grafo casuale connesso
            Graph grafo = Graph.generateRandomGraph(dimensione, probabilitaArco);
            
            // Test BFS
            System.out.println("\nRicerca in Ampiezza (Breadth-First Search):");
            long tempoInizio = System.currentTimeMillis();
            Set<Integer> risultatoBFSSequenziale = grafo.bfs(0);
            long tempoFine = System.currentTimeMillis();
            System.out.printf("  Tempo BFS sequenziale: %d ms, Visitati: %d vertici%n", 
                    (tempoFine - tempoInizio), risultatoBFSSequenziale.size());
            
            tempoInizio = System.currentTimeMillis();
            Set<Integer> risultatoBFSParallelo = bfsParallelo(grafo, 0, numThread);
            tempoFine = System.currentTimeMillis();
            System.out.printf("  Tempo BFS parallelo: %d ms, Visitati: %d vertici%n", 
                    (tempoFine - tempoInizio), risultatoBFSParallelo.size());
            
            // Test DFS
            System.out.println("\nRicerca in Profondità (Depth-First Search):");
            tempoInizio = System.currentTimeMillis();
            Set<Integer> risultatoDFSSequenziale = grafo.dfs(0);
            tempoFine = System.currentTimeMillis();
            System.out.printf("  Tempo DFS sequenziale: %d ms, Visitati: %d vertici%n", 
                    (tempoFine - tempoInizio), risultatoDFSSequenziale.size());
            
            tempoInizio = System.currentTimeMillis();
            Set<Integer> risultatoDFSParallelo = dfsParallelo(grafo, 0, numThread);
            tempoFine = System.currentTimeMillis();
            System.out.printf("  Tempo DFS parallelo: %d ms, Visitati: %d vertici%n", 
                    (tempoFine - tempoInizio), risultatoDFSParallelo.size());
        }
    }
    
    /**
     * Implementazione parallela della Ricerca in Ampiezza (BFS) utilizzando thread.
     * Questo utilizza un approccio sincronizzato a livelli dove ogni livello del BFS
     * viene elaborato in parallelo.
     */
    public static Set<Integer> bfsParallelo(Graph grafo, int verticeIniziale, int numThread) {
        final Set<Integer> visitati = Collections.synchronizedSet(new HashSet<>());
        final Set<Integer> livelloCorrente = Collections.synchronizedSet(new HashSet<>());
        final Set<Integer> prossimoLivello = Collections.synchronizedSet(new HashSet<>());
        
        // Inizia con il vertice iniziale
        visitati.add(verticeIniziale);
        livelloCorrente.add(verticeIniziale);
        
        // Crea un pool di thread
        ExecutorService executor = Executors.newFixedThreadPool(numThread);
        
        // Elabora ogni livello nel BFS
        while (!livelloCorrente.isEmpty()) {
            prossimoLivello.clear();
            final CountDownLatch latch = new CountDownLatch(livelloCorrente.size());
            
            // Elabora tutti i vertici nel livello corrente in parallelo
            for (final int vertice : livelloCorrente) {
                executor.submit(() -> {
                    try {
                        for (int vicino : grafo.getNeighbors(vertice)) {
                            // Prova ad aggiungere a visitati (restituisce false se già visitato)
                            if (visitati.add(vicino)) {
                                prossimoLivello.add(vicino);
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            // Attende il completamento di tutti i task a questo livello
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Scambia i livelli
            Set<Integer> temp = livelloCorrente;
            livelloCorrente = prossimoLivello;
            prossimoLivello = temp;
        }
        
        executor.shutdown();
        return visitati;
    }
    
    /**
     * Implementazione parallela della Ricerca in Profondità (DFS) utilizzando furto di lavoro.
     */
    public static Set<Integer> dfsParallelo(Graph grafo, int verticeIniziale, int numThread) {
        final Set<Integer> visitati = Collections.synchronizedSet(new HashSet<>());
        final ConcurrentLinkedQueue<Integer> codaGlobale = new ConcurrentLinkedQueue<>();
        final ReentrantLock[] lockVertice = new ReentrantLock[grafo.getVerticesCount()];
        
        // Inizializza lock per ogni vertice
        for (int i = 0; i < grafo.getVerticesCount(); i++) {
            lockVertice[i] = new ReentrantLock();
        }
        
        // Inizia con il vertice iniziale
        codaGlobale.add(verticeIniziale);
        
        // Crea thread worker
        Thread[] workers = new Thread[numThread];
        final AtomicInteger workerAttivi = new AtomicInteger(numThread);
        final CountDownLatch latch = new CountDownLatch(numThread);
        
        for (int i = 0; i < numThread; i++) {
            workers[i] = new Thread(() -> {
                Deque<Integer> pilaLocale = new ArrayDeque<>();
                
                while (true) {
                    Integer vertice = null;
                    
                    // Prova a ottenere lavoro dalla pila locale
                    if (!pilaLocale.isEmpty()) {
                        vertice = pilaLocale.pop();
                    } 
                    // Se la pila locale è vuota, prova a rubare dalla coda globale
                    else {
                        vertice = codaGlobale.poll();
                        
                        // Se non è disponibile lavoro, segnala che questo worker è inattivo
                        if (vertice == null) {
                            // Se tutti i worker sono inattivi, abbiamo finito
                            if (workerAttivi.decrementAndGet() == 0) {
                                // Sveglia tutti i worker per terminare
                                workerAttivi.set(numThread);
                            } else {
                                // Attendi lavoro o segnale di terminazione
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                                
                                // Riprova
                                if (codaGlobale.isEmpty() && workerAttivi.get() == numThread) {
                                    break;
                                } else {
                                    workerAttivi.incrementAndGet();
                                    continue;
                                }
                            }
                            
                            break;
                        }
                    }
                    
                    // Elabora il vertice se non visitato
                    if (vertice != null && !visitati.contains(vertice)) {
                        // Prova a bloccare il vertice
                        if (lockVertice[vertice].tryLock()) {
                            try {
                                if (!visitati.contains(vertice)) {
                                    visitati.add(vertice);
                                    
                                    // Aggiungi vicini alla pila locale
                                    for (int vicino : grafo.getNeighbors(vertice)) {
                                        if (!visitati.contains(vicino)) {
                                            pilaLocale.push(vicino);
                                        }
                                    }
                                }
                            } finally {
                                lockVertice[vertice].unlock();
                            }
                        } else {
                            // Se non è stato possibile bloccare, aggiungi nuovamente alla coda globale
                            codaGlobale.add(vertice);
                        }
                    }
                }
                
                latch.countDown();
            });
            
            workers[i].start();
        }
        
        // Attende il completamento di tutti i worker
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return visitati;
    }
}