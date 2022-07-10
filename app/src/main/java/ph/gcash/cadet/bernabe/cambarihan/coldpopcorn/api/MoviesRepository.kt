package ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.api

import android.util.Log
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.GetMovieDetailsResponse
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.GetMoviesResponse
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.Movie
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MoviesRepository {
    private val api: Api
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(Api::class.java)
    }

    fun getPopularMovies(
        page: Int = 1,
        onSuccess: (movies: List<Movie>) -> Unit,
        onError: () -> Unit
    ) {
        api.getPopularMovies(page = page)
            .enqueue(object : Callback<GetMoviesResponse> {
                override fun onResponse(
                    call: Call<GetMoviesResponse>,
                    response: Response<GetMoviesResponse>
                ) {
                   if (response.isSuccessful) {
                       val responseBody = response.body()

                       if (responseBody != null) {
                           onSuccess.invoke(responseBody.movies)
                       } else {
                           onError.invoke()
                       }
                   }
                }

                override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }

    fun getTopRatedMovies(
        page: Int = 1,
        onSuccess: (movies: List<Movie>) -> Unit,
        onError: () -> Unit
    ){
        api.getTopRatedMovies(page = page)
            .enqueue(object : Callback<GetMoviesResponse> {
                override fun onResponse(
                    call: Call<GetMoviesResponse>,
                    response: Response<GetMoviesResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            onSuccess.invoke(responseBody.movies)
                        } else {
                            onError.invoke()
                        }
                    } else {
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                   onError.invoke()
                }
            })
    }

    fun getUpcomingMovies(
        page: Int = 1,
        onSuccess: (movies: List<Movie>) -> Unit,
        onError: () -> Unit
    ){
        api.getUpcomingMovies(page = page)
            .enqueue(object : Callback<GetMoviesResponse> {
                override fun onResponse(
                    call: Call<GetMoviesResponse>,
                    response: Response<GetMoviesResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            onSuccess.invoke(responseBody.movies)
                        } else {
                            onError.invoke()
                        }
                    } else {
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }

    fun getSearchContent(
        query: String,
        onSuccess: (movies: List<Movie>) -> Unit,
        onError: () -> Unit
    ){
        api.getMoviesSearchResult(query = query)
            .enqueue(object : Callback<GetMoviesResponse> {
                override fun onResponse(
                    call: Call<GetMoviesResponse>,
                    response: Response<GetMoviesResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            onSuccess.invoke(responseBody.movies)
                        } else {
                            onError.invoke()
                        }
                    } else {
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }

    fun getMovieDetails(
        id: String,
        onSuccess: (backdropPath: String, posterPath:String,
                    title: String, rating: Float,
                    releaseDate: String, overView: String,
                    homepage: String) -> Unit,
        onError: () -> Unit){
        api.getMovieDetails(id = id)
            .enqueue(object : Callback<GetMovieDetailsResponse>{
                override fun onResponse(
                    call: Call<GetMovieDetailsResponse>,
                    response: Response<GetMovieDetailsResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            onSuccess.invoke(
                                responseBody.backdropPath,
                                responseBody.posterPath,
                                responseBody.title,
                                responseBody.rating,
                                responseBody.releaseDate,
                                responseBody.overview,
                                responseBody.homepage
                            )
                        }
                        else{
                            onError.invoke()
                        }
                    }
                    else{
                        onError.invoke()
                    }
                }
                override fun onFailure(call: Call<GetMovieDetailsResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }
}