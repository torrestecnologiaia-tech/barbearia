package barbearia_producao.aab.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import barbearia_producao.aab.R
import barbearia_producao.aab.model.Portfolio
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class PortfolioAdapter(private val context: Context, private val portfolioList: MutableList<Portfolio>) :
    RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.portfolio_item, parent, false)
        return PortfolioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val portfolio = portfolioList[position]

        // Carrega imagem com Glide e cantos arredondados
        Glide.with(context)
            .load(portfolio.url)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(30)))
            .into(holder.imgPortfolio)

        holder.textPortfolioDesc.text = portfolio.description
    }

    override fun getItemCount(): Int = portfolioList.size

    inner class PortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPortfolio: ImageView = itemView.findViewById(R.id.imgPortfolio)
        val textPortfolioDesc: TextView = itemView.findViewById(R.id.textPortfolioDesc)
    }
}
