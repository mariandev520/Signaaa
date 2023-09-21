package com.xilonet.signa.model

import android.util.Log
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await

const val LOGIN_URL = "https://xilonet.herokuapp.com/auth/login"
const val UPDATE_URL = "https://xilonet.herokuapp.com/users/"

// Singleton disponible en toda la aplicación que se encarga de administrar lo relacionado con
// la comunicación con la API del servidor (donde se guarda toda la información de los usuarios).
object HTTPUserManager {
    private val client = OkHttpClient()
    // Representación local de la información de usuario obtenida de la API:
    private var user: UserInfo? = null

    /*TODO:
        Mandar errores específicos dependiendo de si hay un error del servidor, el login es
        incorrecto, etc. De momento, siempre que el HTTP Request falla, la app asume que el usuario
        puso mal el correo/contraseña.
    */
    // Devuelve null si no hay éxito en la conexión. Intenta iniciar sesión y obtener la información
    // del usuario.
    suspend fun tryLogIn(email: String, password: String) : UserInfo? {
        val formBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()
        val request = Request.Builder()
            .url(LOGIN_URL)
            .post(formBody)
            .build()
        val result = client.newCall(request).await()
        return if(result.isSuccessful){
            Log.d("LOGIN", "Reached here")
            val body = result.body()
            val jsonString = body?.string()
            val userInfo = jsonString?.let { jsonToUserInfo(it) }
            body?.close()
            user = userInfo
            userInfo
        } else {
            null
        }
    }

    // Intenta actualizar el puntaje acumulado del usuario, primero localmente y luego en la nube.
    suspend fun postScore(score: Int){
        if(user == null) return
        user!!.accumScore = user!!.accumScore + score
        val headers = Headers.Builder().add("Authorization", user!!.token).build()
        val json = JSONObject().put("points", user!!.accumScore).toString()
        val JSONType = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSONType, json)
        val request = Request.Builder()
            .url(UPDATE_URL + user!!.id)
            .headers(headers)
            .put(body)
            .build()
        client.newCall(request).await()
    }

    // Añade las categorías al progreso del usuario (si es que no están ya)
    suspend fun postCategoryProgress(newCategories: List<String>){
        if(user == null) return
        val categoriesSet = user!!.progressedCategories.toMutableSet()
        categoriesSet.addAll(newCategories)
        user!!.progressedCategories = categoriesSet.toTypedArray()
        val headers = Headers.Builder().add("Authorization", user!!.token).build()
        val json = JSONObject().put("progress",
                                    JSONArray(user!!.progressedCategories)).toString()
        val JSONType = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSONType, json)
        val request = Request.Builder()
            .url(UPDATE_URL + user!!.id)
            .headers(headers)
            .put(body)
            .build()
        client.newCall(request).await()
    }

    // Crea un objeto UserInfo con un json obtenido de la API
    private fun jsonToUserInfo(json: String): UserInfo {
        val jsonObject = JSONObject(json)
        val token = jsonObject.getString("token")
        val userInfoObj = jsonObject.getJSONObject("user")
        val id = userInfoObj.getString("id")
        val firstName = userInfoObj.getString("first_name")
        val lastName = userInfoObj.getString("last_name")
        val email = userInfoObj.getString("email")
        val progressedCategories =
            jsonArrayToStringArray(userInfoObj.getJSONArray("progress"))
        val accumScore = userInfoObj.getInt("points")
        return UserInfo(id, token, firstName, lastName, email, progressedCategories, accumScore)
    }

    fun getUserInfo() = user
    fun nullUserInfo(){
        user = null
    }
}

fun jsonArrayToStringArray(jsonArray: JSONArray) : Array<String> {
    val result = Array<String>(jsonArray.length()) {_ -> ""}
    for(i in 0 until jsonArray.length()){
        result[i] = jsonArray.getString(i)
    }
    return result
}

data class UserInfo(
    val id: String,
    val token: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    var progressedCategories: Array<String>,
    var accumScore: Int
)
