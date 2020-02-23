package serveur;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import metier.Etudiant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import metier.Semestre;

import java.net.Inet6Address;

import static constantes.NET.*;


public class Serveur {
    private final Gson gson = new GsonBuilder().create();


    private final SocketIOServer server;
    private DBManager dbManager;
    private Etudiant etudiant;

    public Serveur(String ip, int port) {

        Debug.log("Creating server..");

        // config  com.corundumstudio.socketio.Configuration;
        Debug.log("Creating server configuration..");
        Configuration config = new Configuration();
        config.setHostname(ip);
        config.setPort(port);
        Debug.log("Server configuration created.");

        // creation du serveur
        this.server = new SocketIOServer(config);
        Debug.log("Server created.");

        this.server.addEventListener(CONNEXION, String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String json, AckRequest ackRequest) throws Exception {
                Etudiant etu = gson.fromJson(json,Etudiant.class);
                clientConnect(socketIOClient, etu);
                clientConnectData(socketIOClient, etu);
            }
        });

        this.server.addEventListener(SENDCLIENTSAVE, String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String json, AckRequest ackRequest) throws Exception {
                Semestre data = gson.fromJson(json, Semestre.class);
                dbManager.save(data);
                //TODO : envoyer un liste contenant le semestre et l'etudiant si ce n'est pas un thread.
                Debug.log("Save data for "+etudiant.getNom());
            }
        });


    }

    protected void clientConnect(SocketIOClient socketIOClient, Etudiant id) {
        // map.put(id, socketIOClient);
        etudiant = id;
        Debug.log("New client connected : "+id);
        socketIOClient.sendEvent(SENDMESSAGE ,"Hello client");
    }

    protected void clientConnectData(SocketIOClient socketIOClient, Etudiant etu){
        dbManager = new DBManager(etu.toString());
        if(dbManager.getFile().exists()){
            Debug.log("Send data to : "+etu);
        }
        else{
            Debug.log("Create data for : "+etu);
        }
        socketIOClient.sendEvent(SENDDATACONNEXION,dbManager.load());
    }


    protected void startServer() {

        server.start();
        Debug.log("Server listening.");
    }


}
