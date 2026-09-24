# MobUB

Application de mobilité pour le campus de Bordeaux : calcul d'itinéraires multimodaux, transports en commun (TBM, TER), vélos en libre-service (VCub) et free-floating, voiture avec parkings, marche, qualité de l'air et météo.

Ce document explique comment installer et lancer le projet **en local**, étape par étape.

## Architecture

Le projet est composé de quatre briques :

- **Application mobile** : Android natif (Kotlin)
- **Backend** : API Spring Boot
- **OTP (OpenTripPlanner)** : moteur de calcul d'itinéraires multimodaux
- **Base de données** : PostgreSQL + PostGIS

```
[ Application Android ] --> [ Backend Spring Boot :8080 ] --> [ PostgreSQL/PostGIS :5433 ]
                                        |
                                        v
                              [ OpenTripPlanner :8081 ]
```

Les données utilisées par OTP :

| Mode | Source des données |
|------|--------------------|
| Marche, vélo perso, voiture, parkings | OpenStreetMap (Aquitaine, Geofabrik) |
| Tram, bus, bateau | GTFS TBM (Bordeaux Métropole) |
| TER | GTFS TER (SNCF) |
| VCub | GBFS Bordeaux Métropole |
| Vélos et trottinettes free-floating | GBFS Pony et Dott |



## 1. Prérequis

