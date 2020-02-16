package com.example.karamchand.criptogramador.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karamchand.criptogramador.FileUtils;
import com.example.karamchand.criptogramador.PreviewActivity;
import com.example.karamchand.criptogramador.R;
import com.example.karamchand.criptogramador.SearchActivity;

import java.util.ArrayList;

/**
 * Para hacer esto necesitamos un nuevo file type en el que haya simplemente word \t definition
 * Para generar previews ahí si necesitamos el finished
 * Podríamos gurdarnos sólo un puntero al finished (y si alguien lo toca yafu)
 * Entonces:
 * Al entrar acá desde main le pasamos generamos uno de definiciones que no incluye un pointer
 * Al tocar preview generamos un archivo finished y lo appendeamos al pointer
 * el archivo lo generaría la otra activity, la idea es que al pasarle un definition file lo appendee
 * Para eso necesito la frase. La primera línea de un definition file entonces debería ser la frase si no hay file.
 * Al entrar acá desde root leemos el archivo y guardamos el puntero
 * Si el puntero no existe lo generamos al tocar preview
 * En la preview le pasamos el archivo apuntado y el de definiciones y con eso estamos
 * Y con tudo eso ahora podemos mandar el archivo directo al server.
 */

public class DefinitionsActivity extends AppCompatActivity implements FileUtils.LoadListener {

    private static final String PATH = "/definitions";
    private Intent mIntent;
    private ArrayList<String> mWords;
    private ArrayList<String> mDefinitions;
    private LinearLayout mLayout;
    private String finishedpath;
    private String phrase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definitions);
        mLayout = findViewById(R.id.definitions_layout);

        mIntent = getIntent();
        if (mIntent == null || !mIntent.hasExtra("words")) {
            load();
        } else {
            mWords = new ArrayList<>();
            mDefinitions = new ArrayList<>();
            phrase = getIntent().getStringExtra("phrase");
            for (String word : getIntent().getStringArrayListExtra("words")) {
                mWords.add(word);
                mDefinitions.add("");
            }
            restoreFromState();
        }
        setupToolbar();
    }

    private void setupToolbar() {
        findViewById(R.id.go_to_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DefinitionsActivity.this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });
        findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DefinitionsActivity.this, PreviewActivity.class);
                intent.putExtra("words", mWords);
                intent.putExtra("phrase", phrase);
                intent.putExtra("definitions_path", save());
                if (finishedpath != null)
                    intent.putExtra("finished_path", finishedpath);
                //Si no hay archivo finished, dejar que lo genere y recibir de alguna forma el puntero
                startActivityForResult(intent, 10000);
            }
        });
    }

    private void load() {
        FileUtils.load(this, this, PATH);
    }

    private String save() {
        ArrayList<String> content = new ArrayList<>();
        content.add(firstLine());
        for (int i = 0; i < mWords.size(); i++) {
            content.add(mWords.get(i) + "\t" + mDefinitions.get(i));
        }
        String filename = FileUtils.phrase2Filename(firstLine());
        FileUtils.save(this, PATH, filename, content);
        Toast.makeText(this, "File written to " + filename, Toast.LENGTH_SHORT).show();
        return filename;
    }

    @Override
    public void onLoad(ArrayList<String> text, String filename) {
        mWords = new ArrayList<>();
        mDefinitions = new ArrayList<>();
        loadFirstLine(text.remove(0));
        for (String s : text) {
            String[] split = s.split("\t");
            mWords.add(split[0]);
            mDefinitions.add(split.length > 1 ? split[1] : "");
        }
        restoreFromState();
    }

    private void restoreFromState() {
        mLayout.removeAllViews();
        for (int i = 0; i < mWords.size(); i++) {
            final int int2 = i;
            LinearLayout line = new LinearLayout(this);
            TextView word = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            word.setLayoutParams(layoutParams);
            word.setText(mWords.get(i));
            word.setGravity(Gravity.CENTER_VERTICAL);

            layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
            EditText definition = new EditText(this);
            definition.setLayoutParams(layoutParams);
            definition.setText(mDefinitions.get(i));
            definition.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    mDefinitions.set(int2, editable.toString());
                }
            });

            line.addView(word);
            line.addView(definition);
            mLayout.addView(line);
        }
    }

    private String firstLine() {
        return finishedpath == null ? phrase : finishedpath;
    }

    private void loadFirstLine(String candidate) {
        if (candidate.length() <= 8) {
            finishedpath = candidate;
        } else {
            finishedpath = null;
            phrase = candidate;
        }
    }
}
