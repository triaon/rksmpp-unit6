package ru.mirea.barnatovich.nobel.domain.model

// code — значение параметра nobelPrizeCategory в API, apiName — название в ответе
enum class PrizeCategory(val code: String, val apiName: String, val title: String) {
    PHYSICS("phy", "Physics", "Физика"),
    CHEMISTRY("che", "Chemistry", "Химия"),
    MEDICINE("med", "Physiology or Medicine", "Медицина"),
    LITERATURE("lit", "Literature", "Литература"),
    PEACE("pea", "Peace", "Мир"),
    ECONOMICS("eco", "Economic Sciences", "Экономика");

    companion object {
        fun fromApiName(name: String): PrizeCategory? = entries.find { it.apiName == name }
        fun fromCode(code: String): PrizeCategory? = entries.find { it.code == code }
    }
}
