import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.Laporan

class LaporanAdapter(private var laporanList: List<Laporan>) :
    RecyclerView.Adapter<LaporanAdapter.LaporanViewHolder>() {

    class LaporanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaProdukTextView: TextView = itemView.findViewById(R.id.namaProdukTextView)
        val stokTextView: TextView = itemView.findViewById(R.id.stokTextView)
        val hargaTextView: TextView = itemView.findViewById(R.id.hargaTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data_laporan, parent, false)
        return LaporanViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LaporanViewHolder, position: Int) {
        val currentLaporan = laporanList[position]
        holder.namaProdukTextView.text = currentLaporan.namaProduk
        holder.stokTextView.text = currentLaporan.stok.toString()
        holder.hargaTextView.text = currentLaporan.harga.toString()
    }
    fun updateLaporanList(newLaporanList: List<Laporan>) {
        this.laporanList = newLaporanList
        notifyDataSetChanged()
    }
    override fun getItemCount() = laporanList.size
}
