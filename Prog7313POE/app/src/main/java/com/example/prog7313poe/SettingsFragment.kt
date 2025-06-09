package com.example.prog7313poe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = view.findViewById(R.id.spinner_currency)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.currency_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val knownUsers = UserAccountManager.getKnownUsers(requireContext())
        android.util.Log.d("DEBUG_USER_LIST", "Known users in prefs: $knownUsers")

        val btnSwitchAccount = view.findViewById<Button>(R.id.btn_switch_account)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        val quickSwitchSpinner = view.findViewById<Spinner>(R.id.spinner_quick_switch)
        val quickSwitchButton = view.findViewById<Button>(R.id.btn_quick_switch)

        val userList = UserAccountManager.getKnownUsers(requireContext()).toList()
        val currentEmail = CurrentUser.email

        // Display emails with "(Current)" label
        val displayList = userList.map { email ->
            if (email == currentEmail) "$email (Current)" else email
        }

        val quickSwitchAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, displayList)
        quickSwitchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        quickSwitchSpinner.adapter = quickSwitchAdapter

        // Set the spinner to the current user
        val currentIndex = userList.indexOf(currentEmail)
        if (currentIndex >= 0) {
            quickSwitchSpinner.setSelection(currentIndex)
        }

        quickSwitchButton.setOnClickListener {
            val selectedIndex = quickSwitchSpinner.selectedItemPosition
            val selectedEmail = userList[selectedIndex]
            UserAccountManager.switchUser(requireContext(), selectedEmail)
            CurrentUser.email = selectedEmail

            val intent = Intent(requireContext(), MyHomeActivity::class.java)
            intent.putExtra("email", selectedEmail)
            startActivity(intent)
        }

        btnSwitchAccount.setOnClickListener {
            val intent = Intent(requireContext(), SwitchUserActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        btnLogout.setOnClickListener {
            val prefs = requireActivity().getSharedPreferences("MY_APP_PREFS", AppCompatActivity.MODE_PRIVATE)
            prefs.edit().remove("CURRENT_USER_EMAIL").apply()

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
