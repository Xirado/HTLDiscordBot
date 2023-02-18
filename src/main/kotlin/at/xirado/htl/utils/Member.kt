package at.xirado.htl.utils

import net.dv8tion.jda.api.entities.Member

fun Member.hasRole(id: Long): Boolean {
    return id in roles.map { it.idLong }
}