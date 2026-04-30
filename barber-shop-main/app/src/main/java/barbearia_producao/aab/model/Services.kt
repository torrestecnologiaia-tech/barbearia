package barbearia_producao.aab.model

data class Services(
    val img: Int? = null,
    val name: String? = null,
    val price: String? = null,
    var id: String? = null // Adicionado para gestão dinâmica
)
