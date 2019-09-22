package pl.edu.pollub.virtualcasino.roulettegame

import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import pl.edu.pollub.virtualcasino.DomainEvent
import java.time.Clock

@Component
class SpringContextBasedRouletteGameFactory(private val context: ApplicationContext): RouletteGameFactory {

    override fun create(aggregateId: RouletteGameId, events: List<DomainEvent>): RouletteGame {
        val eventPublisher = context.getBean(RouletteGameEventPublisher::class.java)
        val clock = context.getBean(Clock::class.java)
        return RouletteGame(aggregateId, events.toMutableList(), eventPublisher, clock)
    }

}