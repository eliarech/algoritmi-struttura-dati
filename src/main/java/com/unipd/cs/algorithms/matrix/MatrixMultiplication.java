package com.unipd.cs.algorithms.matrix;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementazione di moltiplicazione matriciale sequenziale e parallela.
 * 
 * Questo algoritmo dimostra come la parallelizzazione può migliorare significativamente
 * le prestazioni per operazioni con matrici di grandi dimensioni, suddividendo
 * il carico di lavoro tra più thread.
 * 
 * @author Università di Padova - Corso di Algoritmi Avanzati
 * @version 1.0
 * @since Maggio 2020
 */
public class MatrixMultiplication {
    private static final Random random = new Random();
    
    /**
     * Metodo principale per dimostrare gli algoritmi di moltiplicazione matriciale.
     */
    public static void main(String[] args) {
        System.out.println("Dimostrazione di Moltiplicazione Matriciale");
        
        int[] dimensioni = {100, 500, 1000};
        
        for (int dimensione : dimensioni) {
            System.out.println("\nTest con matrice di dimensione: " + dimensione + "x" + dimensione);
            
            // Genera matrici casuali
            int[][] A = generaMatriceCasuale(dimensione, dimensione);
            int[][] B = generaMatriceCasuale(dimensione, dimensione);
            
            // Moltiplicazione sequenziale
            long tempoInizio = System.currentTimeMillis();
            int[][] C1 = moltiplicazioneSequenziale(A, B);
            long tempoFine = System.currentTimeMillis();
            System.out.println("Moltiplicazione sequenziale: " + (tempoFine - tempoInizio) + "ms");
            
            // Moltiplicazione parallela
            tempoInizio = System.currentTimeMillis();
            int[][] C2 = moltiplicazioneParallela(A, B, Runtime.getRuntime().availableProcessors());
            tempoFine = System.currentTimeMillis();
            System.out.println("Moltiplicazione parallela: " + (tempoFine - tempoInizio) + "ms");
            
            // Verifica risultati
            System.out.println("Risultati corrispondenti: " + matriciUguali(C1, C2));
        }
    }
    
    /**
     * Genera una matrice casuale con dimensioni specificate.
     */
    public static int[][] generaMatriceCasuale(int righe, int colonne) {
        int[][] matrice = new int[righe][colonne];
        for (int i = 0; i < righe; i++) {
            for (int j = 0; j < colonne; j++) {
                matrice[i][j] = random.nextInt(10); // Valori piccoli per evitare overflow
            }
        }
        return matrice;
    }
    
    /**
     * Controlla se due matrici sono uguali.
     */
    public static boolean matriciUguali(int[][] A, int[][] B) {
        if (A.length != B.length || A[0].length != B[0].length) {
            return false;
        }
        
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                if (A[i][j] != B[i][j]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Moltiplicazione matriciale sequenziale.
     */
    public static int[][] moltiplicazioneSequenziale(int[][] A, int[][] B) {
        int righeA = A.length;
        int colonneA = A[0].length;
        int colonneB = B[0].length;
        
        int[][] C = new int[righeA][colonneB];
        
        for (int i = 0; i < righeA; i++) {
            for (int j = 0; j < colonneB; j++) {
                for (int k = 0; k < colonneA; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        
        return C;
    }
    
    /**
     * Moltiplicazione matriciale parallela utilizzando la classe Thread.
     */
    public static int[][] moltiplicazioneParallela(int[][] A, int[][] B, int numThread) {
        int righeA = A.length;
        int colonneA = A[0].length;
        int colonneB = B[0].length;
        
        final int[][] C = new int[righeA][colonneB];
        
        // Crea pool di thread
        ExecutorService executor = Executors.newFixedThreadPool(numThread);
        
        // Divide il lavoro tra i thread
        int righePerThread = Math.max(1, righeA / numThread);
        
        for (int t = 0; t < numThread; t++) {
            final int idThread = t;
            
            executor.submit(() -> {
                // Calcola righe iniziali e finali per questo thread
                int rigaInizio = idThread * righePerThread;
                int rigaFine = (idThread == numThread - 1) ? righeA : Math.min(rigaInizio + righePerThread, righeA);
                
                // Esegue moltiplicazione per le righe assegnate
                for (int i = rigaInizio; i < rigaFine; i++) {
                    for (int j = 0; j < colonneB; j++) {
                        for (int k = 0; k < colonneA; k++) {
                            C[i][j] += A[i][k] * B[k][j];
                        }
                    }
                }
            });
        }
        
        // Attende il completamento di tutti i thread
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return C;
    }
}