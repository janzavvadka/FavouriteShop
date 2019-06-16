package pl.janzawadka.favouriteshop.shop_list

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import pl.janzawadka.favouriteshop.R
import pl.janzawadka.favouriteshop.database.DatabaseService
import pl.janzawadka.favouriteshop.model.Shop
import pl.janzawadka.favouriteshop.shop_editor.ShopEditorActivity
import pl.janzawadka.favouriteshop.shop_editor.static.ShopOperation

class ShopListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewadapter: RecyclerView.Adapter<ShopListRecycleAdapter.ViewHolder>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_list)


        DatabaseService.findAllShopsForCurrentUser {
            createRecycleView(it)
        }
    }

    private fun createRecycleView(shops: ArrayList<Shop>) {
        viewadapter = ShopListRecycleAdapter(shops)
        viewManager = LinearLayoutManager(this)

        recyclerView = findViewById<RecyclerView>(R.id.shop_recycle_view)
            .apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewadapter
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.custom_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_new_shop -> {
                intent = Intent(this, ShopEditorActivity::class.java)
                intent.putExtra(ShopOperation.KEY_OPERATION, ShopOperation.ADD)
                startActivity(intent)
                return true
            }

            R.id.search -> {
                return true
            }

            R.id.map_view_item -> {
//                intent = Intent(this, MapOfShops::class.java)
//                startActivity(intent)
                return true
            }
            else -> return false
        }
    }

}
