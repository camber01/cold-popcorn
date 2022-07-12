package ph.gcash.cadet.bernabe.cambarihan.coldpopcorn

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.api.MoviesRepository
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.databinding.ActivityMovieDetailsBinding
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.Trailer
import java.util.*

const val MOVIE_ID = "extra_movie_id"
const val MOVIE_TITLE = "extra_movie_title"

class MovieDetailsActivity : AppCompatActivity() {
    private val myPick: Int = 1
    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var backdrop: ImageView
    private lateinit var poster: ImageView
    private lateinit var title: TextView
    private lateinit var rating: RatingBar
    private lateinit var releaseDate: TextView
    private lateinit var overview: TextView
    private lateinit var homepage: String
    private lateinit var watchTrailer: Button
    private var shareTrailer: String = ""
    private lateinit var posterUrl: String
    private lateinit var id: String

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
            id = movieId

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
            posterUrl = posterPath
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
        if (trailer.isNotEmpty()) {
            shareTrailer = "https://www.youtube.com/embed/${trailer[0].key}"

            watchTrailer.visibility = View.VISIBLE

            watchTrailer.setOnClickListener {
                val intent = Intent(this, MovieTrailerActivity::class.java)
                intent.putExtra(MOVIE_ID, id)
                intent.putExtra(MOVIE_TITLE, title.text)
                startActivity(intent)
            }
        }else {
            watchTrailer.visibility = View.INVISIBLE
        }
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
                val shareBody = "Title: ${title.text}" +
                        "\nRelease Date: ${releaseDate.text}\n" +
                        "Rating: ${rating.rating}\n\n" +
                        "Overview: ${overview.text}\n\n\n" +
                        "Official Site: $homepage \n\n\n" +
                        "Watch Trailer here: $shareTrailer"
                val shareTitle = "${title.text}"

                if (homepage.isBlank() && shareTrailer.isBlank()){
                    doSocialShare(shareTitle, shareBody, "")
                }
                else if (homepage.isBlank()){
                    doSocialShare(shareTitle, shareBody, shareTrailer)
                }
                else{
                    doSocialShare(shareTitle, shareBody, homepage)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

        @SuppressLint("QueryPermissionsNeeded")
        private fun doSocialShare(title: String?, text: String?, url: String?) {
        val targetedShareIntents: MutableList<Intent> = ArrayList()
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"

        shareIntent.putExtra(Intent.EXTRA_TITLE, title)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, url)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        val resInfo = packageManager.queryIntentActivities(shareIntent, 0)
        if (resInfo.isNotEmpty()) {
            for (info in resInfo) {
                val targetedShare = Intent(Intent.ACTION_SEND)
                targetedShare.type = "text/plain"
                targetedShare.setPackage(info.activityInfo.packageName.lowercase(Locale.getDefault()))
                targetedShareIntents.add(targetedShare)
            }
            val intentPick = Intent()
            intentPick.action = Intent.ACTION_PICK_ACTIVITY

            intentPick.putExtra(Intent.EXTRA_TITLE, "SHARE $title")
            intentPick.putExtra(Intent.EXTRA_INTENT, shareIntent)
            intentPick.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toTypedArray())

            this.startActivityForResult(intentPick, myPick)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == myPick) {
            if (data?.component != null && !TextUtils.isEmpty(
                    data.component!!.flattenToShortString()
                )
            ) {
                val appName: String = data.component!!.flattenToShortString()
                Log.d("SELECTED", appName)

                startActivity(data)
            }
        }
    }

    private fun searchMovie(searchQuery: String) {
        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra("query", searchQuery)
        startActivity(intent)
    }
}