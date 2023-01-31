<p align="center">
  <img style="max-width: 800px;" src="resources/unibg.png">
</p>

# Progetto di ingegneria del software (rev. 4.0)
### Anno accademico 2022-2023

### Membri del team
- Lorenzo Magni, matricola 1073257
- Marianna Romelli, matricola 1072382
- Saif Bouchemal, matricola 1074800

## Indice
- __Il progetto__
- __Project plan__
  - Introduzione
  - Modello di processo
  - Organizzazione del progetto
  - Standards, linee guida e procedure
  - Attività di gestione
  - Rischi
  - Membri
  - Metodi e tecniche
  - Garanzie di qualità
  - Package di lavoro
  - Risorse
  - Budget e pianificazione
  - Cambiamenti
  - Consegna
- __Software lifecycle__
- __Configuration management__
  - Struttura del progetto
  - Issues
  - Branches
- __People management__
- __Software quality__
  - Parametri riguardanti l'operatività del software
  - Parametri riguardanti la revisione del software
  - Parametri riguardanti la transizione verso un nuovo ambiente
- __Requirement engineering__
- __Modelling__
  - State machine diagram
  - Use case diagram
  - Class diagram
  - Sequence diagram
  - Acrtivity diagram
- __Software architecture__
- __Software design__
- __Software testing e manutenibilità__

