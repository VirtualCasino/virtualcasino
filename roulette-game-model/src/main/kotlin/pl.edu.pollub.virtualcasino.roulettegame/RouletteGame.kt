package pl.edu.pollub.virtualcasino.roulettegame

import pl.edu.pollub.virtualcasino.DomainEvent
import pl.edu.pollub.virtualcasino.EventSourcedAggregateRoot
import pl.edu.pollub.virtualcasino.clientservices.client.Tokens
import pl.edu.pollub.virtualcasino.clientservices.table.events.JoinedTable
import pl.edu.pollub.virtualcasino.clientservices.table.events.RouletteTableReserved
import pl.edu.pollub.virtualcasino.roulettegame.commands.CancelRouletteBet
import pl.edu.pollub.virtualcasino.roulettegame.commands.LeaveRouletteGame
import pl.edu.pollub.virtualcasino.roulettegame.commands.PlaceRouletteBet
import pl.edu.pollub.virtualcasino.roulettegame.events.RouletteBetCanceled
import pl.edu.pollub.virtualcasino.roulettegame.events.RouletteBetPlaced
import pl.edu.pollub.virtualcasino.roulettegame.events.RouletteGameLeft
import pl.edu.pollub.virtualcasino.roulettegame.exceptions.BetNotExist
import pl.edu.pollub.virtualcasino.roulettegame.exceptions.BetValueMustBePositive
import pl.edu.pollub.virtualcasino.roulettegame.exceptions.PlacedBetsExceedPlayerFreeTokens
import pl.edu.pollub.virtualcasino.roulettegame.exceptions.RoulettePlayerNotExist
import java.lang.RuntimeException

class RouletteGame(private val id: RouletteGameId = RouletteGameId(),
                   changes: MutableList<DomainEvent> = mutableListOf(),
                   private val eventPublisher: RouletteGameEventPublisher
): EventSourcedAggregateRoot() {

    private val players = mutableSetOf<RoulettePlayer>()

    init {
        changes.toMutableList().fold(this) { _, event -> patternMatch(event) }
    }

    fun handle(command: PlaceRouletteBet) {
        val betValue = command.value
        val playerId = command.playerId
        val player = players.find { it.id() == playerId } ?: throw RoulettePlayerNotExist(id(), playerId)
        if(betValue <= Tokens()) throw BetValueMustBePositive(id, player.id(), betValue)
        val playerFreeTokens = player.freeTokens()
        if(betValue > playerFreeTokens) throw PlacedBetsExceedPlayerFreeTokens(id, player.id(), betValue, playerFreeTokens)
        `when`(RouletteBetPlaced(gameId = id(), playerId = player.id(), field = command.field, value = command.value))
    }

    fun handle(command: CancelRouletteBet) {
        val playerId = command.playerId
        val player = players.find { it.id() == playerId } ?: throw RoulettePlayerNotExist(id(), playerId)
        val canceledBetField = command.field
        if(!player.placedBetsFields().contains(canceledBetField)) throw BetNotExist(id, playerId, canceledBetField)
        `when`(RouletteBetCanceled(gameId = id(), playerId = playerId, field = canceledBetField))
    }

    fun handle(command: LeaveRouletteGame) {
        val playerId = command.playerId
        val player = players.find { it.id() == playerId } ?: throw RoulettePlayerNotExist(id(), playerId)
        val event = RouletteGameLeft(gameId = id(), playerId = player.id(), playerTokens = player.tokens())
        `when`(event)
        eventPublisher.publish(event)
    }

    fun id(): RouletteGameId = id

    fun players(): Set<RoulettePlayer> = players

    fun `when`(event: JoinedTable): RouletteGame {
        players.add(RoulettePlayer(event.clientId, event.clientTokens))
        changes.add(event)
        return this
    }

    fun `when`(event: RouletteTableReserved): RouletteGame {
        players.add(RoulettePlayer(event.clientId, event.clientTokens))
        changes.add(event)
        return this
    }

    private fun `when`(event: RouletteBetPlaced): RouletteGame {
        val playerThatPlacedBet = players.find { it.id() == event.playerId }!!
        playerThatPlacedBet.placeBet(event.field, event.value)
        return this
    }

    private fun `when`(event: RouletteBetCanceled): RouletteGame {
        val playerThatPlacedBet = players.find { it.id() == event.playerId }!!
        playerThatPlacedBet.cancelBet(event.field)
        return this
    }

    private fun `when`(event: RouletteGameLeft): RouletteGame {
        players.removeIf { it.id() == event.playerId }
        changes.add(event)
        return this
    }

    private fun patternMatch(event: DomainEvent): RouletteGame  = when(event) {
        is RouletteTableReserved -> `when`(event)
        is JoinedTable -> `when`(event)
        is RouletteGameLeft -> `when`(event)
        is RouletteBetPlaced -> `when`(event)
        is RouletteBetCanceled -> `when`(event)
        else -> throw RuntimeException("event: $event is not acceptable for RouletteGame")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RouletteGame

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}