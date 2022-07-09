package com.lugares_v.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.lugares_v.data.LugarDao
import com.lugares_v.data.LugarDatabase
import com.lugares_v.model.Lugar
import com.lugares_v.repository.LugarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LugarViewModel(application: Application)
    : AndroidViewModel(application) {

    private val getAllData : MutableLiveData<List<Lugar>>

    private val repository: LugarRepository = LugarRepository(LugarDao())

    init {
        getAllData = repository.getAllData
    }

    //fun addLugar(lugar:Lugar){ viewModelScope.launch { Dispatchers.IO  }{repository.addLugar(lugar)}}
    //fun updateLugar(lugar:Lugar){ viewModelScope.launch { Dispatchers.IO  }{repository.addLugar(lugar)}}
    //fun DeleteLugar(lugar:Lugar){ viewModelScope.launch { Dispatchers.IO  }{repository.addLugar(lugar)}}
    fun addLugar(lugar: Lugar) {
        repository.addLugar(lugar)
    }
    fun updateLugar(lugar: Lugar) {
        repository.updateLugar(lugar)
    }
    fun deleteLugar(lugar: Lugar){
        repository.deleteLugar(lugar)
    }
}
