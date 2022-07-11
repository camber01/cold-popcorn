package ph.gcash.cadet.bernabe.cambarihan.coldpopcorn

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.api.MoviesRepository
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.databinding.ActivityMovieDetailsBinding
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.Trailer

const val MOVIE_ID = "extra_movie_id"
const val MOVIE_TITLE = "extra_movie_title"

class MovieDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var backdrop: ImageView
    private lateinit var poster: ImageView
    private lateinit var title: TextView
    private lateinit var rating: RatingBar
    private lateinit var releaseDate: TextView
    private lateinit var overview: TextView
    private lateinit var homepage: String
    private lateinit var watchTrailer: Button
    private lateinit var shareTrailer: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backdrop = binding.movieBackdrop
        poster = binding.moviePoster
        title = binding.movieTitle
        rating = binding.movieRating
        releaseDate = binding.movieReleaseDate
        overview = binding.movieOverview
        watchTrailer = binding.watchTrailer

        val extras = intent.extras

        if (extras != null) {
            val movieId = extras.getLong(MOVIE_ID, 0).toString()
            getMovieDetails(movieId)
            getMovieTrailer(movieId)

            watchTrailer.setOnClickListener {
                val intent = Intent(this, MovieTrailerActivity::class.java)
                intent.putExtra(MOVIE_ID, movieId)
                intent.putExtra(MOVIE_TITLE, title.text)
                startActivity(intent)
            }
        } else {
            finish()
        }

    }

    private fun getMovieDetails(id: String) {
        MoviesRepository.getMovieDetails(
            id,
            ::onMovieDetailsFetched,
            ::onError
        )
    }

    private fun onMovieDetailsFetched(
        backdropPath: String, posterPath:String,
        titleExtra: String, ratingExtra: Float,
        releaseDateExtra: String, overViewExtra: String,
        homepageExtra: String)
        {
            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w1280${backdropPath}")
                .transform(CenterCrop())
                .into(backdrop)

            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w342${posterPath}")
                .transform(CenterCrop())
                .into(poster)

            title.text = titleExtra
            rating.rating = ratingExtra.div(2)
            releaseDate.text = releaseDateExtra
            overview.text = overViewExtra
            homepage = homepageExtra
    }

    private fun getMovieTrailer(id: String) {
        MoviesRepository.getMovieTrailer(
            id,
            ::onMovieTrailerFetched,
            ::onError
        )
    }

    private fun onMovieTrailerFetched(trailer: List<Trailer>)
    {
//        Log.d("Trailer", trailer.get(0).key)
        shareTrailer = "https://www.youtube.com/embed/${trailer[0].key}"
    }

    private fun onError() {
        Toast.makeText(this, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        inflater.inflate(R.menu.share_button, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMovie = menu?.findItem(R.id.search_button)
        val searchView = searchMovie?.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("",false)
                searchView.queryHint = "Search Movies"
                searchView.setIconifiedByDefault(false)

                searchMovie(query.toString())

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share_button -> {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "*/*"

                val shareBody = "Title: ${title.text}" +
                        "\nRelease Date: ${releaseDate.text}\n" +
                        "Rating: ${rating.rating}\n\n" +
                        "Overview: ${overview.text}\n\n\n" +
                        "Official Site: $homepage \n\n\n" +
                        "Watch Trailer here: $shareTrailer"
                val shareSubject = "${title.text}"
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)

                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                startActivity(Intent.createChooser(sharingIntent, "Share using"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchMovie(searchQuery: String) {
        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra("query", searchQuery)
        startActivity(intent)
    }
}