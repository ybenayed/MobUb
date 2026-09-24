package com.smartcampus.backend.dto.parking;

import lombok.*;
import java.time.OffsetDateTime;
//dto pour le status d'un parking 
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingStatusDTO {
    private Long id;
    private String ident;
    private String nom;
    private String adresse;
    private String exploit;
    private String infor;
    private String taType;
    private String type;
    private Double latitude;
    private Double longitude;

    // Capacités
    private Integer npTotal;
    private Integer npGlobal;
    private Integer npPmr;
    private Integer npVle;
    private Integer npVeltot;
    private Integer npVelec;
    private Integer np2rmot;
    private Integer npCovoit;

    // Tarifs horaires (th)
    private Double thQuar;
    private Double thDemi;
    private Double thHeur;
    private Double th2;
    private Double th3;
    private Double th4;
    private Double th10;
    private Double th24;
    private Double thNuit;

    // Tarifs abonnements (ta)
    private Double taTitul;
    private Double taNtitul;
    private Double taResmoi;
    private Double taNres7j;
    private Double taMoimot;
    private Double taMoivel;
    private String taHandi;

    // Infra
    private String anServ;
    private String secteur;
    private String propr;
    private String typgest;
    private Integer nbNiv;
    private Double gabariStd;
    private Double gabariMax;

    // Données dynamiques (Temps Réel)
    private String etat;
    private Integer libre;
    private Integer totalTempsReel;
    private Boolean connecte;
    private OffsetDateTime mdate;
    private boolean dataFraiche; 

    private String url;
}