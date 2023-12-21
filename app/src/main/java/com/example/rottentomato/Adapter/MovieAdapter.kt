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
import com.example.rottentomato.EditFilmActivity
import com.example.rottentomato.R
import com.example.rottentomato.database.MovieData

class MovieAdapter (private val context: Context,
                    private val movieList: List<MovieData>
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){
inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvJudul: TextView = itemView.findViewById(R.id.tvTitle)
        val tvGenre: TextView = itemView.findViewById(R.id.tvGenre)
        val tvDirector: TextView = itemView.findViewById(R.id.tvDirector)
        val imgPoster: ImageView = itemView.findViewById(R.id.ivPoster)
        val cardView: CardView = itemView.findViewById(R.id.cardview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_film_viewholder, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.tvJudul.text = movie.judul_film
        holder.tvGenre.text = movie.genre
        holder.tvDirector.text = movie.direktor
        Glide.with(context).load(movie.posterUrl).into(holder.imgPoster)

        holder.cardView.setOnClickListener {
            val intent = Intent(context, EditFilmActivity::class.java)
            intent.putExtra("movieToEdit", movie.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }
}
