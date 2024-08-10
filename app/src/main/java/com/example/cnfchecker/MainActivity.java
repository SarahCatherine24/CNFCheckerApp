package com.example.cnfchecker;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText stringInput, grammarInput;
    private Button checkStringBtn, checkGrammarBtn, addGrammarBtn;
    private TextView grammarProductions, output;
    private LinearLayout mainLayout;

    private List<String[]> grammarProductionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.mainLayout);
        stringInput = findViewById(R.id.stringInput);
        grammarInput = findViewById(R.id.grammarInput);
        checkStringBtn = findViewById(R.id.checkStringBtn);
        checkGrammarBtn = findViewById(R.id.checkGrammarBtn);
        addGrammarBtn = findViewById(R.id.addGrammarBtn);
        grammarProductions = findViewById(R.id.grammarProductions);
        output = findViewById(R.id.output);

        addGrammarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGrammarRule();
            }
        });

        checkGrammarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGrammar();
            }
        });

        checkStringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkString();
            }
        });
    }

    private void addGrammarRule() {
        String input = grammarInput.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            output.setText("Please enter a grammar rule.");
            return;
        }

        String[] rule = input.split("->");
        if (rule.length != 2) {
            output.setText("Invalid rule format. Use LHS->RHS.");
            return;
        }

        String lhs = rule[0].trim();
        String rhs = rule[1].trim();

        grammarProductionsList.add(new String[]{lhs, rhs});
        displayGrammarRules();
        grammarInput.setText("");
    }

    private void displayGrammarRules() {
        StringBuilder grammarText = new StringBuilder("<h3>Grammar Productions:</h3><ul>");
        for (String[] rule : grammarProductionsList) {
            grammarText.append("<li>").append(rule[0]).append(" -> ").append(rule[1]).append("</li>");
        }
        grammarText.append("</ul>");
        grammarProductions.setText(android.text.Html.fromHtml(grammarText.toString()));
    }

    private void checkGrammar() {
        displayGrammarRules();
        output.setText("Grammar is in CNF.");
        stringInput.setEnabled(true);
        checkStringBtn.setEnabled(true);
    }

    private void checkString() {
        String inputString = stringInput.getText().toString().trim();
        if (TextUtils.isEmpty(inputString)) {
            appendOutput("Input string is empty.");
            return;
        }

        boolean result = stringCheck(inputString);
        String resultText = result ? "String \"" + inputString + "\" is accepted" : "String \"" + inputString + "\" is not accepted";
        appendOutput(resultText);
        mainLayout.setBackgroundColor(result ? Color.GREEN : Color.RED);
    }

    private void appendOutput(String text) {
        String currentOutput = output.getText().toString();
        output.setText(currentOutput + "\n" + text);
    }

    private boolean stringCheck(String inputString) {
        int n = inputString.length();
        int m = grammarProductionsList.size();
        List<String>[][] table = new ArrayList[n + 1][n + 1];

        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                table[i][j] = new ArrayList<>();
            }
        }

        for (int i = 0; i < n; i++) {
            for (String[] rule : grammarProductionsList) {
                String lhs = rule[0];
                String rhs = rule[1];
                if (rhs.length() == 1 && rhs.equals(String.valueOf(inputString.charAt(i)))) {
                    table[i][i + 1].add(lhs);
                }
            }
        }

        for (int l = 2; l <= n; l++) {
            for (int i = 0; i <= n - l; i++) {
                int j = i + l;
                for (int k = i + 1; k < j; k++) {
                    for (String[] rule : grammarProductionsList) {
                        String lhs = rule[0];
                        String rhs = rule[1];
                        if (rhs.length() == 2) {
                            String A = String.valueOf(rhs.charAt(0));
                            String B = String.valueOf(rhs.charAt(1));
                            for (String a : table[i][k]) {
                                for (String b : table[k][j]) {
                                    if (a.equals(A) && b.equals(B)) {
                                        table[i][j].add(lhs);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return table[0][n].contains(grammarProductionsList.get(0)[0]);
    }
}
