package br.com.bossini.pessoal_weatherdata_recyclerview_cardview;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView weatherRecyclerView;
    private WeatherAdapter adapter;
    private List<Weather> previsoes;

    private RequestQueue requestQueue;

    private TextInputEditText locationEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationEditText =
                findViewById(R.id.locationEditText);
        //no método onCreate
        weatherRecyclerView =
                findViewById(R.id.weatherRecyclerView);
        previsoes = new ArrayList<>();
        previsoes.add(new Weather(1000000, 35, 37, 10, "Teste", "Teste 2"));
        previsoes.add(new Weather(1000000, 35, 37, 10, "Teste", "Teste 2"));
        adapter = new WeatherAdapter(previsoes, this);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        weatherRecyclerView.setAdapter(adapter);

        //no método onCreate
        requestQueue = Volley.newRequestQueue(this);

        //isso está no onCreate...
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cidade =
                        locationEditText.getEditableText().toString();
                obtemPrevisoesV5(cidade);
            }
        });
    }

    public void lidaComJSON (String resultado){
        previsoes.clear();
        try{
            JSONObject json = new JSONObject(resultado);
            JSONArray list = json.getJSONArray("list");
            for (int i = 0; i < list.length(); i++){
                JSONObject day = list.getJSONObject(i);
                JSONObject main = day.getJSONObject("main");
                JSONObject weather = day.getJSONArray("weather").getJSONObject(0);
                previsoes.add (new Weather(day.getLong("dt"), main.getDouble("temp_min"),
                        main.getDouble("temp_max"), main.getDouble ("humidity"),
                        weather.getString("description"),weather.getString("icon")));
            }
            adapter.notifyDataSetChanged();

        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void obtemPrevisoesV4 (String cidade){
        new ObtemPrevisoes().execute(cidade);
    }
    class ObtemPrevisoes extends AsyncTask <String, Void, String>{
        @Override
        protected String doInBackground(String... enderecos) {
            try {
                String endereco = getString(
                        R.string.web_service_url,
                        enderecos[0],
                        getString(R.string.api_key)
                );
                URL url = new URL(endereco);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder resultado = new StringBuilder("");
                String aux = null;
                while ((aux = reader.readLine()) != null)
                    resultado.append(aux);
               return resultado.toString();


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String resultado) {
           lidaComJSON(resultado);
        }
    }

    public void obtemPrevisoesV3 (String cidade){
        //veja uma expressão lambda implementando a interface Runnable...
        new Thread ( ()->{
            try {
                String endereco = getString(
                        R.string.web_service_url,
                        cidade,
                        getString(R.string.api_key)
                );
                URL url = new URL(endereco);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder resultado = new StringBuilder("");
                String aux = null;
                while ((aux = reader.readLine()) != null)
                    resultado.append(aux);
                runOnUiThread(()->{
                    Toast.makeText(this, resultado.toString(), Toast.LENGTH_SHORT).show();
                    lidaComJSON(resultado.toString());
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void obtemPrevisoesV2 (String cidade){
        //veja uma expressão lambda implementando a interface Runnable...
        new Thread ( ()->{
            try {
                String endereco = getString(
                        R.string.web_service_url,
                        cidade,
                        getString(R.string.api_key)
                );
                URL url = new URL(endereco);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder resultado = new StringBuilder("");
                String aux = null;
                while ((aux = reader.readLine()) != null)
                    resultado.append(aux);
                Toast.makeText(this, resultado.toString(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void obtemPrevisoesV1 (String cidade){

        try {
            String endereco = getString(
                    R.string.web_service_url,
                    cidade,
                    getString(R.string.api_key)
            );
            URL url = new URL(endereco);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder resultado = new StringBuilder("");
            //vamos tratar o resultado aqui...


        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void obtemPrevisoesV5 (String cidade){
        String url = getString(
            R.string.web_service_url,
            cidade,
            getString(R.string.api_key)
        );
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                (response)->{
                    previsoes.clear();
                    try{
                        JSONArray list = response.getJSONArray("list");
                        for (int i = 0; i < list.length(); i++){
                            JSONObject day = list.getJSONObject(i);
                            JSONObject main = day.getJSONObject("main");
                            JSONObject weather = day.getJSONArray("weather").getJSONObject(0);
                            previsoes.add (new Weather(day.getLong("dt"), main.getDouble("temp_min"),
                                    main.getDouble("temp_max"), main.getDouble ("humidity"),
                                    weather.getString("description"),weather.getString("icon")));
                        }
                        adapter.notifyDataSetChanged();

                        //depois de notifyDataSetChanged()
                        dismissKeyboard(weatherRecyclerView);
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                },
                (error)->{
                    Toast.makeText(
                            MainActivity.this,
                            getString(R.string.connect_error) +  ": " + error.getLocalizedMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );
        requestQueue.add(req);
    }

    private void dismissKeyboard (View view){
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
