package ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.api

import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.BuildConfig
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.GetMoviesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = BuildConfig.ApiKey

interface Api {

    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    @GET("movie/top_rated")
    fun getTopRatedMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int
    ): Call<GetMoviesResponse>

    @GET("search/movie")
    fun getMoviesSearchResult(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("query") query: String
    ): Call<GetMoviesResponse>
}