package edu.utap.exerciseapp.model

class UserModel {
    private var email: String = ""
    private var role: String = ""
    private var coach: String? = null
    private var clients: MutableList<String> = mutableListOf<String>()

    fun addClient(s : String) {
        clients.add(s)
    }

    fun setClients(l: MutableList<String>) {
        clients = l
    }

    fun getClients() : MutableList<String> {
        return clients
    }

    fun getEmail() : String {
        return email
    }
    fun setEmail(e: String) {
        email = e
    }
    fun getRole() : String {
        return role
    }
    fun setRole(r: String) {
        role = r
    }
    fun getCoach() : String {
        if (coach == null) {
            return "none"
        }
        return coach as String
    }
    fun setCoach(c: String) {
        coach = c
    }
}