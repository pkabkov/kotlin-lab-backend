package ru.yarsu.web.operations.v2

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.http4k.format.Jackson
import ru.yarsu.employee.Employee
import ru.yarsu.employee.EmployeeStorage

class GetListOfUsersOperation(private val usersStorage: EmployeeStorage) {
    private val json = Jackson
    private val jsonFactory = JsonNodeFactory.instance

    fun execute(): ArrayNode {
        val response = jsonFactory.arrayNode()
        val values = usersStorage.getValues().sortedWith(compareBy<Employee> { it.name }.thenBy { it.id } )
        values.forEach {
            val tmp = jsonFactory.objectNode()
            tmp.set<ObjectNode>("Id", json.string(it.id.toString()))
            tmp.set<ObjectNode>("Name", json.string(it.name))
            tmp.set<ObjectNode>("Position", json.string(it.position))
            tmp.set<ObjectNode>("RegistrationDateTime", json.string(it.registrationDateTime.toString()))
            tmp.set<ObjectNode>("Email", json.string(it.email))
            response.add(tmp)
        }

        return response
    }
}
