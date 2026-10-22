package ru.mirea.barnatovich.nobelclient.domain.model

// code — значение category в ответе сервера и в параметре ?category=
enum class PrizeCategory(val code: String, val title: String) {
    PHYSICS("physics", "Физика"),
    CHEMISTRY("chemistry", "Химия"),
    MEDICINE("medicine", "Медицина"),
    LITERATURE("literature", "Литература"),
    PEACE("peace", "Мир"),
    ECONOMICS("economics", "Экономика");

    companion object {
        fun fromCode(code: String): PrizeCategory? = entries.find { it.code == code }
    }
}
