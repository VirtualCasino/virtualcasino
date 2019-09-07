package pl.edu.pollub.virtualcasino.roulettegame

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pl.edu.pollub.virtualcasino.DomainEvent
import pl.edu.pollub.virtualcasino.DomainEventListener
import pl.edu.pollub.virtualcasino.DomainException
import pl.edu.pollub.virtualcasino.clientservices.table.events.JoinedTable
import pl.edu.pollub.virtualcasino.clientservices.table.events.RouletteTableReserved
import pl.edu.pollub.virtualcasino.roulettegame.exceptions.RouletteGameForTableNotExist

@Component
@Transactional(rollbackFor = [DomainException::class])
class JoinedTableListener(private val repository: RouletteGameRepository): DomainEventListener<JoinedTable> {

    override fun reactTo(event: DomainEvent) {
        reactTo(event as JoinedTable)
    }

    private fun reactTo(event: JoinedTable) {
        val rouletteGame = repository.find(RouletteGameId(event.tableId)) ?: throw RouletteGameForTableNotExist(event.tableId)
        rouletteGame.`when`(event)
    }

    override fun isListenFor(event: DomainEvent): Boolean = event is RouletteTableReserved
            && repository.contains(RouletteGameId(event.tableId))

}