package barbearia_producao.aab.model

data class Portfolio(
    val url: String? = null,
    val description: String? = null,
    val timestamp: Long? = null,
    var id: String? = null // Adicionado para permitir exclusão/edição
)
