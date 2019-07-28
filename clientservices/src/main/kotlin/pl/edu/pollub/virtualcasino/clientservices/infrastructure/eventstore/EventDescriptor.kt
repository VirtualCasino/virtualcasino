package pl.edu.pollub.virtualcasino.clientservices.infrastructure.eventstore

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*
import java.util.UUID.randomUUID

@Document("event_descriptor")
data class EventDescriptor(
        @Id val id: UUID = randomUUID(),
        val body: String,
        val occurredAt: Instant = Instant.now(),
        val type: String,
        val aggregateId: UUID,
        val sent: Boolean = false
) {

    fun sent() = copy(sent = true)

}