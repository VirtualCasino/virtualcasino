package pl.edu.pollub.virtualcasino.clientservices.table.exceptions

import pl.edu.pollub.virtualcasino.DomainObjectInvalidUsed
import pl.edu.pollub.virtualcasino.clientservices.client.ClientId
import pl.edu.pollub.virtualcasino.clientservices.table.TableId

class TableNotReserved(val clientId: ClientId, val tableId: TableId) : DomainObjectInvalidUsed(
        CODE,
        mapOf(
                Pair("clientId", clientId.value.toString()),
                Pair("tableId", tableId.value.toString())
        ),
        "Client with id: ${clientId.value} can't join table with id: ${tableId.value} because it isn't reserved") {

    companion object {
        const val CODE = "clientServices.table.tableNotReserved"
    }

}