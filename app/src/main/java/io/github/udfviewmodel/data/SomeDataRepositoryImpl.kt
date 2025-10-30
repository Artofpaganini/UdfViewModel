package io.github.udfviewmodel.data

import io.github.udfviewmodel.domain.SomeDataRepository
import io.github.udfviewmodel.domain.model.AnimalsModel
import io.github.udfviewmodel.domain.model.CarsModel
import io.github.udfviewmodel.domain.model.CitiesModel
import io.github.udfviewmodel.domain.model.ColorsModel
import io.github.udfviewmodel.domain.model.NamesModel
import io.github.udfviewmodel.domain.model.NumbersModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import kotlin.random.Random

class SomeDataRepositoryImpl : SomeDataRepository {

    val cities = listOf("Минск", "Москва", "Берлин", "Париж", "Лондон", "Рим", "Мадрид", "Прага", "Варшава", "Вена")
    val names = listOf("Алексей", "Мария", "Иван", "Ольга", "Дмитрий", "Анна", "Сергей", "Елена", "Николай", "Татьяна")
    val colors = listOf(
        "Красный",
        "Синий",
        "Зелёный",
        "Жёлтый",
        "Фиолетовый",
        "Оранжевый",
        "Белый",
        "Чёрный",
        "Серый",
        "Бирюзовый"
    )
    val animals = listOf("Кот", "Собака", "Лев", "Тигр", "Медведь", "Волк", "Заяц", "Лиса", "Слон", "Жираф")
    val cars = listOf("BMW", "Audi", "Mercedes", "Toyota", "Honda", "Ford", "Chevrolet", "Nissan", "Kia", "Hyundai")

    override fun getCities(): Flow<CitiesModel> = flow {
        while (true) {
            emit(CitiesModel(id = UUID.randomUUID().toString(), List(50) { cities.random() }))
            delay(5000)
        }
    }

    override fun getCars(): Flow<CarsModel> = flow {
        while (true) {
            emit(CarsModel(id = UUID.randomUUID().toString(), List(50) { cars.random() }))
            delay(25000)
        }
    }

    override fun getAnimals(): Flow<AnimalsModel> = flow {
        while (true) {
            emit(AnimalsModel(id = UUID.randomUUID().toString(), List(50) { animals.random() }))
            delay(20000)
        }
    }

    override fun getNames(): Flow<NamesModel> = flow {
        while (true) {
            emit(NamesModel(id = UUID.randomUUID().toString(), List(50) { names.random() }))
            delay(10000)
        }
    }

    override fun getNumbers(): Flow<NumbersModel> = flow {
        while (true) {
            emit(
                NumbersModel(
                    id = UUID.randomUUID().toString(), List(50) { Random.nextInt(0, 1000).toString() })
            )
            delay(30000)
        }
    }

    override fun getColors(): Flow<ColorsModel> = flow {
        while (true) {
            emit(ColorsModel(id = UUID.randomUUID().toString(), List(50) { colors.random() }))
            delay(15000)
        }
    }
}