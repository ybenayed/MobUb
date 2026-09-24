package com.smartcampus.backend.entity.parking;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "parking")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ident;

    private String nom;
    private String adresse;
    private String exploit;

    @Column(columnDefinition = "TEXT")
    private String infor;      

    @Column(name = "ta_type")
    private String taType;
    private String type;    

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    @Column(name = "np_total")
    private Integer npTotal;
    
    @Column(name = "np_global")
    private Integer npGlobal;
    
    @Column(name = "np_pmr")
    private Integer npPmr;
    
    @Column(name = "np_vle")
    private Integer npVle;      
    
    @Column(name = "np_veltot")
    private Integer npVeltot;   
    
    @Column(name = "np_velec")
    private Integer npVelec;    
    
    @Column(name = "np_2rmot")
    private Integer np2rmot;    
    
    @Column(name = "np_covoit")
    private Integer npCovoit;   

    // --- Tarifs (statique) ---
    @Column(name = "th_quar")
    private Double thQuar;   
    
    @Column(name = "th_demi")
    private Double thDemi;   
    
    @Column(name = "th_heur")
    private Double thHeur;   
    
    // Forcer le nommage explicite pour les colonnes contenant des chiffres isolés
    @Column(name = "th_2")
    private Double th2;      
    
    @Column(name = "th_3")
    private Double th3;      
    
    @Column(name = "th_4")
    private Double th4;      
    
    @Column(name = "th_10")
    private Double th10;     
    
    @Column(name = "th_24")
    private Double th24;     
    
    @Column(name = "th_nuit")
    private Double thNuit;   
    
    @Column(name = "ta_titul")
    private Double taTitul;  
    
    @Column(name = "ta_ntitul")
    private Double taNtitul; 
    
    @Column(name = "ta_resmoi")
    private Double taResmoi; 
    
    @Column(name = "ta_nres7j")
    private Double taNres7j; 
    
    @Column(name = "ta_moimot")
    private Double taMoimot; 
    
    @Column(name = "ta_moivel")
    private Double taMoivel; 
    
    @Column(name = "ta_handi")
    private String taHandi;  

    // --- Infra (statique) ---
    @Column(name = "an_serv")
    private String anServ;     
    
    private String secteur;    
    private String propr;      
    private String typgest;    
    
    @Column(name = "nb_niv")
    private Integer nbNiv;     
    
    @Column(name = "gabari_std")
    private Double gabariStd;  
    
    @Column(name = "gabari_max")
    private Double gabariMax;  

    // Augmenter la taille maximale pour les URLs au cas où
    @Column(length = 500)
    private String url;
}