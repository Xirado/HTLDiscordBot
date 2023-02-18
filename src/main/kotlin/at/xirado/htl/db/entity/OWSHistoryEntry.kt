package at.xirado.htl.db.entity

import org.ktorm.entity.Entity

interface OWSHistoryEntry : Entity<OWSHistoryEntry> {
    companion object : Entity.Factory<OWSHistoryEntry>()
    var messageId: Long
    var authorId: Long
    var content: String
}