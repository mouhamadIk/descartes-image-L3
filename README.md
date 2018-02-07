# Traitement d'image numériques
## Université Paris Descartes -  License 3

Ce repository est créé avec l'objectif d'aider aux étudiants à créer leur projets et comprendre les commandes nécessaires pour développer des application de traitement d'images.

Le code sera documenté en anglais mais les tutoriaux seront présentés en français.

## Installation du code
## 1 Télécharger le code
Pour télécharger le code de ce repository il suffit d'utiliser un outil de gestion de code Git (ex. SmartGit ou SourceTree). Ici, L'environnement de développement Eclipse est utilisé pour télécharger le code dans le disque dur.

1. Ouvrez Eclipse.
2. Activez la perspective git d'Eclipse

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot1.png)

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot2.png)

3. Copiez l'adresse du repository (`https://github.com/danyfel80/descartes-image-L3.git`). Puis, collez cette adresse dans Eclipse en faisant clic droit sur le panel gauche et sur l'option "Paste Repository Path or URI".

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot3.png)

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot4.png)

Sélectionnez la location du projet et puis cliquez sur _Finish_.

4. Retournez à la perspective Java

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot5.png)

## 2 Importer les projets sur Eclipse
Pour importer les projets téléchargés on utilisera Maven sur Eclipse.
1. Sélectionnez le menu File>Import....
2. Dans la section _Maven_ sélectionnez _Existing Maven Projects_.

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot6.png)

3. Cliquez _Next >_.
4. Sélectionnez la location du projet téléchargé (le dossier **license3.image**) et puis cliquez sur _Finish_.

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot7.png)

Le projet sera configuré et les dépendances seront téléchargés (ImageJ inclus).

## Différentes actions sur le projet
### Pour créer un nouveau sous-projet
Pendant les séances de TD plusieurs projets seront créés. Ici, on explique la création d'un nouveau projet d'ImageJ.
1. Créez un dossier avec le nom du projet dans le dossier **descartes-image-L3**.

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot8.png)

3. Créez un fichier _build.gradle_ à l'intérieur du dossier du nouveaux.

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot9.png)

2. Créez la structure de dossiers du projet (_[nomProjet]/src/main/java/_)

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot10.png)

3. Ajouter le projet dans le fichier _settings.gradle_ dans le dossier **descartes-image-L3**.

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot11.png)

4. Rafraichissez le projet **descartes-image-L3** sur Eclipse.

![alt text](https://raw.githubusercontent.com/danyfel80/descartes-image-L3/master/img/screenshot12.png)

Vous êtes prêt à coder!

### Inclure une dépendance dans les projets
Prochainement... ;-)
