package com.example.travelcompanionapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private double performConversion(String category, String sourceUnit, String destUnit, double inputValue){
        if(sourceUnit.equals(destUnit)){
            return inputValue;
        }

        switch (category){
            case "Currency":
                // 1. Convert source to USD
                double valueInUSD = inputValue;
                switch (sourceUnit){
                    case "AUD":
                        valueInUSD = inputValue/1.55;
                        break;
                    case "EUR":
                        valueInUSD = inputValue/0.92;
                        break;
                    case "JPY":
                        valueInUSD = inputValue/148.50;
                        break;
                    case "GBP":
                        valueInUSD = inputValue/0.78;
                        break;
                    case "USD":
                        valueInUSD = inputValue;
                        break;
                }

                // 2. Convert USD to destination AND return the result immediately
                switch (destUnit){
                    case "AUD":
                        return valueInUSD * 1.55;
                    case "EUR":
                        return valueInUSD * 0.92;
                    case "JPY":
                        return valueInUSD * 148.50;
                    case "GBP":
                        return valueInUSD * 0.78;
                    case "USD":
                        return valueInUSD;
                }
                break;

            case "Fuel & Distance":
                if (sourceUnit.equals("mpg") && destUnit.equals("km/L")) return inputValue * 0.425;
                if (sourceUnit.equals("Km/L") && destUnit.equals("mpg")) return inputValue / 0.425;

                if(sourceUnit.equals("Gallon (US)") && destUnit.equals("Liters")) return inputValue * 3.785;
                if(sourceUnit.equals("Liters") && destUnit.equals("Gallon (US)")) return inputValue / 3.785;

                if(sourceUnit.equals("Nautical Mile") && destUnit.equals("Kilometers")) return inputValue * 1.852;
                if(sourceUnit.equals("Kilometers") && destUnit.equals("Nautical Mile")) return inputValue / 1.852;
                break;

            case "Temperature":
                if (sourceUnit.equals("Celsius") && destUnit.equals("Fahrenheit")) return (inputValue * 1.8) + 32;
                if (sourceUnit.equals("Celsius") && destUnit.equals("Kelvin")) return inputValue + 273.15;

                if (sourceUnit.equals("Fahrenheit") && destUnit.equals("Celsius")) return (inputValue - 32) / 1.8;
                if (sourceUnit.equals("Fahrenheit") && destUnit.equals("Kelvin")) return ((inputValue - 32) / 1.8) + 273.15;

                if (sourceUnit.equals("Kelvin") && destUnit.equals("Celsius")) return (inputValue - 273.15);
                if (sourceUnit.equals("Kelvin") && destUnit.equals("Fahrenheit")) return ((inputValue - 273.15) * 1.8) + 32;
        }

        return 0.0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        Spinner spinnerSource = findViewById(R.id.spinnerSource);
        Spinner spinnerDestination = findViewById(R.id.spinnerDestination);
        EditText editTextInput = findViewById(R.id.editTextInput);
        Button buttonConvert = findViewById(R.id.buttonConvert);
        TextView textViewResult = findViewById(R.id.textViewResult);

        String[] categories = {"Currency", "Fuel & Distance", "Temperature"};
        String[] currencies = {"USD", "AUD", "EUR", "JPY", "GBP"};
        String[] fuelAndDistance = {"mpg", "km/L", "Gallon (US)", "Liters", "Nautical Mile", "Kilometers"};
        String[] temperatures = {"Celsius", "Fahrenheit", "Kelvin"};

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );
        spinnerCategory.setAdapter(categoryAdapter);

        spinnerCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedCategory = categories[position];
                ArrayAdapter<String> unitAdapter;

                switch (selectedCategory) {
                    case "Currency":
                        unitAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, currencies);
                        break;
                    case "Fuel & Distance":
                        unitAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, fuelAndDistance);
                        break;
                    case "Temperature":
                        unitAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, temperatures);
                        break;
                    default:
                        // Fallback empty array just in case
                        unitAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, new String[]{});
                }

                spinnerSource.setAdapter(unitAdapter);
                spinnerDestination.setAdapter(unitAdapter);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {

            }
        });

        buttonConvert.setOnClickListener(v -> {

            String inputText = editTextInput.getText().toString().trim();

            // Validation 1 - Empty Input
            if (inputText.isEmpty()) {
                editTextInput.setError("Please enter a value!"); // Shows a red error icon on the text box
                android.widget.Toast.makeText(MainActivity.this, "Input cannot be empty", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            double inputValue;
            try {
                inputValue = Double.parseDouble(inputText);
            } catch (NumberFormatException e) {
                // Validation 2 - checking whether number input given
                editTextInput.setError("Invalid number");
                return;
            }

            if (spinnerCategory.getSelectedItem() == null || spinnerSource.getSelectedItem() == null || spinnerDestination.getSelectedItem() == null) {
                android.widget.Toast.makeText(MainActivity.this, "Please select categories and units", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            String category = spinnerCategory.getSelectedItem().toString();
            String sourceUnit = spinnerSource.getSelectedItem().toString();
            String destUnit = spinnerDestination.getSelectedItem().toString();

            // Validation 3 - Identity Conversion (e.g., USD to USD)
            if (sourceUnit.equals(destUnit)) {
                android.widget.Toast.makeText(MainActivity.this, "Source and destination are the same!", android.widget.Toast.LENGTH_SHORT).show();
                textViewResult.setText(String.format("%.2f %s", inputValue, destUnit));
                return; // Stop here, no need to do math
            }

            // Validation 4 - Negative Values
            // Temperature can be negative, but Currency, Fuel, and Distance generally shouldn't be in this context
            if (inputValue < 0 && !category.equals("Temperature")) {
                editTextInput.setError("Value cannot be negative");
                android.widget.Toast.makeText(MainActivity.this, "Negative values not allowed for " + category, android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            double result = performConversion(category, sourceUnit, destUnit, inputValue);

            textViewResult.setText(String.format("%.2f", result));
        });
    }
}