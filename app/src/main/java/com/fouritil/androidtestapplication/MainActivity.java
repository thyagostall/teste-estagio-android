package com.fouritil.androidtestapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Spinner mSpinner;
    private JSONArray mJsonArray;
    private ArrayAdapter<String> mAdapter;
    private TextView mSelectedStore;

    private ArrayList<String> initialArray = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Partindo do principio que o retorno em formato JSON venha de uma API
            Devemos buscar tais dados em uma nova Thread ou AsyncTask, utilizaremos aqui uma nova Thread
         */
        new Thread(new Runnable() {
            public void run() {


                // Instanciando a classe que fará a requisição
                Webservice ws = new Webservice();

                // Realizando a requisição e obtendo o resultado
                JSONObject jsonObject = ws.getStores();


                try {

                    mJsonArray = jsonObject.getJSONArray("content");

                    /*
                        PROBLEMA 1: a intenção é popular a spinner com os dados da resposta da API.

                        Além de possuir alguns erros (descritos abaixo) no método populateSpinner(), ao compilarmos o App
                        temos um erro que em resumo informa que uma Thread não pode manipular views...
                        Mais precisamente: android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.

                        Corrija este erro.
                     */
                    populateSpinner();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();


        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, initialArray);

    }


    /*

        PROBLEMA 2: realize os devidos tratamentos para objetos JSON

        PROBLEMA 3: Adicione cada um dos elementos processados no spinner

     */

    protected void populateSpinner() {

        int total = mJsonArray.length();

        mAdapter.add("Selecione um item");
        for (int i = 0; i < total; i++) {

            // Realize os corretos tratamentos para manipulação de objetos JSON
            JSONObject item = (JSONObject) mJsonArray.get(i);

            mAdapter.add(item.getString("title"));
        }

        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setAdapter(mAdapter);

        setItemSelectionListener();
    }

    private void setItemSelectionListener() {

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selected = mSpinner.getSelectedItem().toString();

                if (selected == "Selecione um item") {
                    selected = "Nada selecionado";
                }
                mSelectedStore = (TextView) findViewById(R.id.textView3);
                mSelectedStore.setText(selected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectedStore.setText("Nada selecionado");
            }
        });

    }

}
