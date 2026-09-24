package com.smartcampus.backend.service.auth;


import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Fournit la liste des nationalités (gentilés) proposées côté formulaire
 * (inscription + modification de compte). Pour l'instant statique en mémoire,
 * mais isolée dans un service pour pouvoir passer facilement à une source
 * en base de données ou un fichier de configuration plus tard, sans toucher
 * au contrôleur ni au front.
 */
@Service
public class NationalityService {

    private static final List<String> NATIONALITIES = List.of(
        "Afghane", "Albanaise", "Algérienne", "Allemande", "Andorrane", "Angolaise",
        "Antiguaise-et-Barbudienne", "Argentine", "Arménienne", "Australienne", "Autrichienne",
        "Azerbaïdjanaise", "Bahamienne", "Bahreïnienne", "Bangladaise", "Barbadienne", "Belge",
        "Bélizienne", "Béninoise", "Bhoutanaise", "Biélorusse", "Birmane", "Bissau-Guinéenne",
        "Bolivienne", "Bosnienne", "Botswanaise", "Brésilienne", "Britannique", "Brunéienne",
        "Bulgare", "Burkinabée", "Burundaise", "Cambodgienne", "Camerounaise", "Canadienne",
        "Cap-Verdienne", "Centrafricaine", "Chilienne", "Chinoise", "Chypriote", "Colombienne",
        "Comorienne", "Congolaise", "Costaricienne", "Croate", "Cubaine", "Danoise",
        "Djiboutienne", "Dominicaine", "Dominiquaise", "Égyptienne", "Émirienne", "Équatorienne",
        "Équato-Guinéenne", "Érythréenne", "Espagnole", "Estonienne", "Américaine", "Éthiopienne",
        "Fidjienne", "Finlandaise", "Française", "Gabonaise", "Gambienne", "Géorgienne",
        "Ghanéenne", "Grecque", "Grenadienne", "Guatémaltèque", "Guinéenne", "Guyanienne",
        "Haïtienne", "Hondurienne", "Hongroise", "Indienne", "Indonésienne", "Irakienne",
        "Iranienne", "Irlandaise", "Islandaise", "Israélienne", "Italienne", "Ivoirienne",
        "Jamaïcaine", "Japonaise", "Jordanienne", "Kazakhe", "Kényane", "Kirghize",
        "Kiribatienne", "Kittitienne-et-Névicienne", "Koweïtienne", "Laotienne", "Lesothane",
        "Lettone", "Libanaise", "Libérienne", "Libyenne", "Liechtensteinoise", "Lituanienne",
        "Luxembourgeoise", "Macédonienne", "Malgache", "Malaisienne", "Malawite", "Maldivienne",
        "Malienne", "Maltaise", "Marocaine", "Marshallaise", "Mauricienne", "Mauritanienne",
        "Mexicaine", "Micronésienne", "Moldave", "Monégasque", "Mongole", "Monténégrine",
        "Mozambicaine", "Namibienne", "Nauruane", "Népalaise", "Néerlandaise", "Nicaraguayenne",
        "Nigérienne", "Nigériane", "Nord-Coréenne", "Norvégienne", "Néo-Zélandaise", "Omanaise",
        "Ougandaise", "Ouzbèke", "Pakistanaise", "Palaosienne", "Palestinienne", "Panaméenne",
        "Papouasienne-Néo-Guinéenne", "Paraguayenne", "Péruvienne", "Philippine", "Polonaise",
        "Portugaise", "Qatarienne", "Roumaine", "Russe", "Rwandaise", "Saint-Lucienne",
        "Saint-Marinaise", "Saint-Vincentaise", "Salomonaise", "Salvadorienne", "Samoane",
        "Santoméenne", "Saoudienne", "Sénégalaise", "Serbe", "Seychelloise", "Sierra-Léonaise",
        "Singapourienne", "Slovaque", "Slovène", "Somalienne", "Soudanaise", "Sud-Africaine",
        "Sud-Coréenne", "Sud-Soudanaise", "Sri-Lankaise", "Suédoise", "Suisse", "Surinamaise",
        "Swazie", "Syrienne", "Tadjike", "Tanzanienne", "Tchadienne", "Tchèque", "Thaïlandaise",
        "Timoraise", "Togolaise", "Tongienne", "Trinidadienne", "Tunisienne", "Turkmène",
        "Turque", "Tuvaluane", "Ukrainienne", "Uruguayenne", "Vanuataise", "Vaticane",
        "Vénézuélienne", "Vietnamienne", "Yéménite", "Zambienne", "Zimbabwéenne"
    );

    /**
     * @return la liste des nationalités, triée alphabétiquement selon les règles
     * du français (les accents sont pris en compte correctement grâce au Collator).
     */
    public List<String> getAllNationalities() {
        Collator collator = Collator.getInstance(Locale.FRENCH);
        return NATIONALITIES.stream()
                .sorted(collator)
                .collect(Collectors.toList());
    }
}
