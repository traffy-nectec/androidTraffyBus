package com.traffy.attapon.traffybus;

import android.os.StrictMode;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Attapon on 7/4/2559.
 */
public class GetJsonUrl {

    public String getJsonUrls(String url)
    {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                //   .detectAll()
                //   .penaltyLog()
                //   .penaltyDialog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                //   .penaltyLog()
                .build());



        String str = "";
        HttpResponse response;
        HttpClient myClient = new DefaultHttpClient();
        HttpGet myConnection = new HttpGet(url);

        try
        {
           // response = myClient.execute(myConnection);
           // str = EntityUtils.toString(response.getEntity(), "UTF-8");
            response = myClient.execute(myConnection);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                InputStream inputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader
                        (new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    str += line;
                }
            }

        }

        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        return str;
    }
}
