FROM opentripplanner/opentripplanner@sha256:e4c7c39f7a5ac2f6195331df4348ddac04bb1b7e852f65576f32229f89237557

WORKDIR /var/opentripplanner

# Copie des fichiers de configuration et de données
COPY otp-data/aquitaine.osm.pbf  ./aquitaine.osm.pbf
COPY otp-data/otp-config.json    ./otp-config.json
COPY otp-data/router-config.json ./router-config.json

# (Optionnel si tu veux inclure tes GTFS dans l'image pour le build)
# COPY otp-data/gtfs-tbm.zip       ./gtfs-tbm.zip
# COPY otp-data/gtfs-ter.zip       ./gtfs-ter.zip

# Étape 1 : Construction du graphe au moment du build Docker
RUN java -jar otp-shaded.jar --build .

EXPOSE 8080

ENV JAVA_OPTIONS="-Xmx450m"

# Étape 2 : Lancement du serveur au démarrage du conteneur
CMD ["java", "-jar", "otp-shaded.jar", "--server", "."]