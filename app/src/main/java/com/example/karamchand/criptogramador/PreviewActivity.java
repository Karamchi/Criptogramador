package com.example.karamchand.criptogramador;

import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class PreviewActivity extends PrintActivity implements CellView.CellListener,
        SolveWordView.DefinitionShownListener {

    private static final String FINISHED_PATH = "/finished";
    private static final String DEFS_PATH = "/definitions";
    private String mDefinitionsPath;

    @Override
    protected void generateInitialState() {
        if (getIntent().hasExtra("definitions_path"))
            mDefinitionsPath = getIntent().getStringExtra("definitions_path");

        if (mDefinitionsPath != null) {
            loadDefsFile(mDefinitionsPath);
        }

        if (getIntent().hasExtra("finished_path")) {
            String finishedPath = mIntent.getStringExtra("finished_path");
            load(FileUtils.readFromFile(FINISHED_PATH, finishedPath + ".txt"), finishedPath);
            //I don't like this but definitions are added after the list
            mDefinitions = new ArrayList<>(mDefinitions.subList(0, mLettersState.size()));

        } else if (mIntent.hasExtra("words")) {
            generate();
        }

        findViewById(R.id.save).setVisibility(View.VISIBLE);
    }

    private void generate() {
        Generator g = new Generator(mIntent.getStringExtra("phrase"),
                (ArrayList<String>) mIntent.getExtras().get("words"));
        g.generate();
        mLettersState = g.mLettersState;
        mCellLetters = g.mCellLetters;
        mCellNumbers = g.mCellNumbers;
        mPunctuation = g.mPunctuation;
        mTitle = g.mFileId;
        mSolution = g.mSolution;

        restoreFromState();
        saveIncomplete();
    }

    private void loadDefsFile(String definitionsPath) {
        mDefinitions = new ArrayList<>();
        ArrayList<String> text = FileUtils.readFromFile(DEFS_PATH, definitionsPath + ".txt");
        text.remove(0);
        for (String s : text) {
            String[] split = s.split("\t");
            mDefinitions.add(split.length > 1 ? split[1] : "");
        }
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullSave();
            }
        });
    }

    private void saveIncomplete() {
        if (mDefinitionsPath == null) return;
        ArrayList<String> content = FileUtils.readFromFile(DEFS_PATH, mDefinitionsPath  + ".txt");
        content.set(0, mTitle);
        FileUtils.save(this, DEFS_PATH, mDefinitionsPath, content);
    }

    private void fullSave() {
        ArrayList<String> content = getSaveContent();
        FileUtils.save(this, FINISHED_PATH, mTitle, content);
        Toast.makeText(this, "File written to " + mTitle, Toast.LENGTH_SHORT).show();
    }

}
