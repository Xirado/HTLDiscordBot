package at.xirado.htl.io

import net.dv8tion.jda.api.utils.data.DataObject

class Config(val dataObject: DataObject) {
    val token: String

    init {
        token = dataObject.getString("token", "Your Token")
    }
}