package com.kp.bookproject.PostgresControllers;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseConnection {
    //main information about server
    private final String USER="postgres";
    private final String PASSWORD="postgres";
    private final String DB_URL="jdbc:postgresql://83.69.10.56:40023/postgres";
    private final String JDBC_DRIVER="org.postgresql.Driver";
    private Connection connection;
    private boolean isConnected=false;
    public DatabaseConnection(){
        connect();
        Log.d("ConnectP","connect status(constructor):"+isConnected);
    }

    private void connect(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("org.postgresql.Driver");
                    //try to connect server
                    connection= DriverManager.getConnection(DB_URL,USER,PASSWORD);
                    //check status is true if connected
                    isConnected=true;
                    Log.d("DatabaseConnector","connect status:"+isConnected);

                } catch (Exception e) {
                    isConnected=false;
                    Log.d("ConnectP","Error, connect status:"+isConnected+"Err:"+e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try{
            thread.join();
        }catch(Exception err){
            isConnected=false;
            Log.d("DatabaseConnector","connect status:"+isConnected+" Error:"+err.getMessage());
        }
    }
    protected Connection returnConnection(){
        return connection;
    }
}
