# TimeLineGame

Timeline est une application de jeu développée en Java / JavaFX dans le cadre de la SAE 2.01. Le joueur doit placer des cartes représentant des événements sur une frise chronologique. Le jeu propose un mode solo ou deux joueurs, un créateur de decks personnalisés, ainsi qu’un système de gestion et d’édition des cartes.

Le projet propose :

- Un mode **solo**
- Un mode **deux joueurs**
- Un **créateur de decks** personnalisables
- Une interface graphique intuitive

---

## Fonctionnalités

### Joueur

- Configuration d’une nouvelle partie (solo ou duo)
- Choix du deck
- Choix du temps de réflexion (optionnel)
- Visualisation :
  - des cartes en main
  - de la frise chronologique
  - du score des joueurs
  - du temps restant
- Placement de cartes sur la frise
- Feedback immédiat sur le placement
- Affichage du gagnant en fin de partie

Certaines fonctionnalités sont partiellement implémentées :

- Sauvegarde automatique de la partie
- Reprise d’une partie non terminée
- Déplacement d’une carte mal placée
- Affichage du nombre de cartes restantes dans le deck

---

### Créateur de decks

- Création de decks personnalisés
- Suppression et renommage de decks
- Ajout, modification et suppression de cartes
- Modification :
  - du nom
  - de la date
  - de la description
  - de l’image (via URL)
- Gestion des decks via fichiers JSON

---

## Technologies utilisées

- **Java**
- **JavaFX**
- **FXML**
- **JSON** (stockage des decks et cartes)
- **Scene Builder** (interfaces)
- **Git / GitHub**

---

## Architecture du projet

- **Vues FXML** pour l’interface graphique
- **Contrôleurs JavaFX** pour la logique
- **Modèles** pour la gestion des cartes, decks et parties
- **Fichiers JSON** pour la persistance des données

---

## Équipe

Projet réalisé en quadrinôme :

- **Marc Foucher**
- **Antoine Rioul**
- **Ethan Poirrier**
- **Gabriel Chalmel-Toussin**

Travail réparti entre :

- Interface graphique
- Logique de jeu (solo / duo)
- Gestion des decks et cartes
- Système de placement et vérification
- Rédaction du rapport

---

## Lancer le projet

1. Cloner le dépôt :

   ```bash
   git clone <url-du-repo>
   ```

2. Si vous essayez l'executable et qu'il ne fonctionne pas, suivez les étapes suivantes :
   Téléchargez le Java développeur via ce lien : https://www.oracle.com/java/technologies/downloads/
   Puis relancez l'executable
