package com.radioentertainment.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserM3UToURL {

    public static String parse(String urlM3u, String type) {

        String ligne;

        try {
            URL urlPage = new URL(urlM3u);
            HttpURLConnection connection = (HttpURLConnection) urlPage.openConnection();
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer stringBuffer = new StringBuffer();

            if (type.equals("m3u")) {
                while ((ligne = bufferedReader.readLine()) != null) {
                    if (ligne.contains("http")) {
                        connection.disconnect();
                        bufferedReader.close();
                        inputStream.close();
                        return ligne;
                    }
                    stringBuffer.append(ligne);
                }
            } else if(type.equals("m3u8")) {
                try {

                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    String line;
                    HashMap<String, Integer> segmentsMap = null;
                    String digitRegex = "\\d+";
                    Pattern p = Pattern.compile(digitRegex);

                    while((line = r.readLine())!=null){
                        if(line.equals("#EXTM3U")){ //start of m3u8
                            segmentsMap = new HashMap<String, Integer>();
                        }else if(line.contains("#EXTINF")){ //once found EXTINFO use runner to get the next line which contains the media file, parse duration of the segment
                            Matcher matcher = p.matcher(line);
                            matcher.find(); //find the first matching digit, which represents the duration of the segment, dont call .find() again that will throw digit which may be contained in the description.
                            segmentsMap.put(r.readLine(), Integer.parseInt(matcher.group(0)));
                        }
                    }
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                while ((ligne = bufferedReader.readLine()) != null) {
                    if (ligne.contains("http")) {
                        connection.disconnect();
                        bufferedReader.close();
//                        inputStream.close();
                        ligne = ligne.split("http")[1];
                        ligne = "http" + ligne;
                        Log.e("line", ligne);
                        return ligne;
                    }
                    stringBuffer.append(ligne);
                }
            }
            connection.disconnect();
            bufferedReader.close();
            inputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}