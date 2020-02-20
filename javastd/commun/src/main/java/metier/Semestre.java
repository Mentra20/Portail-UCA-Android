package metier;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Semestre implements ToJSON{
    private int number;
    private ArrayList<UE> listUE;

    public Semestre(int number, ArrayList<UE> listUE){
        this.number = number;
        this.listUE = listUE;
    }

    public int getNumber() {
        return number;
    }

    public ArrayList<UE> getListUE() {
        return listUE;
    }

    public void setListUE(ArrayList<UE> listUE) {
        this.listUE = listUE;
    }

    /*JSON*/
    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", getNumber());
            json.put("code", getListUE());
        } catch (JSONException e) {
            e.printStackTrace();}
        return json;
    }
}
