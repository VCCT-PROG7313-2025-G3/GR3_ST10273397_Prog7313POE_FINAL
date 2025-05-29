package com.example.prog7313poe.ui.login

import com.example.prog7313poe.loginData.model.LoggedInUser

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String
) {
    companion object {
        /**
         * Maps domain model to UI view.
         */
        fun fromModel(user: LoggedInUser): LoggedInUserView =
            LoggedInUserView(displayName = user.displayName)
    }
}
