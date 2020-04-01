package com.example.plplbproject.Vue.mainMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.plplbproject.R;
import com.example.plplbproject.Vue.creationMenu.CreationMenuActivity;
import com.example.plplbproject.data.DataPredefinedCourse;
import com.example.plplbproject.data.DataSemester;
import com.example.plplbproject.data.UpdatePredefinedCourse;
import com.example.plplbproject.data.UpdateSemester;
import com.example.plplbproject.reseau.Connexion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

import static constantes.NET.COURSESNAMES;
import static constantes.NET.PREDEFINEDCOURSE;
import static constantes.NET.SEMSTERDATA;

/**
 * L'Activité du menu principal.
 */
public class MainMenuActivity extends AppCompatActivity{

    private Context context;
    private Button deconnexion;//Le bouton de deconnexion.
    private Button newCourse;//Le button pour creer un nouveau parcours

    private ArrayList<String> clientCourses;//La liste de nom des parcours du client.

    private RecyclerView clientCourseRecyclerView;
    private ClientCourseAdapter clientCourseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_princ);
        this.context = getApplicationContext();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //le client a reussi a se connecter et n'a pas la liste des semestre on lui envoie
        if(!DataSemester.SEMESTER.hasSemesterList()){
            Connexion.CONNEXION.setEventListener(SEMSTERDATA, new UpdateSemester());
            Connexion.CONNEXION.send(SEMSTERDATA, "");
        }

        //Le client a reussi a se connecter et n'a pas la liste des parcours prédéfinis. on lui envoie
        if(!DataPredefinedCourse.PREDEFINEDCOURSE.hasPredefinedCourseList()){
            Connexion.CONNEXION.setEventListener(PREDEFINEDCOURSE, new UpdatePredefinedCourse());
            Connexion.CONNEXION.send(PREDEFINEDCOURSE,"");
        }

        deconnexion = findViewById(R.id.deconnexion);
        newCourse = findViewById(R.id.nouveauParcours);


        if(clientCourses == null){
            clientCourses = new ArrayList<String>();
        }

        // #################### Mise en place de l'adapter de parcours ################################"

        clientCourseRecyclerView = findViewById(R.id.parcoursList);
        clientCourseAdapter = new ClientCourseAdapter(clientCourses,this);
        clientCourseRecyclerView.setAdapter(clientCourseAdapter);
        clientCourseRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ################## Définition des listeners #####################

        //Le listener du bouton de nouveau parcours.
        newCourse.setOnClickListener(createNewCourse());

        //Le listener du bouton de deconnexion.
        deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // ################### récupération des parcours sauvegardés #####################################
        setupCoursesList();

    }

    /**
     * setup et appel les event necessaire pour cette activity
     */
    public void setupCoursesList(){
        Connexion.CONNEXION.setEventListener(COURSESNAMES, receiveParcoursName());
        Connexion.CONNEXION.send(COURSESNAMES,"");
    }

    public void setClientCourses(ArrayList<String> clientCourses){
        this.clientCourses = clientCourses;
    }


    /**
     * Gère la reception des parcours sauvegardés envoyé par le serveur
     * Le serveur envoie les noms de parcours uniquement
     * @return traitement à affectuer
     */
    public Emitter.Listener receiveParcoursName (){
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gson gson = new GsonBuilder().create();

                final ArrayList<String> coursesNames = gson.fromJson((String) args[0], ArrayList.class);
                setClientCourses(coursesNames);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clientCourseAdapter.setParcoursNames(coursesNames);
                        clientCourseAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
    }

    /**
     * Le listener du bouton newCourse
     * Il switch d'intent pour aller vers le menu de creation de parcours.
     * @return le listener
     */
    public View.OnClickListener createNewCourse(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this , CreationMenuActivity.class);
                intent.putExtra("clientCourses",clientCourses);
                startActivity(intent);
            }
        };
    }

}
