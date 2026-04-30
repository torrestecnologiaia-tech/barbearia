package barbearia_producao.aab.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import barbearia_producao.aab.databinding.PortfolioItemBinding
import barbearia_producao.aab.model.Portfolio
import com.bumptech.glide.Glide

class PortfolioAdapter(
    private val context: Context,
    private val listPortfolio: MutableList<Portfolio>
) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val item = PortfolioItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return PortfolioViewHolder(item)
    }

    override fun getItemCount() = listPortfolio.size

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val portfolio = listPortfolio[position]
        
        Glide.with(context)
            .load(portfolio.url)
            .into(holder.imgPortfolio)
            
        holder.textDesc.text = portfolio.description
    }

    inner class PortfolioViewHolder(binding: PortfolioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgPortfolio = binding.imgPortfolio
        val textDesc = binding.textPortfolioDesc
    }
}
