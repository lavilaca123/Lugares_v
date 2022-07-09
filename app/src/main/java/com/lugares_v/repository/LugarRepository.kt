package com.lugares_v.repository


import com.lugares_v.data.LugarDao
import com.lugares_v.model.Lugar
import androidx.lifecycle.MutableLiveData


class LugarRepository(private val lugarDao: LugarDao) {
    val getAllData : MutableLiveData<List<Lugar>> = lugarDao.getLugares()

    fun addLugar(lugar: Lugar) {
        lugarDao.saveLugar(lugar)
    }

    fun updateLugar(lugar: Lugar) {
        lugarDao.saveLugar(lugar)
    }

    fun deleteLugar(lugar: Lugar){
        lugarDao.deleteLugar(lugar)
    }
}
