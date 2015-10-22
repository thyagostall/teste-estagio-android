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
    private ArrayAdapter<Concessionaria> mAdapter;
    private TextView mSelectedStore;

    private ArrayList<Concessionaria> initialArray = new ArrayList<Concessionaria>();

    public static class Concessionaria {
        private int id;
        private String title;

        private static Concessionaria NULL_ITEM = new Concessionaria(-1, "");

        public Concessionaria(int _id, String _title) {
            this.id = _id;
            this.title = _title;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            if (this.id == -1)
                return "Selecione um item";
            else
                return String.format("%d (%s)", this.id, this.title);
        }

        public static Concessionaria fromJson(JSONObject src) {
            return new Concessionaria(src.optInt("id"), src.optString("title"));
        }

        public static Concessionaria getNullObject() {
            return NULL_ITEM;
        }

        public static boolean isNullItem(Object obj) {
            return (obj == NULL_ITEM);
        }
    }

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
                        PROBLEMA 2: a intenção é popular a spinner com os dados da resposta da API.

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

        PROBLEMA 3: realize os devidos tratamentos para objetos JSON

        PROBLEMA 4: Adicione cada um dos elementos processados no spinner

     */

    protected void populateSpinner() {

        int total = mJsonArray.length();

        mAdapter.add(Concessionaria.getNullObject());
        for (int i = 0; i < total; i++) {

            // Realize os corretos tratamentos para manipulação de objetos JSON
            try {
                JSONObject rawItem = (JSONObject) mJsonArray.get(i);
                Concessionaria item = Concessionaria.fromJson(rawItem);

                mAdapter.add(item);
            } catch (JSONException e) {

            }
        }

        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.post(new Runnable() {
            @Override
            public void run() {
                mSpinner.setAdapter(mAdapter);

                setItemSelectionListener();
            }
        });
    }

    private void setItemSelectionListener() {

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selected = mSpinner.getSelectedItem().toString();

                if (selected.equals("Selecione um item")) {
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
