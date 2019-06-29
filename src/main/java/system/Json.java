package system;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Json {

    private static String JSONPAC;
    private static FileWriter arqw;
    private static PrintWriter writearq;

    public <T> boolean writeJson(T p, String dir) {
        try {
            arqw = new FileWriter(dir);
            writearq = new PrintWriter(arqw);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            JSONPAC = gson.toJson(p);
            writearq.println(JSONPAC);

            arqw.close();
            writearq.close();
            return true;

        } catch (IOException e) {
            System.out.println("Erro de gravação");
            e.getMessage();
        }
        return false;
    }

    public void exportJson(Object obj, String dir) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String temp = gson.toJson(obj);

        FileWriter arq = null;

        try {
            arq = new FileWriter(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter gravarArq = new PrintWriter(arq);
        gravarArq.print(temp);

        try {
            arq.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public <T> T readJson(String dir, Class<T> type) {

        T dao;
        FileReader arqr;
        BufferedReader lerArq;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            arqr = new FileReader(dir);

            dao = gson.fromJson(dir, type);

            arqr.close();
            return dao;

        } catch (Exception e) {
            // ...
        }
        return null;
    }

    public <T> T importJson(String dir, Class<T> type) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        T d = null;
        Reader reader;

        try {
            reader = new FileReader(dir);

            d = gson.fromJson(reader, type);
            return d;
        } catch (IOException e) {
            return null;
        }
    }
}
