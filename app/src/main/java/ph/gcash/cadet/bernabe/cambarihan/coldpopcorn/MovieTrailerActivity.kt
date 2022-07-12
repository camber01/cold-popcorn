package ph.gcash.cadet.bernabe.cambarihan.coldpopcorn

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.adapter.VideoAdapter
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.api.MoviesRepository
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.databinding.ActivityMovieTrailerBinding
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.Trailer
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.YouTubeVideos
import java.util.*

class MovieTrailerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieTrailerBinding

    private lateinit var movieTrailer: RecyclerView
    private lateinit var movieTrailerAdapter: VideoAdapter
    private lateinit var movieTrailerLayoutMgr: LinearLayoutManager
    private var youtubeVideos = Vector<YouTubeVideos>()
    private lateinit var trailerForTv: TextView
    private lateinit var shareTrailer: String

    private lateinit var linkContent: ShareLinkContent
    private lateinit var shareDialog: ShareDialog
    private lateinit var callbackManager: CallbackManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieTrailerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FacebookSdk.sdkInitialize(this.application.applicationContext)
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)

        trailerForTv = binding.trailerForTV

        movieTrailer = binding.movieTrailer
        movieTrailerLayoutMgr = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        movieTrailer.layoutManager = movieTrailerLayoutMgr

        val extras = intent.extras

        if (extras != null) {
            val movieId = extras.getString(MOVIE_ID, "")
            Log.d("Trailer", movieId)
            trailerForTv.text = "Watch Trailer: " + extras.getString(MOVIE_TITLE, "")
            getMovieTrailer(movieId)
        } else {
            finish()
        }

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
        shareTrailer = trailer[0].key
        for (i in trailer.indices){
            youtubeVideos.add(
                YouTubeVideos(
                    "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/${trailer[i].key}\" frameBorder=\"0\" allowFullScreen></iframe>")
            )
        }
        movieTrailerAdapter = VideoAdapter(youtubeVideos)
        movieTrailer.adapter = movieTrailerAdapter
//        Log.d("Trailer", trailer.get(0).key)
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
                linkContent = ShareLinkContent.Builder()
                    .setQuote("$title")
                    .setContentUrl(Uri.parse("https://www.youtube.com/watch?v=${shareTrailer}"))
                    .build()
                if (ShareDialog.canShow(linkContent.javaClass)) {
                    shareDialog.show(linkContent)
                }
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