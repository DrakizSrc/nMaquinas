package me.dkz.plugins.nmaquinas.dao;

import me.dkz.plugins.nmaquinas.Main;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLite {

    private Main plugin;
    public Connection connection = null;


    public SQLite(Main plugin){
        this.plugin = plugin;
        try {
            File database = new File(plugin.getDataFolder(), "database.db");
            if(!database.exists()){database.createNewFile();}

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+database);
            createTable();
        } catch (SQLException | ClassNotFoundException | IOException throwables) {
            plugin.getLogger().severe("Falha na conexão com o banco de dados, motivo: "+throwables.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }




    }

    public Connection getConnection(){
        return connection;
    }

    private void createTable(){
        try {
            PreparedStatement stm = connection.prepareStatement("create table if not exists `maquinas`(`data` STRING, `fuel` STRING, `items` STRING, `holo` STRING, `amount` INTEGER, `level` INTEGER)");
            stm.execute();
            plugin.getServer().getConsoleSender().sendMessage(plugin.prefix+"§aMaquinas carregadas com sucesso, atualizando..");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    public void close(){
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException throwables) {
                plugin.getLogger().severe("Falha ao fechar conexão com o banco de dados, motivo: "+throwables.getMessage());
            }
        }
    }
}
