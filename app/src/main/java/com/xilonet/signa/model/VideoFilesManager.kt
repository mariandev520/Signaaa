package com.xilonet.signa.model

import android.content.Context
import java.text.Normalizer
import java.util.*
import kotlin.collections.HashSet

// Se comunica con los archivos y carpetas dentro de "assets" para que el resto de la app pueda
// acceder a las categorías y videos de forma fácil y ordenada.
class VideoFilesManager(ctxt: Context){
    private val _allVideos: HashSet<LSMVideo> = hashSetOf()
    private val _categories: HashSet<CategoryWithVideos> = hashSetOf()

    init {
        ctxt.assets.list("lsm")?.forEach {
            val videos = ctxt.assets.list("lsm/$it")?.map {
                    videoFullName -> LSMVideo(videoFullName.substring(0, videoFullName.length-4),
                                                it, "lsm/$it/$videoFullName") }
            _categories.add(CategoryWithVideos(it, videos ?: listOf()))
        }

        _categories.forEach {
            it.videos.forEach {
                _allVideos.add(it)
            }
        }
    }

    // Devuelve todos los videos que "coinciden" con los caracteres de búsqueda.
    // Es para el buscador.
    fun search(query: String) : List<LSMVideo> {
        return _allVideos.filter {it.name.length >= query.length &&
                                  it.name.lowercase().unaccent().substring(0, query.length) == (
                                    query.lowercase().unaccent())}.sortedBy{
                                        it.name.unaccent()
                                    }
    }

    // Devuelve todos los nombres de las categorías
    fun getCategoryNames() : List<String> {
        return _categories.map {it.name}.sorted()
    }

    // Devuelve todos los videos que pertenecen a una categoría específica
    fun getVideosOfCategory(category: String) : List<LSMVideo> {
        return _categories.find { it.name == category }?.videos?.sortedBy { it.name } ?: listOf()
    }
}

data class LSMVideo(val name: String, val category: String, val path: String)
data class CategoryWithVideos(val name: String, val videos: List<LSMVideo>)

// Quita el acento de un string o charsequence. Sirve para hacer que la búsqueda no discrimine por
// tildes.
fun CharSequence.unaccent(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return REGEX_UNACCENT.replace(temp, "")
}
private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()
