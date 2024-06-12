package com.fstt.calculatrice_scientifique;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {


    private String currentInput = "";
    private double lastResult = 0;
    private boolean lastResultDisplayed = false;

    private EditText display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        display.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                display.setSelection(display.getText().length());
                return true;
            }
        });

        setNumberButtonClickListener(R.id.btn0, "0");
        setNumberButtonClickListener(R.id.btn1, "1");
        setNumberButtonClickListener(R.id.btn2, "2");
        setNumberButtonClickListener(R.id.btn3, "3");
        setNumberButtonClickListener(R.id.btn4, "4");
        setNumberButtonClickListener(R.id.btn5, "5");
        setNumberButtonClickListener(R.id.btn6, "6");
        setNumberButtonClickListener(R.id.btn7, "7");
        setNumberButtonClickListener(R.id.btn8, "8");
        setNumberButtonClickListener(R.id.btn9, "9");
        setNumberButtonClickListener(R.id.btnDot, ".");

        setOperatorButtonClickListener(R.id.btnAdd, "+");
        setOperatorButtonClickListener(R.id.btnSub, "-");
        setOperatorButtonClickListener(R.id.btnMul, "*");
        setOperatorButtonClickListener(R.id.btnDiv, "/");

        setFunctionButtonClickListener(R.id.btnSqrt, "sqrt(");
        setFunctionButtonClickListener(R.id.btnLog, "log10(");
        setFunctionButtonClickListener(R.id.btnSin, "sin(");
        setFunctionButtonClickListener(R.id.btnCos, "cos(");
        setFunctionButtonClickListener(R.id.btnTan, "tan(");

        setParenthesisButtonClickListener(R.id.btnOpenParen, "(");
        setParenthesisButtonClickListener(R.id.btnCloseParen, ")");

        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateResult();
            }
        });

        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentInput = "";
                display.setText("");
                lastResultDisplayed = false;
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(currentInput)) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                    display.setText(currentInput);
                }
            }
        });

        findViewById(R.id.inverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(currentInput)) {
                    currentInput = "1/(" + currentInput + ")";
                    calculateResult();
                } else {
                    Toast.makeText(MainActivity.this, "Enter a number first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.fact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(currentInput)) {
                    int number = Integer.parseInt(currentInput);
                    long factorial = calculateFactorial(number);
                    display.setText(String.valueOf(factorial));
                    currentInput = String.valueOf(factorial);
                    lastResultDisplayed = true;
                } else {
                    Toast.makeText(MainActivity.this, "Enter a number first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.power).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(currentInput)) {
                    currentInput += "^";
                    display.setText(currentInput);
                } else {
                    Toast.makeText(MainActivity.this, "Enter a number first", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setNumberButtonClickListener(int buttonId, final String value) {
        findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastResultDisplayed && !currentInput.matches(".*[+\\-*/].*")) {
                    currentInput = value;
                    lastResultDisplayed = false;
                } else {
                    currentInput += value;
                }
                display.setText(currentInput);
            }
        });
    }


    private void setOperatorButtonClickListener(int buttonId, final String value) {
        findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(currentInput) || isOperator(currentInput.charAt(currentInput.length() - 1))) {
                    Toast.makeText(MainActivity.this, "Enter a number first", Toast.LENGTH_SHORT).show();
                } else {
                    currentInput += value;
                    display.setText(currentInput);
                }
            }
        });
    }

    private void setFunctionButtonClickListener(int buttonId, final String value) {
        findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastResultDisplayed && !currentInput.matches(".*[+\\-*/].*")) {
                    currentInput = String.valueOf(lastResult);
                    lastResultDisplayed = false;
                }
                currentInput += value;
                display.setText(currentInput);
            }
        });
    }


    private void setParenthesisButtonClickListener(int buttonId, final String value) {
        findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentInput += value;
                display.setText(currentInput);
            }
        });
    }

    private void calculateResult() {
        if (isParenthesisMatching(currentInput)) {
            try {
                Expression expression = new ExpressionBuilder(currentInput).build();
                double result = expression.evaluate();
                display.setText(String.valueOf(result));
                currentInput = String.valueOf(result);
                lastResultDisplayed = true;
            } catch (IllegalArgumentException | ArithmeticException e) {
                Toast.makeText(this, "Error in calculation", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Parentheses do not match", Toast.LENGTH_SHORT).show();
        }
    }
//*****************************  Gestion de parenthèse ********************************
    private boolean isParenthesisMatching(String input) {
        int balance = 0;
        for (char c : input.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false;
        }
        return balance == 0;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }


    private long calculateFactorial(int n) {
        if (n <= 1) {
            return 1;
        } else {
            return n * calculateFactorial(n - 1);
        }
    }

    }
