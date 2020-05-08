package com.unipd.cs.algorithms;

import java.util.Arrays;
import java.util.Random;

/**
 * Implementazione di algoritmi di ordinamento sequenziali di base.
 * 
 * Questa classe fornisce implementazioni di algoritmi di ordinamento classici
 * e metodi di supporto per generare e verificare array.
 *
 * @author Università di Padova - Corso di Algoritmi Avanzati
 * @version 1.0
 * @since Maggio 2020
 */
public class SortingAlgorithms {
    private static final Random random = new Random();
    
    public static void main(String[] args) {
        System.out.println("Dimostrazione Algoritmi di Ordinamento");
        
        // Genera array di test
        int[] arrayTest = generaArrayCasuale(1000);
        
        // Test QuickSort sequenziale
        int[] arrayQuickSort = Arrays.copyOf(arrayTest, arrayTest.length);
        long tempoInizio = System.currentTimeMillis();
        quickSort(arrayQuickSort, 0, arrayQuickSort.length - 1);
        long tempoFine = System.currentTimeMillis();
        
        System.out.println("Tempo QuickSort: " + (tempoFine - tempoInizio) + "ms");
        System.out.println("Array ordinato: " + èOrdinato(arrayQuickSort));
    }
    
    /**
     * Genera un array con interi casuali
     */
    public static int[] generaArrayCasuale(int dimensione) {
        int[] array = new int[dimensione];
        for (int i = 0; i < dimensione; i++) {
            array[i] = random.nextInt(10000);
        }
        return array;
    }
    
    /**
     * Verifica se un array è ordinato
     */
    public static boolean èOrdinato(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Implementazione sequenziale dell'algoritmo QuickSort
     */
    public static void quickSort(int[] array, int inizio, int fine) {
        if (inizio < fine) {
            // Partiziona l'array e ottieni l'indice del pivot
            int indicePivot = partiziona(array, inizio, fine);
            
            // Ordina ricorsivamente i sottoarray
            quickSort(array, inizio, indicePivot - 1);
            quickSort(array, indicePivot + 1, fine);
        }
    }
    
    /**
     * Metodo di partizionamento per QuickSort
     */
    private static int partiziona(int[] array, int inizio, int fine) {
        // Usa l'elemento più a destra come pivot
        int pivot = array[fine];
        int i = inizio - 1; // Indice dell'elemento più piccolo
        
        for (int j = inizio; j <= fine - 1; j++) {
            // Se l'elemento corrente è minore o uguale al pivot
            if (array[j] <= pivot) {
                i++;
                // Scambia array[i] e array[j]
                scambia(array, i, j);
            }
        }
        
        // Scambia array[i+1] e array[fine] (metti il pivot nella sua posizione corretta)
        scambia(array, i + 1, fine);
        return i + 1; // Restituisci la posizione del pivot
    }
    
    /**
     * Metodo di supporto per scambiare due elementi in un array
     */
    public static void scambia(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}