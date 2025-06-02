package com.example.prog7313poe.ui.expense.expenseview;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prog7313poe.Database.Expenses.ExpenseData;
import com.example.prog7313poe.Database.Expenses.ExpenseViewModel;
import com.example.prog7313poe.R;
import com.example.prog7313poe.ui.expense.ExpenseAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ExpenseFilterFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;

    private EditText filterEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private Spinner categorySpinner;

    private ExpenseViewModel expenseViewModel;
    private List<ExpenseData> fullExpenseList = new ArrayList<>();

    // Current filter criteria
    private String currentKeyword = "";
    private long currentStartMillis = -1;  // -1 = no lower bound
    private long currentEndMillis = Long.MAX_VALUE; // max long = no upper bound
    private String currentCategory = "All";

    // Used to format milliseconds → "yyyy-MM-dd"
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public ExpenseFilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_filter_list, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Keep edge-to-edge if you need it
        EdgeToEdge.enable(requireActivity());

        // 1) BIND VIEWS
        recyclerView = view.findViewById(R.id.rv_category_keyword);
        filterEditText = view.findViewById(R.id.et_filter);
        startDateEditText = view.findViewById(R.id.et_start_date_filter);
        endDateEditText = view.findViewById(R.id.et_end_date_filter);
        categorySpinner = view.findViewById(R.id.spn_category_filter);

        // 2) SET UP RECYCLER VIEW + ADAPTER
        adapter = new ExpenseAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // 3) INIT VIEWMODEL
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // 4) OBSERVE EXPENSE LIST
        expenseViewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> {
            // Whenever the LiveData changes, cache the full list,
            // then re‐apply whatever filters are currently set.
            fullExpenseList.clear();
            fullExpenseList.addAll(expenses);

            // Populate the category spinner based on unique categories
            populateCategorySpinner(expenses);

            // Finally, run the filter logic so the adapter is updated immediately
            applyFilters();
        });

        // 5) KEYWORD TEXT CHANGES
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString().trim();
                applyFilters();
            }
        });

        // 6) DATE PICKERS
        startDateEditText.setOnClickListener(v -> showDatePicker(true));
        endDateEditText.setOnClickListener(v -> showDatePicker(false));

        // 7) CATEGORY SPINNER CHANGED
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = (String) parent.getItemAtPosition(position);
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // If nothing selected, treat as "All"
                currentCategory = "All";
                applyFilters();
            }
        });
    }

    /**
     * Shows a DatePickerDialog.
     * If isStartDate == true, we update startDateEditText & currentStartMillis.
     * Otherwise we update endDateEditText & currentEndMillis.
     */
    private void showDatePicker(boolean isStartDate) {
        final Calendar now = Calendar.getInstance();

        // If the EditText already has a date, parse it to pre‐set the DatePicker
        if (isStartDate && currentStartMillis > 0) {
            now.setTimeInMillis(currentStartMillis);
        } else if (!isStartDate && currentEndMillis < Long.MAX_VALUE) {
            now.setTimeInMillis(currentEndMillis);
        }

        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Build a Calendar from the chosen values
                    Calendar chosen = Calendar.getInstance();
                    chosen.set(selectedYear, selectedMonth, selectedDay);

                    String formatted = sdf.format(chosen.getTime());
                    long millis = chosen.getTimeInMillis();

                    if (isStartDate) {
                        startDateEditText.setText(formatted);
                        currentStartMillis = millis;
                    } else {
                        endDateEditText.setText(formatted);
                        currentEndMillis = millis;
                    }
                    applyFilters();
                },
                year, month, day
        ).show();
    }

    /**
     * Filters fullExpenseList based on:
     *   - currentKeyword (searching name OR category)
     *   - currentCategory (exact match or "All")
     *   - currentStartMillis ≤ expenseDate ≤ currentEndMillis
     */
    private void applyFilters() {
        List<ExpenseData> filtered = new ArrayList<>();

        for (ExpenseData e : fullExpenseList) {
            boolean matchesKeyword = currentKeyword.isEmpty()
                    || e.getExpenseName().toLowerCase(Locale.ROOT)
                    .contains(currentKeyword.toLowerCase(Locale.ROOT))
                    || e.getExpenseCategory().toLowerCase(Locale.ROOT)
                    .contains(currentKeyword.toLowerCase(Locale.ROOT));

            boolean matchesCategory = currentCategory.equals("All")
                    || e.getExpenseCategory().equals(currentCategory);

            long expenseTime = e.getExpenseDate(); // already in millis
            boolean withinDateRange =
                    expenseTime >= currentStartMillis
                            && expenseTime <= currentEndMillis;

            if (matchesKeyword && matchesCategory && withinDateRange) {
                filtered.add(e);
            }
        }

        adapter.setExpenses(filtered);
    }

    /**
     * Scans all expenses once to build a unique category list.
     * Then sets up a Spinner adapter that starts with “All”, followed by each distinct category.
     */
    private void populateCategorySpinner(List<ExpenseData> expenses) {
        Set<String> unique = new HashSet<>();
        for (ExpenseData e : expenses) {
            unique.add(e.getExpenseCategory());
        }

        List<String> categories = new ArrayList<>();
        categories.add("All");            // Always show “All” as first item
        categories.addAll(unique);        // Then each distinct category

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        // If the user already had a category selected (e.g. after rotation),
        // try to restore that position:
        int pos = categories.indexOf(currentCategory);
        categorySpinner.setSelection(pos >= 0 ? pos : 0);
    }
}
