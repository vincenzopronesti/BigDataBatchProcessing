# BigDataBatchProcessing
Repository for a big data project con batch processing.

#### SABD - Progetto 1
Il codice fornito utilizza Apache Flume, HDFS e Apache Spark per rispondere alle query:
1. Per ogni settimana, calcolare il numero medio di guariti e dei tamponi effettuati in Italia 	in quella settimana.
2. Per ogni continente, calcolare la media, la deviazione standard, il minimo e il massimo del numero di casi confermati giornalmente per ogni settimana. Nel calcolo delle statistiche, considerare solo i 100 stati più colpiti dalla pandemia. Qualora lo stato non fosse indicato, 	considerare la nazione. Per determinare gli stati più colpiti nell’intero dataset, si consideri l’andamento degli incrementi giornalieri dei casi confermati attraverso il trendline coefficient. Per stimare il trendline coefficient, si calcoli la pendenza della retta di regressione che approssima la tendenza degli incrementi giornalieri. 

#### Esecuzione del codice
L'architettura viene eseguita in locale. Per Flume e HDFS sono stati utilizzati dei container Docker. 
* Dall'interno della directory `ingestion` fare il build (`docker build -t hdfsflume .`) del container Docker che contiene Apache Flume con le configurazioni degli agenti che eseguiranno l'ingestion dei dati.
* Con il comando `sh start-docker.sh` si avvia il cluster HDFS. Il master del cluster HDFS contiene Apache Flume.
* Con il comando precedente viene avviato il terminale del master. Eseguendo il comando `sh inputDir/driver.sh` viene avviato HDFS, vengono create le directory dove verrà effettuato l'ingestion dei dati, e dove verranno salvati i dati di output. Inoltre si consente all'utente, che esegue l'applicazione Spark, di accedere alle directory dove verrà scritto l'output.
* A questo punto si può avviare l'agente Flume per effettuare l'ingestion dei dati della prima query tramite il comando `flume-ng agent -n q1agent -c /usr/local/flume/apache-flume-1.9.0-bin/conf/ -f /usr/local/flume/apache-flume-1.9.0-bin/conf/flume.conf`
* Inserire il file con i dati della prima query nella directory `ingestion/tmp/query1_input_data/`
* Eseguire il codice contenuto nella classe Query1
* I risultati prodotti vengono salvati nella directory `query1/res/`
* In modo analogo, per la seconda query si avvia l'agente Flume tramite `flume-ng agent -n q2agent -c /usr/local/flume/apache-flume-1.9.0-bin/conf/ -f /usr/local/flume/apache-flume-1.9.0-bin/conf/flume.conf`
* Inserire il file con i dati della seconda query nella directory `ingestion/tmp/query2_input_data/`
* Eseguire il codice contenuto nella classe Query2
* I risultati prodotti vengono salvati nella directory `query2/res/`