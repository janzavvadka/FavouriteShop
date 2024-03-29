package pl.janzawadka.favouriteshop.shop_list

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import pl.janzawadka.favouriteshop.shop_editor.PictureUtil
import pl.janzawadka.favouriteshop.R
import pl.janzawadka.favouriteshop.database.StorageService
import pl.janzawadka.favouriteshop.model.Shop
import pl.janzawadka.favouriteshop.shop_editor.ShopEditorActivity
import pl.janzawadka.favouriteshop.intent_operation.ShopOperation

class ShopListRecycleAdapter(var shops: ArrayList<Shop>) : androidx.recyclerview.widget.RecyclerView.Adapter<ShopListRecycleAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var itemImage: ImageView = itemView.findViewById(R.id.item_image)
        var itemTitle: TextView = itemView.findViewById(R.id.item_title)
        var itemDetail: TextView = itemView.findViewById(R.id.item_detail)
        var shopCategory: TextView = itemView.findViewById(R.id.shop_category)

        init {
            itemView.setOnClickListener {v: View ->
                val shop: Shop = shops[adapterPosition]

                val intent = Intent(v.context, ShopEditorActivity::class.java)
                intent.putExtra(ShopOperation.KEY_SHOP, createJsonFromShopDTO(shop))
                intent.putExtra(ShopOperation.KEY_OPERATION, ShopOperation.EDIT)
                v.context.startActivity(intent)
            }
        }

        fun createJsonFromShopDTO(shopDTO: Shop) : String {
            return Gson().toJson(shopDTO)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycle_shop_element_layout, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        if(shops[i].photoPath.length > 1) {
            val pictureReference = StorageService.findPictureByLink(shops[i].photoPath)
            PictureUtil.bitmapFromReference(pictureReference) {
                viewHolder.itemImage.setImageBitmap(it)
            }
        }
        val description = "Description : ${shops[i].description}"
        val category = "Category : ${shops[i].category}"
        viewHolder.itemTitle.text = shops[i].name
        viewHolder.itemDetail.text = description
        viewHolder.shopCategory.text = category
    }


    override fun getItemCount(): Int {
        return shops.size
    }
}