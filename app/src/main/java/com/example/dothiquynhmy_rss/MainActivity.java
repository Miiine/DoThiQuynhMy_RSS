package com.example.dothiquynhmy_rss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.AsyncListUtil;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ListView lvRss;
    Customadapter customadapter;
    ArrayList<Docbao> mangdocbao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvRss = (ListView)findViewById(R.id.lvRSS);
        mangdocbao = new ArrayList<Docbao>();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadRSS().execute("https://vnexpress.net/rss/du-lich.rss");

            }
        });
        lvRss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("link", mangdocbao.get(position).link);
                startActivity(intent);
            }
        });
    }

   private class ReadRSS extends AsyncTask<String, Void, String> {
       @Override
       protected String doInBackground(String... strings) {
           return docNoiDung_Tu_Url(strings[0]);
       }

       @Override
       protected void onPostExecute(String s) {

           XMLDOMParser parser = new XMLDOMParser();
           Document document = parser.getDocument(s);
           NodeList nodeList = document.getElementsByTagName("item");
           NodeList nodeListdescription = document.getElementsByTagName("description");
           String hinhanh = "";
           String title ="";
           String link = "";
           for (int i = 0; i < nodeList.getLength(); i++) {
               String cdata = nodeListdescription.item(i + 1).getTextContent();
               Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
               Matcher matcher = p.matcher(cdata);
               if (matcher.find()) {
                   hinhanh = matcher.group(1);
               }
               Element element = (Element) nodeList.item(i);
               title = parser.getValue(element, "title");
               link = parser.getValue(element, "link");
               mangdocbao.add(new Docbao(title, link, hinhanh));
           }
           customadapter = new Customadapter(MainActivity.this, android.R.layout.simple_list_item_1,mangdocbao);
           lvRss.setAdapter(customadapter);
           super.onPostExecute(s);


       }
   }
   private static String docNoiDung_Tu_Url(String theUrl) {
       StringBuilder content = new StringBuilder();
       try {
           URL url = new URL(theUrl);
           URLConnection urlConnection = url.openConnection();
           BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

           String line = "";
           while ((line = bufferedReader.readLine()) != null) {
               content.append(line + "\n");
           }
           bufferedReader.close();

       } catch (Exception e) {
           e.printStackTrace();
       }
       return content.toString();
   }
}