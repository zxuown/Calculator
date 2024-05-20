package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button[] numberButtons;
    private Button[] actionButtons;
    private Button acButton;
    private Button plusMinusButton;
    private Button percentButton;
    private Button commaButton;
    private TextView mainText;
    private double prevText;
    private boolean step = true;
    private String prevAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        int[] numberButtonsIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                R.id.button8, R.id.button9
        };

        int[] actionButtonsIds = {
                R.id.buttonPlus, R.id.buttonMinus, R.id.buttonMultiply, R.id.buttonDivide,
                R.id.buttonEqual,
        };

        mainText = findViewById(R.id.main_text);
        acButton = findViewById(R.id.buttonAC);
        plusMinusButton = findViewById(R.id.buttonPlusMinus);
        percentButton = findViewById(R.id.buttonPercent);
        commaButton = findViewById(R.id.buttonComma);

        acButton.setOnClickListener(v ->{
            mainText.setText("0");
            step = true;
        });

        plusMinusButton.setOnClickListener(v ->{
            mainText.setText(formatResult(dotFormat(mainText.getText().toString()) * -1));
        });

        percentButton.setOnClickListener(v ->{
            mainText.setText(formatResult(dotFormat(mainText.getText().toString()) / 100));
        });

        commaButton.setOnClickListener(v ->{
            if (!mainText.getText().toString().contains(",")){
                mainText.append(",");
            }
        });

        numberButtons = new Button[numberButtonsIds.length];

        actionButtons = new Button[actionButtonsIds.length];

        for (int i = 0; i < numberButtonsIds.length; i++) {
            numberButtons[i] = findViewById(numberButtonsIds[i]);
            numberButtons[i].setOnClickListener(v -> {
                Button b = (Button)v;
                if (mainText.getText().charAt(0) == '0' && !mainText.getText().toString().contains("0,")) {
                    mainText.setText("");
                }
                if (mainText.getText().length() < 9){
                    mainText.append(b.getText());
                }
            });
        }

        for (int i = 0; i < actionButtonsIds.length; i++) {
            actionButtons[i] = findViewById(actionButtonsIds[i]);
            actionButtons[i].setOnClickListener(v ->{
                Button button = (Button)v;
                if (step) {
                    prevText = dotFormat(mainText.getText().toString());
                    mainText.setText("0");
                    prevAction = button.getText().toString();
                    step = false;
                } else {
                    setMainText(prevAction, dotFormat(mainText.getText().toString()));
                    step = true;
                }
            });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        if (savedInstanceState != null) {
            mainText.setText(savedInstanceState.getString("mainText"));
            prevText = savedInstanceState.getDouble("prevText");
            step = savedInstanceState.getBoolean("step");
            prevAction = savedInstanceState.getString("prevAction");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mainText", mainText.getText().toString());
        outState.putDouble("prevText", prevText);
        outState.putBoolean("step", step);
        outState.putString("prevAction", prevAction);
    }

    private void setMainText(String action, double currentText) {
        double result = 0;
        switch (action) {
            case "+":
                result = prevText + currentText;
                break;
            case "-":
                result = prevText - currentText;
                break;
            case "×":
                result = prevText * currentText;
                Toast.makeText(this, String.valueOf(result), Toast.LENGTH_SHORT).show();
                break;
            case "÷":
                if (currentText != 0) {
                    result = prevText / currentText;
                } else {
                    Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
        mainText.setText(formatResult(result));
    }

    private String formatResult(double value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        } else {
            return String.valueOf(dotFormat(String.valueOf(Math.round(value * 10000000) / 10000000.0)))
                    .replace(".", ",");
        }
    }

    private Double dotFormat(String value){
        return Double.parseDouble(value.replace(",", "."));
    }
}
