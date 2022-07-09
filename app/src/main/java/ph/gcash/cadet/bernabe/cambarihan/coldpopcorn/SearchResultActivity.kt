package ph.gcash.cadet.bernabe.cambarihan.coldpopcorn

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.adapter.MoviesAdapter
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.api.MoviesRepository
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.databinding.ActivitySearchResultBinding
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.model.Movie

class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultBinding

    private lateinit var searchContent: RecyclerView
    private lateinit var searchContentAdapter: MoviesAdapter
    private lateinit var searchContentLayoutMgr: LinearLayoutManager
    private lateinit var resultQuery: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultQuery = binding.resultQueryTV
        searchContent = binding.searchContent
        searchContentLayoutMgr = GridLayoutManager(
            this,
            2,
            GridLayoutManager.VERTICAL,
            false
        )
        searchContent.layoutManager = searchContentLayoutMgr
        searchContentAdapter = MoviesAdapter(mutableListOf()) { movie -> showMovieDetails(movie) }
        searchContent.adapter = searchContentAdapter

        val extras = intent.extras

        if (extras != null) {
                resultQuery.text = extras.getString("query", "")
                getSearchContent(extras.getString("query", ""))
        } else {
            finish()
        }
    }

    private fun getSearchContent(query: String) {
       MoviesRepository.getSearchContent(
           query,
           ::onSearchContentFetched,
           ::onError
       )
    }

    private fun onSearchContentFetched(movies: List<Movie>) {
        searchContentAdapter.updateMovies(movies)
    }

    private fun onError() {
        Toast.makeText(this, getString(R.string.error_fetch_movies), Toast.LENGTH_SHORT).show()
    }

    private fun showMovieDetails(movie: Movie) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra(MOVIE_BACKDROP, movie.backdropPath)
        intent.putExtra(MOVIE_POSTER, movie.posterPath)
        intent.putExtra(MOVIE_TITLE, movie.title)
        intent.putExtra(MOVIE_RATING, movie.rating)
        intent.putExtra(MOVIE_RELEASE_DATE, movie.releaseDate)
        intent.putExtra(MOVIE_OVERVIEW, movie.overview)
        intent.putExtra(MOVIE_HOMEPAGE, movie.homepage)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

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

    private fun searchMovie(searchQuery: String) {
//          Log.d("Search", "Search-Content: $searchQuery")
        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra("query", searchQuery)
        startActivity(intent)
    }
}