## Il progetto
Realizzazione in Java di un gioco di ruolo multiplayer online basato sul noto gioco da tavolo [Lupus in Tabula](https://www.dvgiochi.com/catalogo/lupus-in-tabula).<br>
I giocatori sono divisi in due gruppi principali: i villici che hanno come scopo riconoscere e uccidere i lupi mannari e i lupi mannari che hanno come scopo divorare tutti i villici senza farsi scoprire.

### Guida rapida al gioco
#### Personaggi
- con __8 giocatori__ si hanno: 5 villici, 2 lupi mannari e 1 veggente
- con __9 o più giocatori__ si aggiungono carte a sufficienze scegliendo altri villici e/o personaggi speciali indicati di seguito
- con __16 o più giocatori__ si aggiungie un terzo lupo mannaro e carte a sufficienza scegliendo altri villici e/o personaggi speciali indicati di seguito

#### Ruoli
- __Villico__: durante il giorno, i villici votano per linciare un giocatore, durante la notte dormono.
- __Lupo mannaro__ (2-3): durante la notte divorano un villico, durante il giorno si comportano come normali villici.
- __Veggente__ (1): durante la notte può vedere se un giocatore è o meno un lupo mannaro.
- __Medium__ (1): durante la notte può vedere se il giocatore linciato il giorno prima era o no un lupo mannaro.
- __Indemoniato__ (1): è un umano ma parteggia per i lupi mannari senza sapere chi siano. Vince se vincono i lupi mannari.
- __Guardia del corpo__ (1): durante la notte può proteggere un giocatore da un attacco dei lupi mannari.
- __Gufo__ (1): durante la notte può può scegliere un giocatore da mandare al linciaggio. Giocando con 20+ giocatori, il gufo diventa "letale". Se il giocatore scelto non era un Mannaro (Lupo o Criceto) muore all'inizio del giorno.
- __Massone__ (2): durante la prima notte si svegliano e si riconoscono.
- __Criceto mannaro__ (1): il criceto mannaro non appartiene a nessuna squadra, gioca per sé stesso. Vince se sopravvive fino alla fine del gioco. Se visto dal medium risulta come umano, se visto dal veggente di notte muore insieme allo sbranato. Il criceto mannaro non può essere sbranato dai lupi.
- __Mitomane__ (1): alla fine della seconda notte il mitomane decide un giocatore da imitare. Se il giocatore scelto è un lupo mannaro diventa anch'esso un lupo mannaro, se era un veggente diventa anch'esso un veggente, altrimenti diventa un villico normale.

#### Svoglimento del gioco
Il gioco prevede due fasi che si alternano: il giorno e la notte. Durante il giorno i giocatori vivi discutono su chi siano i lupi mannari per linciarli, durante la notte invece il narratore (server) chiama i vari ruoli speciali che hanno la possibilità di compiere azioni. Il gioco termina quando i villici sono riusciti ad uccidere tutti i lupi mannari o quando i lupi mannari sono riusciti a rimanere in numero uguale a quello dei villici.<br>
Il giorno è diviso a sua volta in due fasi: la discussione, durante la quale i giocatori vivi discutono su chi possa essere un lupo mannaro e che termina con la votazione per scegliere i due giocatori che saranno mandati al linciaggio, e il linciaggio durante il quale i due giocatori accusati dovranno difendersi e uno dei due sarà votato e linciato.<br><br>

Per maggiori informazioni sul gioco è possibile consultare il manuale di gioco ufficiale disponibile [qui](http://www.laruzzoteca.it/ruzzoteca/upload/istruzioni/100.pdf).

## 1. Project plan
Si sviluppa il gioco pensando ad una community generica di videogiocatori. Nella fase iniziale del lavoro i requisiti sono decisi all'interno del team di sviluppo, mentre nel momento in cui l'applicazione sarà resa pubblica, gli utenti avranno una parte attiva nel suggerire modifiche e miglioramenti nonchè di segnalare eventuali bug attraverso le piattaforme social e/o issue di GitHub raggiungibili anche dall'interno del gioco.<br>

L'azienda che si occupa della produzione è una società di sviluppo software costituita da Lorenzo Magni, Marianna Romelli e Saif Bouchemal ed è stata costituita con il fine di progettare e sviluppare il software.<br>
L'obiettivo del lavoro è di pubblicare il gioco sulle piattaforme di redistribuzione di videogiochi, quali Steam o Epic Games, e/o renderlo disponibile per eventuali investitori interessati a finanziare il progetto.<br>

Inizialmente il lavoro del team di sviluppo sarà non salariato, in seguito alla distribuzione del gioco i guadagni ricavati dalle vendite e/o dagli investimenti verranno ripartiti tra i membri sulla base delle ore di lavoro svolte.

### 1.1 Introduzione
Dopo un'attenta analisi dei requisiti ideati dal team ponendosi nell'ottica di un possibile giocatore, sono stati stabiliti i passaggi da seguire al fine di realizzare una prima versione del sistema richiesto.<br>
Alla progettazione del gioco prenderà parte l'intero team, successivamente per la fase di sviluppo e scrittura di questa documentazione verranno stabiliti i ruoli e le responsabilità dei singoli membri.<br>

Durante la prima riunione operativa si è deciso di sviluppare un'applicazione desktop che implementi le seguenti funzionalità:
- Sviluppo di un gioco multigiocatore online
- Il gioco dovrà possedere una grafica piacevole, avere una chat testuale e possibilmente vocale
- Il gioco dovrà supportare più lingue
- Il gioco dovrà essere disponibile per Windows, Mac e Linux

All'interno della stessa si è stabilito che per tutto il periodo di progettazione e sviluppo si sarebbero tenuti degli incotri settimanali in modo da favorire la comunicazione e collaborazione tre i membri del team, nonchè per discutere sugli obiettivi e avere un feedback dell'intero gruppo di lavoro riguardo alle modifiche apportete.<br>
Si è inoltre posta come deadline per il primo prototipo il 30/12/2022 mentre per la versione alpha il 10/02/2023.<br>
In seguito alla pubblicazione del gioco in versione alpha sarà data la possibilità agli utenti di giocare gratuitamente per un periodo di tempo limitato e di mandare feedback riguardo al gameplay ed eventuali implementazioni e segnalarci eventuali bug.

### 1.2 Modello di processo
Per il processo di sviluppo del gioco si è deciso di sfruttare un approccio di tipo *agile*, infatti, si è ritenuto che questo fosse più adatto per un progetto di questo tipo in cui la documentazione non è essenziale per l'utente finale, ossia i giocatori.<br>
Il processo di sviluppo sarà strutturato nel seguente modo:
 - __Fase di sviluppo della versione alpha__
   Durante questa fase verrà sviluppata una prima versione del gioco con tutte le funzionalità di base necessarie al gameplay.
 - __Fase di manutenzione e aggiornamento__
   Durante questa seconda fase ci si occuperà di correggere eventuali bug e implementare nuove funzionalità del gioco.<br>
   Gli aggiornamenti potranno essere di due tipi: aggiornamenti minori che correggono bug e/o implementano nuove funzionalità minori, e aggiornamenti maggiori che implementano nuove funzionalità principali.

Come piattaforma di Version Control System si è scelto di sfruttare GitHub, in quanto è la piattaforma più utilizzata per lo sviluppo software ed è nota anche dagli utenti, inoltre permette di sfruttare le funzionalità di issue tracking, branch e wiki.<br>
Oltre allo scopo di bug tracker, si è deciso di sfruttare le *issue* per assegnare i task da svolgere ai membri del team e per tenere traccia dello stato di avanzamento del lavoro svolto. Una issue può essere chiusa solo in seguito alla conclusione del task (o alla correzione del bug) e al relativo aggiornamento del repository.

### 1.3 Organizzazione del progetto
Le persone coinvolte nella progettazione del gioco inizialmente sono quelle che compongono il team. In seguito alla pubblicazione, i giocatori potranno prendere parte allo sviluppo suggerendo nuove funzionalità o segnalando bug, o, se lo desiderano, potranno contribuire direttamente allo sviluppo facendo un *fork* del repository, implementando le loro modifiche e aprendo una *pull request*. Affinché le modifiche entrino a far parte del gioco dovranno essere revisionate e approvate dal team di sviluppo.<br>
Il team si incontrerà settimanalmente, o di persona o attraverso videoconferenza, per fare il punto sullo stato dei lavori e pianificare le attività da svolgere per la settimana successiva.

### 1.4 Standards, linee guida e procedure
Il gioco è composto da due applicativi separati:
- Server: Gestisce l'andamento di una o più partite, non possiede un'interfaccia grafica ed è compatibile con i più diffusi sistemi operativi (anche ARM)
- Client: Gestisce l'interfaccia grafica e l'interazione con l'utente

Sia server che client sono scritti in Java e per la comunicazione utilizzano la libreria open source [MCLib-Network](https://github.com/MultiCoreNetwork/MCLib) sviluppata da Lorenzo Magni e basata a sua volta sulla libreria [Netty](https://netty.io) per la comunicazione in rete tramite pacchetti TCP.<br>
Per la gestione dei file di configurazione e delle traduzioni si sfruttano file json mediante la libreria [Gson](https://github.com/google/gson) sviluppata da Google.<br>
Per la grafica si è deciso di utilizzare la libreria [JCEF](https://github.com/chromiumembedded/java-cef) che permette di integrare all'interno di una finestra Swing un browser *Chromium*. Si è deciso di utilizzare questo tipo di approccio in modo da semplificare lo sviluppo dell'interfaccia grafica mediante l'uso dei linguaggi web quali HTML, CSS e JavaScript e permettere una facile modifica (anche da parte degli utenti) delle interfacce del gioco. La scelta dei linguaggi web permette inoltre di avere un rescaling automatico delle interfacce in base alle dimensioni della finestra.<br>
Sempre per la grafica sono state utilizzate le librerie CSS/JS [Bootstrap](https://getbootstrap.com) e [jQuery](https://jquery.com) e [Font Awesome](https://fontawesome.com/) gestite localmente.

### 1.5 Attività di gestione
Si è deciso di organizzare lo sviluppo secondo la filosofia dell'*extreme programming* in quanto permette di migliorare la qualità del codice e approcciarsi al cambiamento dei requisiti in modo più responsivo.<br>
Sulla base di questo si è deciso di dedicare del tempo alla lettura del codice scritto dagli altri membri del team come metodo di validatzione, inoltre si è deciso di applicare l'approccio del *pair programming* per la scrittura del codice, con il quale si hanno coppie di sviluppatori che lavorano insieme su un singolo frammento di codice e si dividono il lavoro di scrittura e revisione in tempo reale.<br>
Gli incontri settimanali sono di breve durata, all'incirca 60 minuti, durante i quali possono essere effettuate delle sessioni di *brain storming* per trovare nuove idee o cercare soluzioni a problemi riscontrati.<br>
Ad ogni incontro, i membri del team dovranno esporre brevemente quali sono stati i task portati avanti durante la settimana antecedente e rendere noti agli altri eventuali problemi riscontrati.<br>
Al termine del meeting ci si accorda sui compiti da portare avanti durante le settimane successive.