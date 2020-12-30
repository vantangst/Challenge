package com.co.challengeliv3ly.models

data class UserModel(
    var avatar_url : String? = "",
    var email : String? = "",
    var id : Int = 0,
    var name : String? = "",
    var phone : String? = "",
    var token : String? = "",
    var total_rate : Int? = 0,
    var average_rate : Boolean = false,
    var average_rate_Str : String = ""
)