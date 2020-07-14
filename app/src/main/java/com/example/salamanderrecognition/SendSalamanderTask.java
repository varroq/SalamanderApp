package com.example.salamanderrecognition;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class SendSalamanderTask extends AsyncTask<String, Integer, String > {

    private Exception exception;
    private MainActivity mainAct;

    public SendSalamanderTask(MainActivity mainAct){
        this.mainAct = mainAct;
    }
    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            File file = new File(urls[1]);

            int size = (int) file.length();
            byte[] bytes = new byte[size];

            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

            //String auth = "Bearer " + oauthToken;
            //connection.setRequestProperty("Authorization", basicAuth);

            String boundary = UUID.randomUUID().toString();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream request = new DataOutputStream(connection.getOutputStream());

            request.writeBytes("--" + boundary + "\r\n");
            request.writeBytes("Content-Disposition: form-data; name=\"metadata\"\r\n\r\n");
            request.writeBytes("insert location and time" + "\r\n");

            request.writeBytes("--" + boundary + "\r\n");
            request.writeBytes("Content-Disposition: form-data; name=\"imagefile\"; filename=\"" + file.getName() + "\"\r\n\r\n");
            request.write(bytes);
            request.writeBytes("\r\n");

            request.writeBytes("--" + boundary + "--\r\n");
            request.flush();
            int respCode = connection.getResponseCode();

            switch (respCode) {
                case 200:
                    return "Successfully uploaded!";
                default:
                    return "Failed with code " + respCode;
            }
        } catch (Exception e) {
            this.exception = e;
            exception.printStackTrace();

            return null;
        }
    }
    protected void onPostExecute(String result) {
        mainAct.setTextAfterRequest(result);
    }
}
