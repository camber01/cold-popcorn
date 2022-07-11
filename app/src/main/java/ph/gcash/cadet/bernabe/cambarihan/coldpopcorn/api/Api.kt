package ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.api

import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.BuildConfig
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.GetMovieDetailsResponse
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.GetMoviesResponse
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.GetTrailerResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
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

    @GET("movie/{id}")
    fun getMovieDetails(
        @Path("id") id: String,
        @Query("api_key") apiKey: String = API_KEY
    ): Call<GetMovieDetailsResponse>

    @GET("movie/{id}/videos")
    fun getMovieTrailer(
        @Path("id") id: String,
        @Query("api_key") apiKey: String = API_KEY
    ): Call<GetTrailerResponse>
}