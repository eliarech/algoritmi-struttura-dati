package com.unipd.cs.algorithms;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Implementazione di algoritmi di ordinamento paralleli utilizzando il framework Fork/Join di Java.
 * 
 * Questa classe confronta le prestazioni degli algoritmi di ordinamento sequenziali e paralleli
 * su array di diverse dimensioni, mostrando i vantaggi dell'elaborazione parallela.
 *
 * @author Università di Padova - Corso di Algoritmi Avanzati
 * @version 1.0
 * @since Maggio 2020
 */
public class ParallelSortingAlgorithms {
    private static final Random random = new Random();
    
    /**
     * Metodo principale per dimostrare gli algoritmi di ordinamento
     */
    public static void main(String[] args) {
        System.out.println("Algoritmi di Ordinamento Paralleli - Progetto Finale CS3242");
        System.out.println("================================================");
        
        // Test con varie dimensioni di array
        int[] dimensioni = {1000, 10000, 100000, 1000000};
        
        for (int dimensione : dimensioni) {
            System.out.println("\nTest con array di dimensione: " + dimensione);
            
            // Genera array casuale
            int[] dati = SortingAlgorithms.generaArrayCasuale(dimensione);
            
            // Crea copie per diversi metodi di ordinamento
            int[] datiQuickSortSequenziale = Arrays.copyOf(dati, dati.length);
            int[] datiQuickSortParallelo = Arrays.copyOf(dati, dati.length);
            int[] datiMergeSortParallelo = Arrays.copyOf(dati, dati.length);
            
            // Esegui e misura il tempo di ciascun algoritmo
            long tempoInizio, tempoFine;
            
            // Test QuickSort Sequenziale
            tempoInizio = System.currentTimeMillis();
            SortingAlgorithms.quickSort(datiQuickSortSequenziale, 0, datiQuickSortSequenziale.length - 1);
            tempoFine = System.currentTimeMillis();
            System.out.printf("Tempo QuickSort Sequenziale: %d ms, Ordinato: %s%n", 
                    (tempoFine - tempoInizio), SortingAlgorithms.èOrdinato(datiQuickSortSequenziale));
            
            // Test QuickSort Parallelo
            tempoInizio = System.currentTimeMillis();
            quickSortParallelo(datiQuickSortParallelo);
            tempoFine = System.currentTimeMillis();
            System.out.printf("Tempo QuickSort Parallelo: %d ms, Ordinato: %s%n", 
                    (tempoFine - tempoInizio), SortingAlgorithms.èOrdinato(datiQuickSortParallelo));
            
            // Test MergeSort Parallelo
            tempoInizio = System.currentTimeMillis();
            mergeSortParallelo(datiMergeSortParallelo);
            tempoFine = System.currentTimeMillis();
            System.out.printf("Tempo MergeSort Parallelo: %d ms, Ordinato: %s%n", 
                    (tempoFine - tempoInizio), SortingAlgorithms.èOrdinato(datiMergeSortParallelo));
        }
    }
    
    /**
     * Avvia il processo di QuickSort parallelo utilizzando il framework Fork/Join
     */
    public static void quickSortParallelo(int[] array) {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.invoke(new TaskQuickSortParallelo(array, 0, array.length - 1));
    }
    
    /**
     * Implementazione RecursiveAction per QuickSort parallelo
     */
    static class TaskQuickSortParallelo extends RecursiveAction {
        private static final int SOGLIA_SEQUENZIALE = 1000;
        private final int[] array;
        private final int inizio;
        private final int fine;
        
        TaskQuickSortParallelo(int[] array, int inizio, int fine) {
            this.array = array;
            this.inizio = inizio;
            this.fine = fine;
        }
        
