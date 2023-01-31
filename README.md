![Logo](docs/resources/logo.png)
## Progetto di ingegneria del software
### Anno accademico 2022-2023
![GitHub version](https://img.shields.io/badge/alpha-0.1.0-blue)
![GitHub issues](https://img.shields.io/github/issues/LoreSchaeffer/LupusInTabula-SoftEng)
![Last commit](https://img.shields.io/github/last-commit/LoreSchaeffer/LupusInTabula-SoftEng)

### Progetto
Realizzazione in Java di un gioco di ruolo multiplayer online basato sul noto gioco da tavolo [Lupus in Tabula](https://www.dvgiochi.com/catalogo/lupus-in-tabula).

### Membri del team
- Lorenzo Magni, matricola 1073257
- Marianna Romelli, matricola 1072382
- Saif Bouchemal, matricola 1074800

## Organizzazione del repository
- __branch__:
  - `main`: Contiene le versioni stabili del codice e la documentazione
  - `dev`: Contiene le versioni in via di sviluppo del codice
- __struttura__:
  - `code`: Contiene il codice sorgente del progetto. È diviso in 3 moduli:
    + *common*: Modulo contenente le classi comuni a client e server
    + *client*: Modulo contenente le classi del client e le risorse grafiche
    + *server*: Modulo contenente le classi del server headless
- __docs__: Contiene la documentazione del progetto
- __.github/workflows__: Utilizzato per la configurazione dei workflow di GitHub Actions, testing, compilazione e distribuzione automatica

### Requisiti
- __[Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)__

### Guida all'uso

##### Client
1. Scaricare il file `LupusInTabula-Client.jar` dalle [release](https://github.com/LoreSchaeffer/LupusInTabula-SoftEng/releases).
2. Avviare il client con un doppio click o con il comando ```java -jar LupusInTabula-Client.jar```.
3. Attendere il download delle librerie native. Se si vuole utilizzare un server diverso da quello di default, modificare il file `conf/config.json` inserendo il server desiderato e riavviare il client per applicare le modifiche.

##### Server
1. Scaricare il file `LupusInTabula-Server.jar` dalle [release](https://github.com/LoreSchaeffer/LupusInTabula-SoftEng/releases).
2. Avviare il server con il comando ```java -jar LupusInTabula-Server.jar```.
3. Attendere l'avvio del programma. Se necessario modificare le impostazioni del server modificando il file `conf/config.json` e riavviare il server per applicare le modifiche.

### Contribuire
Per contribuire a questo progetto forkare il repository, applicare le proprie modifiche e aprire una pull request.
La pull request verrà poi valutata e, se approvata, verrò unita al codice principale.

### Licenza
Questo programma è rilasciato sotto licenza BSD-3-Clause. Per maggiori informazioni consultare il file [LICENSE](LICENSE).