package barbearia_producao.aab.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import barbearia_producao.aab.R
import barbearia_producao.aab.model.Portfolio
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class ManagementAdapter(
    private val context: Context,
    private val list: MutableList<Portfolio>,
    private val onDelete: (Portfolio) -> Unit,
    private val onEdit: (Portfolio) -> Unit
) : RecyclerView.Adapter<ManagementAdapter.ManagementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagementViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.management_item, parent, false)
        return ManagementViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManagementViewHolder, position: Int) {
        val item = list[position]

        Glide.with(context)
            .load(item.url)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(20)))
            .into(holder.img)

        holder.desc.text = item.description

        holder.btnDelete.setOnClickListener { onDelete(item) }
        holder.btnEdit.setOnClickListener { onEdit(item) }
    }

    override fun getItemCount(): Int = list.size

    inner class ManagementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgManagement)
        val desc: TextView = view.findViewById(R.id.textManagementDesc)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
    }
}