- [Docker Engine](https://docs.docker.com/engine/install/) avec le plugin **Docker Compose v2**
- Git, `curl`
- [Android Studio](https://developer.android.com/studio) (uniquement pour compiler l'application mobile)
- **Matériel** : 8 Go de RAM minimum et environ 5 Go de disque libre. La construction du graphe OTP est la partie la plus gourmande (voir la section *Dépannage* si la mémoire est juste).

Vérifie l'installation :

```bash
docker --version
docker compose version
git --version
```

Si `docker compose version` répond `unknown command`, installe le plugin (Ubuntu) : `sudo apt install -y docker-compose-v2`.

---

## 2. Récupérer le projet

```bash
git clone https://github.com/ybenayed/ObservatoireCampusBordeaux
cd ObservatoireCampusBordeaux
```

---

## 3. Configurer les variables d'environnement

Les secrets ne sont **jamais** stockés dans Git. À la racine du projet (là où se trouve `docker-compose.yml`), crée un fichier `.env` :

```env
DB_USER=ton_utilisateur_postgres
DB_PASSWORD=ton_mot_de_passe_postgres
NAVITIA_TOKEN=ton_token_navitia
MAIL_USERNAME=ton_adresse_gmail
MAIL_PASSWORD=mot_de_passe_application_gmail
JWT_SECRET=une_chaine_secrete_longue_et_aleatoire
ADMIN_PASSWORD=mot_de_passe_du_compte_admin
```

---

## 4. Préparer les données OpenTripPlanner

Les gros fichiers de données ne sont pas dans le dépôt : tu les télécharges une fois, puis OTP construit un « graphe » à partir de ces données.

### 4.1 Télécharger les données

```bash
mkdir -p otp-service/otp-data
cd otp-service/otp-data

# Carte OpenStreetMap de l'Aquitaine 
curl -L -o aquitaine.osm.pbf https://download.geofabrik.de/europe/france/aquitaine-latest.osm.pbf

# Horaires TBM (tram, bus, bateau)
curl -L -o gtfs-tbm.zip "https://bdx.mecatran.com/utw/ws/gtfsfeed/static/bordeaux?apiKey=opendata-bordeaux-metropole-flux-gtfs-rt"

# Horaires TER (SNCF)
curl -L -o gtfs-ter.zip https://eu.ftp.opendatasoft.com/sncf/plandata/export-ter-gtfs-last.zip

# Vérifier que les archives ne sont pas corrompues
unzip -t gtfs-tbm.zip | tail -n 1
unzip -t gtfs-ter.zip | tail -n 1
```

Les deux dernières commandes doivent afficher `No errors detected`. Le nom des fichiers GTFS doit contenir « gtfs » pour qu'OTP les reconnaisse.

### 4.2  Construire le graphe

Depuis la racine du projet :

```bash
docker compose run --rm otp-build
```

Attends la fin du conteneur (quelques minutes). À la fin, le fichier `otp-service/otp-data/graph.obj` existe :

```bash
ls -lh otp-service/otp-data/graph.obj
```

Cette étape se refait à chaque mise à jour des données ou de l'image OTP.

---

## 5. Lancer l'application

```bash
docker compose up -d db otp backend
```

- `db` : PostgreSQL/PostGIS (port `5433` sur ta machine)
- `otp` : moteur d'itinéraires (port `8081`)
- `backend` : API Spring Boot (port `8080`)

Vérifie l'état :

```bash
docker compose ps
```

Suis les logs d'un service si besoin (remplace `backend` par `otp` ou `db`) :

```bash
docker compose logs -f backend
```

### Vérifier que PostgreSQL répond

Le conteneur `db` a un healthcheck intégré, mais tu peux le vérifier toi-même :

```bash
docker compose exec db pg_isready -U "$DB_USER" -d smartcampus
```

Réponse attendue : `accepting connections`. Pour te connecter directement à la base et lister les tables créées par le backend :

```bash
docker compose exec db psql -U "$DB_USER" -d smartcampus -c "\dt"
```

Si `pg_isready` ne répond pas ou que le conteneur reste `unhealthy` dans `docker compose ps`, regarde `docker compose logs db` : c'est en général un mauvais `DB_USER`/`DB_PASSWORD` dans `.env`, ou un ancien volume `pgdata` créé avec d'autres identifiants (voir section 9).

### Remplir la base de données (imports au premier démarrage)

La base PostgreSQL est **vide** juste après `docker compose up`. Une fois le `backend` démarré (`docker compose ps` doit le montrer `healthy`/`running`), il faut appeler manuellement les endpoints d'import suivants, une seule fois, dans cet ordre :

| Ordre | Endpoint | Remplit |
|---|---|---|
| 1 | `POST /api/import/local` | Campus, bâtiments et couleurs d'institution (à partir des fichiers locaux du projet) |
| 2 | `POST /api/parking/import` | Parkings, depuis l'API open data de Bordeaux Métropole |
| 3 | `POST /api/stationV/import` | Stations VCub (vélo en libre-service), depuis le flux GBFS |
| 4 | `POST /api/stationTer/import` | Gares TER, depuis l'API Navitia/SNCF (nécessite `NAVITIA_TOKEN` dans `.env`) |
| 5 | `POST /api/stationTB/import` | Arrêts bus/tram TBM, depuis l'API Mecatran |
| 6 | `POST /api/freeVehicle/import-types` | Types de véhicules free-floating (Pony, Dott…), depuis l'API RideYeGo |

Commandes `curl` correspondantes :

```bash
curl -X POST http://localhost:8080/api/import/local
curl -X POST http://localhost:8080/api/parking/import
curl -X POST http://localhost:8080/api/stationV/import
curl -X POST http://localhost:8080/api/stationTer/import
curl -X POST http://localhost:8080/api/stationTB/import
curl -X POST http://localhost:8080/api/freeVehicle/import-types
```

Chaque appel renvoie un JSON du type `{"imported": <nombre>}` (sauf `/api/import/local` qui renvoie le campus et les bâtiments importés). Ces imports sont idempotents : les relancer ne duplique pas les données, mais réimporte/écrase l'existant, donc ils peuvent aussi servir à rafraîchir les données statiques plus tard.



---

## 6. Lancer l'application mobile (Android)

1. Ouvre le dossier du projet Android dans **Android Studio**.
2. Vérifie que `BASE_URL` (dans `BuildConfig` / `build.gradle.kts`) pointe vers le backend :
   - Émulateur Android : `http://10.0.2.2:8080/` (adresse spéciale de l'émulateur pour joindre ta machine)
   - Téléphone physique : `http://<adresse IP de ta machine>:8080/` (même réseau Wi-Fi)
3. Lance l'application avec le bouton **Run**.

---

## 7. Arrêter l'application

```bash
docker compose down
```

Pour tout arrêter **et supprimer les données de la base** (à utiliser avec précaution) :

```bash
docker compose down -v
```

