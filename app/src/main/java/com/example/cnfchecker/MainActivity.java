package com.example.cnfchecker;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText stringInput;
    private Button checkStringBtn, checkGrammarBtn;
    private TextView grammarProductions, output;

    private List<String[]> grammarProductionsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stringInput = findViewById(R.id.stringInput);
        checkStringBtn = findViewById(R.id.checkStringBtn);
        checkGrammarBtn = findViewById(R.id.checkGrammarBtn);
        grammarProductions = findViewById(R.id.grammarProductions);
        output = findViewById(R.id.output);

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

    private void checkGrammar() {
        // Prompt for grammar rules
        int numRules = 3; // Example number of rules for simplicity
        grammarProductionsList.clear(); // Clear existing rules

        // Example rules for simplicity
        grammarProductionsList.add(new String[]{"S", "AB"});
        grammarProductionsList.add(new String[]{"A", "a"});
        grammarProductionsList.add(new String[]{"B", "b"});

        // Display grammar rules
        StringBuilder grammarText = new StringBuilder("<h3>Grammar Productions:</h3><ul>");
        for (String[] rule : grammarProductionsList) {
            grammarText.append("<li>").append(rule[0]).append(" → ").append(rule[1]).append("</li>");
        }
        grammarText.append("</ul>");
        grammarProductions.setText(android.text.Html.fromHtml(grammarText.toString()));

        // Enable string input field and button
        stringInput.setEnabled(true);
        checkStringBtn.setEnabled(true);
    }

    private void checkString() {
        String inputString = stringInput.getText().toString().trim();
        if (TextUtils.isEmpty(inputString)) {
            output.setText("Input string is empty.");
            return;
        }

        boolean result = stringCheck(inputString);
        output.setText(result ? "String is accepted" : "String is not accepted");
    }

    private boolean stringCheck(String inputString) {
        int n = inputString.length();
        int m = grammarProductionsList.size();
        List<String>[][] table = new ArrayList[n + 1][n + 1];

        // Initialize table with empty lists
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                table[i][j] = new ArrayList<>();
            }
        }

        // Initialize diagonal of the table
        for (int i = 0; i < n; i++) {
            for (String[] rule : grammarProductionsList) {
                String lhs = rule[0];
                String rhs = rule[1];
                if (rhs.length() == 1 && rhs.equals(String.valueOf(inputString.charAt(i)))) {
                    table[i][i + 1].add(lhs);
                }
            }
        }

        // CYK algorithm
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

        // Check if start symbol is in the top right cell
        return table[0][n].contains(grammarProductionsList.get(0)[0]);
    }
}