        @Override
        protected void compute() {
            // Se la dimensione dell'array è abbastanza piccola, usa QuickSort sequenziale
            if (fine - inizio < SOGLIA_SEQUENZIALE) {
                SortingAlgorithms.quickSort(array, inizio, fine);
                return;
            }
            
            // Altrimenti, partiziona l'array e ordina in parallelo
            if (inizio < fine) {
                int indicePivot = partiziona(array, inizio, fine);
                
                // Crea task per i sottoarray
                TaskQuickSortParallelo taskSinistra = new TaskQuickSortParallelo(array, inizio, indicePivot - 1);
                TaskQuickSortParallelo taskDestra = new TaskQuickSortParallelo(array, indicePivot + 1, fine);
                
                // Esegui entrambi i task in parallelo
                invokeAll(taskSinistra, taskDestra);
            }
        }
        
        private int partiziona(int[] array, int inizio, int fine) {
            int pivot = array[fine];
            int i = inizio - 1;
            
            for (int j = inizio; j < fine; j++) {
                if (array[j] <= pivot) {
                    i++;
                    // Scambia array[i] e array[j]
                    SortingAlgorithms.scambia(array, i, j);
                }
            }
            
            // Scambia array[i+1] e array[fine]
            SortingAlgorithms.scambia(array, i + 1, fine);
            return i + 1;
        }
    }
    
    /**
     * Avvia il processo di MergeSort parallelo
     */
    public static void mergeSortParallelo(int[] array) {
        int[] temp = new int[array.length];
        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.invoke(new TaskMergeSortParallelo(array, temp, 0, array.length - 1));
    }
    
    /**
     * Implementazione RecursiveAction per MergeSort parallelo
     */
    static class TaskMergeSortParallelo extends RecursiveAction {
        private static final int SOGLIA_SEQUENZIALE = 1000;
        private final int[] array;
        private final int[] temp;
        private final int inizio;
        private final int fine;
        
        TaskMergeSortParallelo(int[] array, int[] temp, int inizio, int fine) {
            this.array = array;
            this.temp = temp;
            this.inizio = inizio;
            this.fine = fine;
        }
        
        @Override
        protected void compute() {
            // Se la dimensione dell'array è abbastanza piccola, usa MergeSort sequenziale
            if (fine - inizio < SOGLIA_SEQUENZIALE) {
                mergeSort(array, temp, inizio, fine);
                return;
            }
            
            // Calcola il punto medio
            int medio = inizio + (fine - inizio) / 2;
            
            // Crea task per i sottoarray
            TaskMergeSortParallelo taskSinistra = new TaskMergeSortParallelo(array, temp, inizio, medio);
            TaskMergeSortParallelo taskDestra = new TaskMergeSortParallelo(array, temp, medio + 1, fine);
            
            // Esegui entrambi i task in parallelo
            invokeAll(taskSinistra, taskDestra);
            
            // Fondi i sottoarray ordinati
            fondi(array, temp, inizio, medio, fine);
        }
    }
    
    /**
     * Implementazione sequenziale dell'algoritmo MergeSort
     */
    private static void mergeSort(int[] array, int[] temp, int inizio, int fine) {
        if (inizio < fine) {
            int medio = inizio + (fine - inizio) / 2;
            mergeSort(array, temp, inizio, medio);
            mergeSort(array, temp, medio + 1, fine);
            fondi(array, temp, inizio, medio, fine);
        }
    }
    
    /**
     * Fonde due sottoarray ordinati
     */
    private static void fondi(int[] array, int[] temp, int inizio, int medio, int fine) {
        // Copia i dati nell'array temporaneo
        for (int i = inizio; i <= fine; i++) {
            temp[i] = array[i];
        }
        
        int i = inizio;     // Indice iniziale del primo sottoarray
        int j = medio + 1;  // Indice iniziale del secondo sottoarray
        int k = inizio;     // Indice iniziale del sottoarray fuso
        
        // Fondi i due sottoarray
        while (i <= medio && j <= fine) {
            if (temp[i] <= temp[j]) {
                array[k++] = temp[i++];
            } else {
                array[k++] = temp[j++];
            }
        }
        
        // Copia gli elementi rimanenti del sottoarray sinistro
        while (i <= medio) {
            array[k++] = temp[i++];
        }
        
        // Nota: Non è necessario copiare gli elementi rimanenti dal sottoarray destro
        // perché sono già nelle loro posizioni corrette
    }
}