package com.example.prog7313poe.ui.expense.expenseview;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prog7313poe.R;
import com.example.prog7313poe.Database.Expenses.ExpenseViewModel;

public class ExpenseFilterFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private EditText filterEditText;
    private ExpenseViewModel expenseViewModel;

    public ExpenseFilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_filter_list, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // If you still need edge-to-edge:
        EdgeToEdge.enable(requireActivity());

        // Initialize ViewModel
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // Bind views
        recyclerView = view.findViewById(R.id.rv_category_keyword);
        filterEditText = view.findViewById(R.id.et_filter);

        // Set up RecyclerView + adapter
        adapter = new ExpenseAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Observe LiveData for changes
        expenseViewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> {
            // Update UI when data changes
            adapter.setExpenses(expenses);
        });

        // Wire up filter
        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }
}
