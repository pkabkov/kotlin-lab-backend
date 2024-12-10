package ru.yarsu.employee

import ru.yarsu.dumpTruck.DumpTruck
import java.util.UUID

class EmployeeStorage(
    usersList: MutableList<Employee>,
) {
    private var users: MutableMap<UUID, Employee> = mutableMapOf()

    init {
        usersList.forEach { add(it) }
    }

    fun add(employee: Employee) {
        require(!has(employee.id)) {
            "В хранилище уже есть работник с идентификатором ${employee.id}."
        }
        users[employee.id] = employee
    }

    operator fun get(id: UUID): Employee? = users[id]

    fun getValues(): List<Employee>{
        return users.values.toList()
    }

    fun has(id: UUID): Boolean = users.keys.any { it == id }
}
