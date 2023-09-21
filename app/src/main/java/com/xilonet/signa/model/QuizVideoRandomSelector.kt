package com.xilonet.signa.model

import android.content.Context
import java.util.*
import kotlin.random.Random.Default.nextInt

// Básicamente es el Back-end del Quiz/Juego. Se encarga de administrar y entregar los videos y
// opciones que salen.
class QuizVideoRandomSelector(context: Context, categories: List<String>) {
    private val videos = Vector<LSMVideo>()
    private val videoFilesManager = VideoFilesManager(context)
    init {
        categories.forEach{
            videos.addAll(videoFilesManager.getVideosOfCategory(it))
        }
        videos.shuffle()
    }

    var index = videos.size - 1
    val OPTIONS_NUM = 4 // Can't be less than 1

    fun getNextVideoAndOptions(): QuizVideoAndOptions?{
        if(index < 0) return null

        val videoPath = videos[index].path
        val correctIndexIndex = nextInt(0, OPTIONS_NUM-1)
        val randomIndices: MutableSet<Int> = mutableSetOf()

        if(index > OPTIONS_NUM){
            while(randomIndices.size < OPTIONS_NUM-1) {
                randomIndices.add(nextInt(0, index - 1))
            }
        } else {
            while(randomIndices.size < OPTIONS_NUM-1){
                val numToAdd = nextInt(0, videos.size-1)
                if(numToAdd != index){
                    randomIndices.add(numToAdd)
                }
            }
        }

        val options = Vector<String>()
        for(i in 0..OPTIONS_NUM-1){
            if(i == correctIndexIndex){
                options.add(videos[index].name)
            } else {
                val indexToPut = randomIndices.first()
                randomIndices.remove(indexToPut)
                options.add(videos[indexToPut].name)
            }
        }

        index--

        return QuizVideoAndOptions(videoPath, options, correctIndexIndex)
    }
}

data class QuizVideoAndOptions(
    val videoPath: String,
    val options: Vector<String>,
    val correctIndex: Int
)