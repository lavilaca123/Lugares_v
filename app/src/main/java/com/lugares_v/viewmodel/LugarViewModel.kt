package com.lugares_v.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.lugares_v.data.LugarDatabase
import com.lugares_v.model.Lugar
import com.lugares_v.repository.LugarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LugarViewModel(application: Application) : AndroidViewModel(application) {

    //Atributo para acceder al repositorio de Lugar
    private val repository: LugarRepository

    //Atributo para obtener la lista de lugares en un ArrayList especial
    val getAllData: LiveData<List<Lugar>>

    //Bloque de inicialización de los atributos
    init {
        val lugarDao = LugarDatabase.getDataBase(application).lugarDao()
        repository = LugarRepository(lugarDao)
        getAllData = repository.getAllData
    }

    fun addLugar(lugar: Lugar) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLugar(lugar)
        }
    }

    fun updateLugar(lugar: Lugar) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLugar(lugar)
        }
    }

    fun deleteLugar(lugar: Lugar) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLugar(lugar)
        }
    }
}