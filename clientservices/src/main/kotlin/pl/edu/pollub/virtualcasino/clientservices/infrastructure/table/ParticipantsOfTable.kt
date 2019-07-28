package pl.edu.pollub.virtualcasino.clientservices.infrastructure.table

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pl.edu.pollub.virtualcasino.clientservices.domain.table.Participation
import pl.edu.pollub.virtualcasino.clientservices.domain.table.TableId
import java.util.*
import java.util.UUID.*

@Document("participants_of_table")
data class ParticipantsOfTable(
        @Id val id: UUID = randomUUID(),
        val tableId: TableId,
        var participation: List<Participation> = emptyList()
)