package at.xirado.htl.db.table

import at.xirado.htl.db.entity.OWSHistoryEntry
import org.ktorm.database.Database
import org.ktorm.dsl.QueryRowSet
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

val Database.owsHistoryEntries get() = this.sequenceOf(OWSHistoryEntries)

object OWSHistoryEntries : Table<OWSHistoryEntry>("ows_history_entries") {
    val messageId = long("message_id").primaryKey().bindTo { it.messageId }
    val authorId = long("author_id").bindTo { it.authorId }
    val content = varchar("content").bindTo { it.content }
}