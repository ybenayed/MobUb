# Utilise une version officielle stable d'OpenTripPlanner qui existe sur le Docker Hub
FROM opentripplanner/opentripplanner:2.5.0

WORKDIR /var/opentripplanner

# Copier les fichiers de configuration et le graphe pré-généré
COPY otp-service/otp-data/otp-config.json ./otp-config.json
COPY otp-service/otp-data/router-config.json ./router-config.json
COPY otp-service/otp-data/graph.obj ./graph.obj

# Lancer le serveur OTP en mode --load
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx2G", "-jar", "/app/opentripplanner.jar", "--load", "."]