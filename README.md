# Algoritmi Paralleli e Sequenziali in Java

Questo progetto implementa e confronta vari algoritmi paralleli e sequenziali per operazioni comuni come moltiplicazione di matrici, ordinamento e attraversamento di grafi.

## Corso Algoritmi e Struttura Dati
**Università degli Studi di Padova**  
**Anno Accademico 2019/2020**

**Studente:** Elia Rech  
**Codice Matricola:** 1128352

## Struttura del Progetto

Il progetto è organizzato nei seguenti pacchetti:

- `com.unipd.cs.algorithms`: Contiene algoritmi di ordinamento
- `com.unipd.cs.algorithms.matrix`: Contiene algoritmi per moltiplicazione di matrici
- `com.unipd.cs.algorithms.graph`: Contiene algoritmi per l'attraversamento di grafi

## Algoritmi Implementati

### Moltiplicazione di Matrici
- Moltiplicazione sequenziale
- Moltiplicazione parallela con thread pool

### Algoritmi di Ordinamento
- QuickSort sequenziale
- QuickSort parallelo (con Fork/Join framework)
- MergeSort parallelo (con Fork/Join framework)

### Algoritmi per Grafi
- Breadth-First Search (BFS) sequenziale
- Breadth-First Search (BFS) parallelo
- Depth-First Search (DFS) sequenziale
- Depth-First Search (DFS) parallelo

## Requisiti

- Java 8 o superiore
- Maven (per la compilazione)

## Compilazione ed Esecuzione

Per compilare il progetto:

```bash
mvn clean compile
```

Per eseguire i test di benchmark:

```bash
mvn exec:java -Dexec.mainClass="com.unipd.cs.algorithms.matrix.MatrixMultiplication"
mvn exec:java -Dexec.mainClass="com.unipd.cs.algorithms.ParallelSortingAlgorithms"
mvn exec:java -Dexec.mainClass="com.unipd.cs.algorithms.graph.ParallelGraphAlgorithms"
```

## Risultati di Benchmark

I risultati variano in base all'hardware utilizzato. In generale, gli algoritmi paralleli mostrano un significativo miglioramento delle prestazioni rispetto alle versioni sequenziali, specialmente per input di grandi dimensioni.
