package metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Classe qui s'occupe de la gestion du parcours et des regle a respecter.
 */
public class Parcours {

    private String name = "default";
    private Semestre semestre; //TODO deviens une liste a l'ajout des semestre suivant
    private HashMap<String,UE> parcoursSelect;
    private HashMap<String,Integer> numberOfSelectUECategori; //TODO deviens une liste de hashmap a l'ajout des semestre suivant
    private int numberOfUEChoose = 0; //TODO deviens une liste a l'ajout des semestre suivant
    private int numberOfObligatoryUE = 0;

    /**
     * constructeur avec une liste valide d'ue (utiliser pour charger les donnée)
     * @param semestre tout les semestre du parcours
     * @param allCodeUESelected une liste valide d'ue
     */
    public Parcours(Semestre semestre,List<String> allCodeUESelected){
        this.semestre = semestre;
        parcoursSelect = new HashMap<String,UE>();
        numberOfSelectUECategori = new HashMap<String,Integer>();
        for(String codeUE : allCodeUESelected){
            UE current = semestre.findUE(codeUE);
            if(current != null){
                parcoursSelect.put(codeUE,current);
                if(semestre.isObligatoryUE(current)) numberOfObligatoryUE+=1;
                if(!semestre.isObligatoryUE(current) || (semestre.isObligatoryUE(current) && numberOfObligatoryUE>=1)){
                        numberOfSelectUECategori.put(current.getCategorie(),numberOfSelectUECategori.getOrDefault(current.getCategorie(),0) + 1);
                }
                numberOfUEChoose +=1;
            }
        }
    }

    /**
     * Constructeur pour un nouveau parcours
     * @param semestre tout les semestre du parcours
     */
    public Parcours(Semestre semestre){
        this.semestre = semestre;
        parcoursSelect = new HashMap<String,UE>();
        numberOfSelectUECategori = new HashMap<String,Integer>();
    }

    public void setSemestre(Semestre semestre){
        this.semestre = semestre;
    }


    /**
     * Regarde a l'aide de toutes les fonction intermediaire si il est possible de cocher l'ue
     * @param ue l'ue que l'on veut cocher
     * @return tru:e il est possible de cocher; false: il n'est pas possible
     */
    public boolean canBeCheckedUE(UE ue){


        Boolean semestreCheck = checkObligatorySemestreRule(ue);
        //TODO iteration 4 verification des prerequis (graphe?)
        //Boolean uePrerequisCheck = ...

        return semestreCheck; //rajouter les condition quand definit:  && uePrerequisCheck
    }

    /**
     * regarde par rapport au regle du semestre si il est possible de cocher une UE
     * @param ue l'ue que l'on veut savoir
     * @return true: il est possible; false: pas possible
     */
    public boolean checkObligatorySemestreRule(UE ue){
        //Si on a deja la max d'ue dans le smestre on ne peut plus rien choisir
        if(numberOfUEChoose >= semestre.getNumberUENeedChoose()) return false;

        if(!semestre.isObligatoryUE(ue) && numberOfObligatoryUE<1 && numberOfUEChoose >= semestre.getNumberUENeedChoose()-1) return false;
        //Si on a deja trop d'ue dans cette categorie on ne peut pas selectionner les UE
        if(!semestre.isObligatoryUE(ue) && numberOfSelectUECategori.getOrDefault(ue.getCategorie(),0) >= semestre.getMaxNumberByCategorie()) return false;

        //dans tout les autre cas il est possible de selectionner l'ue
        return true;
    }

    /**
     * ajoute une UE au parcours (verification effectuer a l'interrieur aucun changement si non possible)
     * @param ue l'ue a ajouter
     */
    public void addUEParcours(UE ue){
        //il faut que ca respecte les regle
        if(canBeCheckedUE(ue)){
            numberOfUEChoose += 1;
            //si c'etait une ue obligatoire on la retire
            if(semestre.isObligatoryUE(ue)) numberOfObligatoryUE += 1;

            //si ce n'est pas une ue obligatoir ou ce n'est pas la premiere ue obligatoir on la compte normalement
            if(!semestre.isObligatoryUE(ue) || (semestre.isObligatoryUE(ue) && numberOfObligatoryUE>1)){
                //si ce n'est pas une ue obligatoire on la compte dans ca categorie
                numberOfSelectUECategori.put(ue.getCategorie(),numberOfSelectUECategori.getOrDefault(ue.getCategorie(),0)+1);
            }
            parcoursSelect.put(ue.getUeCode(),ue);
        }
    }

    /**
     * Supprime l'ue du parcours, si celle ci y est present
     * @param ue l'ue a supprimer du parcours
     */
    public void delUEParcours(UE ue){
        //si elle fait parti de la liste des ue selectionner
        if(parcoursSelect.containsKey(ue.getUeCode())) {

            numberOfUEChoose -= 1;

            //cas normalement impossible
            if(numberOfUEChoose<0){
                //TODO faire une exception
                numberOfUEChoose = 0;
            }

            //si ce n'est pas une ue obligatoir ou ce n'est pas la premiere ue obligatoire on la compte normalement
            if(!semestre.isObligatoryUE(ue) ||(semestre.isObligatoryUE(ue) && numberOfObligatoryUE>1)){
                //si ce n'est pas une ue obligatoire on la l'enleve du nombre d'ue de la categorie.
                numberOfSelectUECategori.put(ue.getCategorie(),numberOfSelectUECategori.getOrDefault(ue.getCategorie(),0)-1);

                //cas normalement impossible le nombre d'ue de la categorie passe au negatif
                if(numberOfSelectUECategori.getOrDefault(ue.getCategorie(),0)< 0){
                    //TODO faire une exception
                    numberOfSelectUECategori.put(ue.getCategorie(),0);
                }
            }
            //si c'etait une ue obligatoire on la retire
            if(semestre.isObligatoryUE(ue)) numberOfObligatoryUE-=1;
            parcoursSelect.remove(ue.getUeCode());
        }
    }

    /**
     * Regarde si une UE est selectionner par l'utilisateur
     * @param ue l'ue que l'on regarde si elle est selectionner
     * @return true : l'ue est selectionner; false: l'ue ne l'est pas
     */
    public boolean isChecked(UE ue){
        return parcoursSelect.containsKey(ue.getUeCode());
    }

    /**
     * renvoie la liste des code d'UE du parcours
     * @return la liste des code d'UE
     */
    public ArrayList<String> createListCodeUE(){
        ArrayList<String> codeUEToList = new ArrayList<String>();
        for(UE ueCode: parcoursSelect.values()){
            codeUEToList.add(ueCode.getUeCode());
        }
        return codeUEToList;
    }

    public String toString(){
        String str = "numberOfUEChoose: "+ numberOfUEChoose+" numberOfObligatoryUE: " + numberOfObligatoryUE +" table: " +createListCodeUE().toString()+";"+this.numberOfSelectUECategori.toString();
        return str;
    }


}
