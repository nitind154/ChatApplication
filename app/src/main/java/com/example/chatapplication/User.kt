package com.example.chatapplication

import android.media.Image

class User {
    var userName: String? = null
    var email: String? = null
    var uid: String? = null
    var userId: String? = null
    var profileImage: String? = null

    constructor(){}

    constructor(userName: String?, email: String?, uid: String?, userId: String?, profileImage: String?) {
        this.userName = userName
        this.email = email
        this.uid = uid
        this.userId = userId
        this.profileImage = profileImage
    }
}