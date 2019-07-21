package pl.edu.pollub.virtualcasino.clientservices.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import pl.edu.pollub.virtualcasino.clientservices.CasinoServicesBoundedContext
import pl.edu.pollub.virtualcasino.clientservices.domain.client.ClientId
import pl.edu.pollub.virtualcasino.clientservices.domain.client.ClientRepository
import pl.edu.pollub.virtualcasino.clientservices.domain.client.Tokens
import pl.edu.pollub.virtualcasino.clientservices.domain.table.TableId
import pl.edu.pollub.virtualcasino.clientservices.domain.table.TableRepository
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static pl.edu.pollub.virtualcasino.clientservices.domain.client.samples.SampleClient.sampleClient
import static pl.edu.pollub.virtualcasino.clientservices.domain.client.samples.SampleClient.sampleClientId
import static pl.edu.pollub.virtualcasino.clientservices.domain.client.samples.events.SampleTokensBought.sampleTokensBought
import static pl.edu.pollub.virtualcasino.clientservices.domain.table.commands.GameType.POKER
import static pl.edu.pollub.virtualcasino.clientservices.domain.table.samples.SampleTable.sampleTableId
import static pl.edu.pollub.virtualcasino.clientservices.domain.table.samples.comands.SampleJoinTable.sampleJoinTable
import static pl.edu.pollub.virtualcasino.clientservices.domain.table.samples.comands.SampleReserveTable.sampleReserveTable

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = [CasinoServicesBoundedContext.class])
class ClientServicesApiTest extends Specification {

    @Autowired
    TableRepository tableRepository

    @Autowired
    ClientRepository clientRepository

    @Autowired
    TestRestTemplate http

    def cleanup() {
        clientRepository.clear()
        tableRepository.clear()
    }

    TableId uriToId(URI tableUri) {
        def tableUriSegments = tableUri.toString().split("/")
        def tableIdValue = tableUriSegments[tableUriSegments.length - 1]
        return sampleTableId(value: tableIdValue)
    }

    ClientId prepareClient() {
        def clientId = sampleClientId()
        def client = sampleClient(id: clientId)
        clientRepository.add(client)
        return clientId
    }

    ClientId prepareClientWithTokens(Tokens tokens) {
        def clientId = sampleClientId()
        def clientThatReservedTable = sampleClient(id: clientId,
                changes: [sampleTokensBought(clientId: clientId, tokens: tokens)])
        clientRepository.add(clientThatReservedTable)
        return clientId
    }

    TableId reserveTable(ClientId clientThatReservedTableId) {
        def tableUri = http.postForLocation(URI.create("/tables"), sampleReserveTable(clientId: clientThatReservedTableId))
        return uriToId(tableUri)
    }

    TableId reservePokerTable(ClientId clientThatReservedTableId, Tokens initialBidingRate) {
        def reserveTable = sampleReserveTable(clientId: clientThatReservedTableId, gameType: POKER, initialBidingRate: initialBidingRate)
        def tableUri = http.postForLocation(URI.create("/tables"), reserveTable)
        return uriToId(tableUri)
    }

    TableId joinTable(TableId tableId, ClientId clientThatJoinedToTableId) {
        def tableUri = http.postForLocation(URI.create("/tables/participation"), sampleJoinTable(tableId: tableId, clientId: clientThatJoinedToTableId))
        return uriToId(tableUri)
    }
}
