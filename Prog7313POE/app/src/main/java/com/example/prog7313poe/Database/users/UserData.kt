package com.example.prog7313poe.Database.users

data class UserData(
    var email: String = " ",
    var firstName: String = " ",
    var lastName: String = " ",
    var password: String = " "
){
    // Firebase needs an empty constructor (even though it’s implicit now)
    constructor() : this("", "", "", "")
}
