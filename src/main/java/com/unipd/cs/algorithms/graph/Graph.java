package com.unipd.cs.algorithms.graph;

import java.util.*;

/**
 * Rappresentazione di un grafo utilizzando una lista di adiacenza.
 * 
 * Questa classe implementa una struttura dati di grafo e fornisce metodi
 * per attraversare il grafo sia in modo sequenziale che parallelo.
 * 
 * @author Università di Padova - Corso di Algoritmi Avanzati
 * @version 1.0
 * @since Maggio 2020
 */
public class Graph {
    private final int vertici;
    private final List<Integer>[] listaAdiacenza;
    
    /**
     * Costruttore per creare un grafo con un numero specificato di vertici.
     */
    @SuppressWarnings("unchecked")
    public Graph(int vertici) {
        this.vertici = vertici;
        listaAdiacenza = new ArrayList[vertici];
        for (int i = 0; i < vertici; i++) {
            listaAdiacenza[i] = new ArrayList<>();
        }
    }
    
    /**
     * Aggiunge un arco diretto dalla sorgente alla destinazione.
     */
    public void addEdge(int sorgente, int destinazione) {
        listaAdiacenza[sorgente].add(destinazione);
    }
    
    /**
     * Restituisce la lista dei vicini per un dato vertice.
     */
    public List<Integer> getNeighbors(int vertice) {
        return listaAdiacenza[vertice];
    }
    
    /**
     * Restituisce il numero di vertici nel grafo.
     */
    public int getVerticesCount() {
        return vertici;
    }
    
    /**
     * Implementazione sequenziale della ricerca in ampiezza (BFS).
     */
    public Set<Integer> bfs(int verticeIniziale) {
        Set<Integer> visitati = new HashSet<>();
        Queue<Integer> coda = new LinkedList<>();
        
        visitati.add(verticeIniziale);
        coda.add(verticeIniziale);
        
        while (!coda.isEmpty()) {
            int vertice = coda.poll();
            
            for (int vicino : getNeighbors(vertice)) {
                if (!visitati.contains(vicino)) {
                    visitati.add(vicino);
                    coda.add(vicino);
                }
            }
        }
        
        return visitati;
    }
    
    /**
     * Implementazione sequenziale della ricerca in profondità (DFS).
     */
    public Set<Integer> dfs(int verticeIniziale) {
        Set<Integer> visitati = new HashSet<>();
        Stack<Integer> pila = new Stack<>();
        
        pila.push(verticeIniziale);
        
        while (!pila.isEmpty()) {
            int vertice = pila.pop();
            
            if (!visitati.contains(vertice)) {
                visitati.add(vertice);
                
                // Aggiunge vicini in ordine inverso per garantire lo stesso attraversamento del DFS ricorsivo
                List<Integer> vicini = getNeighbors(vertice);
                for (int i = vicini.size() - 1; i >= 0; i--) {
                    int vicino = vicini.get(i);
                    if (!visitati.contains(vicino)) {
                        pila.push(vicino);
                    }
                }
            }
        }
        
        return visitati;
    }
    
    /**
     * Genera un grafo casuale connesso per i test.
     */
    public static Graph generateRandomGraph(int vertici, double probabilitaArco) {
        if (probabilitaArco < 0 || probabilitaArco > 1) {
            throw new IllegalArgumentException("La probabilità dell'arco deve essere tra 0 e 1");
        }
        
        Graph grafo = new Graph(vertici);
        Random random = new Random();
        
        // Prima assicura che il grafo sia connesso creando un percorso attraverso tutti i vertici
        for (int i = 0; i < vertici - 1; i++) {
            grafo.addEdge(i, i + 1);
        }
        
        // Aggiunge archi casuali
        for (int i = 0; i < vertici; i++) {
            for (int j = 0; j < vertici; j++) {
                if (i != j && random.nextDouble() < probabilitaArco) {
                    grafo.addEdge(i, j);
                }
            }
        }
        
        return grafo;
    }
}