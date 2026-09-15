package com.smartcampus.backend.dto.parking;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingRequestDTO {
    private String ident;
    private String nom;
    private String adresse;
    private String exploit;
    private String infor;
    private String taType;
    private String type;
    private Double latitude;
    private Double longitude;

    private Integer npTotal;
    private Integer npGlobal;
    private Integer npPmr;
    private Integer npVle;
    private Integer npVeltot;
    private Integer npVelec;
    private Integer np2rmot;
    private Integer npCovoit;

    private Double thQuar;
    private Double thDemi;
    private Double thHeur;
    private Double th2;
    private Double th3;
    private Double th4;
    private Double th10;
    private Double th24;
    private Double thNuit;

    private Double taTitul;
    private Double taNtitul;
    private Double taResmoi;
    private Double taNres7j;
    private Double taMoimot;
    private Double taMoivel;
    private String taHandi;

    private String anServ;
    private String secteur;
    private String propr;
    private String typgest;
    private Integer nbNiv;
    private Double gabariStd;
    private Double gabariMax;

    private String url;
}