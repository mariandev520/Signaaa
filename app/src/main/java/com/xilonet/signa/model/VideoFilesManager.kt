package com.xilonet.signa.model

import android.content.Context
import android.util.Log
import java.text.Normalizer
import kotlin.collections.HashSet

// Se comunica con los archivos y carpetas dentro de "assets" para que el resto de la app pueda
// acceder a las categorías y videos de forma fácil y ordenada.
class VideoFilesManager(ctxt: Context) {
    private val _allVideos: HashSet<LSMVideo> = hashSetOf()
    private val _categories: HashSet<CategoryWithVideos> = hashSetOf()

    private var searchQueue: List<LSMVideo> = emptyList() // Inicializa la cola vacía
    private val _videosByName: MutableMap<String, MutableList<LSMVideo>> = mutableMapOf()
    private val previousVideos = mutableListOf<LSMVideo>()




    init {
        ctxt.assets.list("lsm")?.forEach { category ->
            val videos = ctxt.assets.list("lsm/$category")?.map { videoFullName ->
                val videoName = videoFullName.substring(0, videoFullName.length - 4)
                val video = LSMVideo(videoName, category, "lsm/$category/$videoFullName")
                _allVideos.add(video)
                // Agregar el video al mapa de videos por nombre
                val normalizedVideoName = videoName.lowercase().unaccent()
                if (_videosByName.containsKey(normalizedVideoName)) {
                    _videosByName[normalizedVideoName]!!.add(video)
                } else {
                    _videosByName[normalizedVideoName] = mutableListOf(video)
                }
            }
            _categories.add(CategoryWithVideos(category, videos))
        }
    }



    // Devuelve todos los videos que "coinciden" con los caracteres de búsqueda.
    // Es para el buscador.
    fun search(query: String): List<LSMVideo> {
        val queries = query.split(" ") // Divide el input en varios strings

        // Si el segundo string es distinto al primero, limpia la cola de videos
        if (queries.size > 1 && queries[1] != queries[0]) {
            // Limpiar la cola de videos
            Log.d("VideoFilesManager", "Limpiando la cola de videos")
            searchQueue = emptyList()
        }

        // Filtra videos que coinciden con los strings nuevos
        val newResults = _allVideos.filter { video ->
            queries.all { subQuery ->
                video.name.length >= subQuery.length &&
                        video.name.lowercase().unaccent().contains(subQuery.lowercase().unaccent())
            }
        }.sortedBy {
            it.name.unaccent()
        }

        // Agrega los nuevos resultados a la cola sin reemplazar los resultados anteriores
        if (searchQueue.isEmpty()) {
            searchQueue = newResults
        } else {
            searchQueue = searchQueue.filter { it !in newResults } + newResults
        }

        // Guarda el video anterior encontrado
        if (searchQueue.isNotEmpty()) {
            val previousVideo = searchQueue.first()
            previousVideos.add(previousVideo)
        }

        // Guarda los cuatro videos anteriores encontrados
        if (searchQueue.size >= 4) {
            previousVideos.add(searchQueue.last())
            if (previousVideos.size > 4) {
                previousVideos.removeAt(0)
            }
        }

        return searchQueue
    }

    private fun Boolean.moveVideoToFronte(video: VideoFilesManager.LSMVideo) {

    }


    fun moveVideoToFront(video: LSMVideo) {
        searchQueue = searchQueue.filter { it != video }.toMutableList()
        (searchQueue as MutableList<LSMVideo>).add(0, video)
    }

    fun getSimilarVideos(videoName: String): List<LSMVideo> {
        val normalizedQuery = videoName.lowercase().unaccent()
        val similarVideos = mutableListOf<LSMVideo>()

        _videosByName.keys.sorted().forEach { key ->
            if (key.contains(normalizedQuery)) {
                similarVideos.addAll(_videosByName[key] ?: emptyList())
            }
        }

        return similarVideos.sortedBy { it.name.unaccent() }
    }

    fun getVideosByName(videoName: String): List<LSMVideo> {
        return _allVideos.filter { it.name.contains(videoName, ignoreCase = true) }
    }

    // Devuelve todos los nombres de las categorías
    fun getCategoryNames(): List<String> {
        return _categories.map { it.name }.sorted()
    }

    // Devuelve todos los videos que pertenecen a una categoría específica





    data class LSMVideo(val name: String, val category: String, val path: String)
    data class CategoryWithVideos(val name: String, val videos: List<Any>?)

    // Quita el acento de un string o charsequence. Sirve para hacer que la búsqueda no discrimine por
// tildes.
    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return REGEX_UNACCENT.replace(temp, "")
    }

}
private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

data class LSMVideo(val name: String, val category: String, val path: String)
