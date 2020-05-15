package com.example.democlient;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Scope;
import co.elastic.apm.api.Transaction;

public class HttpClient {

    public static void callServlet(String target) {
        Tracer tracer = new ElasticApmTracer();
        Transaction transaction = ElasticApm.startTransaction();
        try (final Scope scope = transaction.activate()) {
            transaction.setName("CallServlet");

            URL url = new URL(target);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");
            System.out.println("GET [" + target + "]\n");

            //Connect:
            http.connect();

            //Wait for answer.
            BufferedReader in = new BufferedReader(
                new InputStreamReader(http.getInputStream()));
            String inLine = null;

            while ((inLine = in.readLine()) != null) {
                System.out.println(inLine + "\n");
            }

            //Disconnect:
            http.disconnect();

        } catch (Exception e) {
            transaction.captureException(e);
        } finally {
            transaction.end();
        }
    }

    public static void main(String[] args) {

        String target = "http://127.0.0.1:8080/";
        int sleepTimeMs = 1000;

        while (true) {
            callServlet(target);
            try {
                Thread.sleep(sleepTimeMs);
            } catch (InterruptedException e) {
                System.out.println("Exiting...\n");
            }
        }
    }
}



