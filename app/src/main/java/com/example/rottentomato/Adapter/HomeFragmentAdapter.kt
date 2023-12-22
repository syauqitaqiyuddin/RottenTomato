package com.example.rottentomato.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rottentomato.MovieDetailActivity
import com.example.rottentomato.R
import com.example.rottentomato.database.MovieData

class HomeFragmentAdapter (private val context: Context,
                           private val movieList: List<MovieData>
) : RecyclerView.Adapter<HomeFragmentAdapter.HomeFragmentViewHolder>(){
    inner class HomeFragmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJudul: TextView = itemView.findViewById(R.id.tvTitle)
        val tvGenre: TextView = itemView.findViewById(R.id.tvGenre)
        val tvDirector: TextView = itemView.findViewById(R.id.tvDirector)
        val imgPoster: ImageView = itemView.findViewById(R.id.ivPoster)
        val cardView: CardView = itemView.findViewById(R.id.cardview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_film_viewholder, parent, false)
        return HomeFragmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeFragmentViewHolder, position: Int) {
        val movie = movieList[position]
        holder.tvJudul.text = movie.judul_film
        holder.tvGenre.text = movie.genre
        holder.tvDirector.text = movie.direktor
        Glide.with(context).load(movie.posterUrl).into(holder.imgPoster)

        holder.cardView.setOnClickListener {
            val intent = Intent(context, MovieDetailActivity::class.java)
            intent.putExtra("movieToEdit", movie.id)
            intent.putExtra("movieTitle", movie.judul_film)
            intent.putExtra("movieGenre", movie.genre)
            intent.putExtra("movieDirector", movie.direktor)
            intent.putExtra("moviePoster", movie.posterUrl)
            intent.putExtra("movieDeskripsi", movie.deskripsi)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }
}