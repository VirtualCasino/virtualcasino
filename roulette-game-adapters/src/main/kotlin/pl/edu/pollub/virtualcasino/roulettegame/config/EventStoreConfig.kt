package pl.edu.pollub.virtualcasino.roulettegame.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import pl.edu.pollub.virtualcasino.eventstore.EventSerializer
import pl.edu.pollub.virtualcasino.eventstore.EventStore

@Configuration
class RouletteGameBoundedContextEventStoreConfig {

    @Bean
    fun rouletteGameBoundedContextEventStore(rouletteGameBoundedContextMongoTemplate: MongoTemplate): EventStore {
        return EventStore(rouletteGameBoundedContextMongoTemplate)
    }

    @Bean
    fun rouletteGameBoundedContextEventSerializer(): EventSerializer = EventSerializer()

}