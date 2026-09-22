# MobUb

Application de mobilité pour le campus : calcul d'itinéraires, suivi des transports en commun (TBM, TER), stations de vélos/vélo-partage, véhicules en libre-service, qualité de l'air et météo. L'application est composée de trois briques :

- **Backend** : API Spring Boot
- **OTP (OpenTripPlanner)** : moteur de calcul d'itinéraires multimodaux
- **Base de données** : PostgreSQL + PostGIS
- **Application mobile** : Android natif (Kotlin, Retrofit)

Ce document explique comment installer et lancer le projet en local ou sur un serveur, étape par étape.

---

## 1. Prérequis

Avant de commencer, installe sur ta machine (ou ton serveur) :

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/) (inclus avec Docker Desktop)
- [Android Studio](https://developer.android.com/studio) (uniquement si tu veux compiler l'application mobile)
- Git

Vérifie que tout est bien installé :

```bash
docker --version
docker compose version
git --version
```

---

## 2. Récupérer le projet

```bash
git clone https://github.com/ybenayed/ObservatoireCampusBordeaux
cd ObservatoireCampusBordeaux
```

---

## 3. Configurer les variables d'environnement

Le projet a besoin de plusieurs secrets (mots de passe, tokens, clés). Ils ne sont **jamais** stockés dans le dépôt Git.

À la racine du projet (là où se trouve `docker-compose.yml`), crée un fichier `.env` :

```bash
touch .env
```

Ouvre-le et renseigne les valeurs suivantes :

```env
DB_USER=ton_utilisateur_postgres
DB_PASSWORD=ton_mot_de_passe_postgres
NAVITIA_TOKEN=ton_token_navitia
MAIL_USERNAME=ton_adresse_gmail
MAIL_PASSWORD=mot_de_passe_application_gmail
JWT_SECRET=une_chaine_secrete_longue_et_aleatoire
ADMIN_PASSWORD=mot_de_passe_du_compte_admin
```

> ⚠️ Ne partage jamais ce fichier `.env` et ne le commite jamais dans Git. Ajoute `.env` à ton fichier `.gitignore` pour éviter tout risque d'oubli.

---

## 4. Construire les données OpenTripPlanner (OTP)

OTP a besoin de construire un "graphe" à partir des données de transport (GTFS, OSM) avant de pouvoir servir des itinéraires. Cette étape ne se fait qu'une seule fois (ou à chaque mise à jour des données) :

```bash
docker compose --profile build up otp-build
```

Attends que le conteneur termine et s'arrête tout seul — ça peut prendre plusieurs minutes selon la taille des données.

---

## 5. Lancer l'application

Une fois le graphe OTP construit, démarre les trois services principaux :

```bash
docker compose up -d db otp backend
```

- `-d` lance les conteneurs en arrière-plan (detached).
- `db` : la base PostgreSQL/PostGIS
- `otp` : le serveur de calcul d'itinéraires (port `8081`)
- `backend` : l'API Spring Boot (port `8080`)

Pour vérifier que tout tourne correctement :

```bash
docker compose ps
```

Pour suivre les logs en direct (utile en cas de problème) :

```bash
docker compose logs -f backend
```

(remplace `backend` par `otp` ou `db` pour voir les logs d'un autre service)

---

## 6. Arrêter l'application

```bash
docker compose down
```

Pour tout arrêter **et supprimer les données de la base** (à utiliser avec précaution) :

```bash
docker compose down -v
```

---

## 7. Lancer l'application mobile (Android)

1. Ouvre le dossier du projet Android dans **Android Studio**.
2. Vérifie que `BASE_URL` (dans `BuildConfig` / `build.gradle.kts`) pointe bien vers l'adresse du backend :
   - En local : `http://10.0.2.2:8080/` (adresse spéciale de l'émulateur Android pour joindre ta machine)
3. Lance l'application via le bouton **Run**  sur un émulateur ou un appareil physique.

---

## 9. Architecture rapide

```
[ Application Android ] --> [ Backend Spring Boot :8080 ] --> [ PostgreSQL/PostGIS :5433 ]
                                        |
                                        v
                              [ OpenTripPlanner :8081 ]
```

---

## 10. Problèmes fréquents

- **Le backend ne démarre pas** : vérifie que le fichier `.env` existe et contient bien toutes les variables demandées.
- **OTP ne répond pas** : assure-toi que l'étape 4 (construction du graphe) a bien été faite avant de lancer `otp`.
- **La base de données refuse la connexion** : vérifie les identifiants dans `.env` et que le conteneur `db` est bien "healthy" (`docker compose ps`).