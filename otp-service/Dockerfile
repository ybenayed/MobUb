FROM opentripplanner/opentripplanner@sha256:e4c7c39f7a5ac2f6195331df4348ddac04bb1b7e852f65576f32229f89237557

WORKDIR /var/opentripplanner

# On copie cette fois-ci le PBF et les configs (le build va créer le graph.obj tout seul)
COPY otp-data/aquitaine.osm.pbf  ./aquitaine.osm.pbf
COPY otp-data/otp-config.json    ./otp-config.json
COPY otp-data/router-config.json ./router-config.json

EXPOSE 8080

ENV JAVA_OPTIONS="-Xmx450m"

CMD ["--build", "--load", "--serve"]