package com.traffy.attapon.traffybus.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by chanpc on 7/25/2016.
 */
public class SendUserInfo extends AsyncTask<String, String, String> {
    String dateTime,Mac,Lat,Lng;
    Context mainContext;
    ArrayList<String> accelList;
    public SendUserInfo(String DATETIME, String MacAdress, String Latitude, String Longitude, Context con, ArrayList<String> b)
    {
        dateTime = DATETIME;
        Mac = MacAdress;
        Lat = Latitude;
        Lng = Longitude;
        mainContext = con;
        accelList = b;
    }

    @Override
    protected String doInBackground(String... urls) {
        HttpURLConnection connection = null;
        DataOutputStream writer = null;
        BufferedReader reader = null;


        try {//make parameter
            String xArray="",yArray="",zArray="";
            ArrayList<String> xA = new ArrayList<>();
            ArrayList<String> yA = new ArrayList<>();
            ArrayList<String> zA = new ArrayList<>();
            for(int i = 0;i<accelList.size();i++)
            {
                String[] temp = accelList.get(i).split("\\s+");
                xA.add(temp[0]);
                yA.add(temp[1]);
                zA.add(temp[2]);
            }

            //dateTime = dateTime.replaceAll("\\s+","");
            Log.d ("TimeStap",dateTime);
            accelList.clear();
            String urlParameters = URLEncoder.encode("latitude","UTF-8") +"="+ URLEncoder.encode(Lat, "UTF-8") + "&"+ URLEncoder.encode("longitude","UTF-8") + "="+URLEncoder.encode(Lng, "UTF-8")
                    + "&"+ URLEncoder.encode("timestamp","UTF-8") + "="+URLEncoder.encode(dateTime, "UTF-8")+ "&"+ URLEncoder.encode("mac","UTF-8") + "="+URLEncoder.encode(Mac, "UTF-8")
                    + "&"+ URLEncoder.encode("accelx","UTF-8") + "="+URLEncoder.encode(xA.toString(), "UTF-8")+ "&"+URLEncoder.encode("accely","UTF-8") + "="+URLEncoder.encode(yA.toString(), "UTF-8")
                    + "&"+ URLEncoder.encode("accelz","UTF-8") + "="+URLEncoder.encode(zA.toString(), "UTF-8");
            Log.d("parameter",urlParameters);

            URL url = new URL(urls[0]);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Content-Type"," application/x-www-form-urlencoded");
            connection.setRequestProperty( "Content-Length", Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches (false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();


            writer = new DataOutputStream(connection.getOutputStream());

            writer.writeBytes(urlParameters);
            writer.flush();
            writer.close();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             Log.d("readInfo", reader.readLine());




        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("Error",e.getMessage());

        }catch(SocketTimeoutException e){
            Toast.makeText(mainContext,"Time out cannot connect to host",Toast.LENGTH_SHORT);
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d("Error",e.getMessage());
        } finally {
           if(connection!=null)
                connection.disconnect();
        }
        return null;
    }

}
