package com.kp.bookproject.Controller;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseConnection {
    //main information about server
    private Connection connection;
    private boolean isConnected=false;
    public DatabaseConnection(){
        connect();
        Log.d("ConnectP","connect status(constructor):"+isConnected);
    }

    /**
     * Все описано здесь:
     * @link {https://docs.oracle.com/javase/8/docs/api/java/sql/DriverManager.html}
     *
     */
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
                    Log.d("ConnectP","Error, connect status:"+isConnected+" Err:"+e.getMessage());
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
