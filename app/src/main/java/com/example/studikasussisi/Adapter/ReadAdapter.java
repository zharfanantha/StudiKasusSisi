package com.example.studikasussisi.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studikasussisi.R;
import com.example.studikasussisi.Repository.Model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReadAdapter extends RecyclerView.Adapter<ReadAdapter.ReadViewHolder> {
    private List<User> readList = new ArrayList<>();
    Listener mlistener;

    public ReadAdapter(List<User> readList, Listener listener){
        this.readList = readList;
        this.mlistener = listener;
    }

    public void updateList(List<User> newUsers){
        readList.clear();
        readList = newUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        ReadViewHolder viewHolder = new ReadViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ReadViewHolder holder, final int position) {
        holder.bind(readList.get(position));
        final String id = holder.getId();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onClick(readList.get(position));
            }
        });
    }

    public interface Listener {
        void onClick(User User);
    }

    @Override
    public int getItemCount() {
        return readList.size();
    }

    class ReadViewHolder extends RecyclerView.ViewHolder {

        TextView account, nama, type;
        private String itemId;
        ImageView iv;

        public String getId() {
            return itemId;
        }

        public ReadViewHolder(@NonNull View itemView) {
            super(itemView);
            account = itemView.findViewById(R.id.read_account);
            nama = itemView.findViewById(R.id.read_nama);
            type = itemView.findViewById(R.id.read_type);
            iv = itemView.findViewById(R.id.read_iv);
        }

        void bind(User deliveredUser){
            String umur;
            account.setText(deliveredUser.getNoKtp());
            nama.setText(deliveredUser.getNama());
            itemId = deliveredUser.getNoKtp();
            iv.setImageBitmap(decodeImage(deliveredUser.getFoto()));
            umur = calculateAge(deliveredUser.getTglLahir());
            type.setText(umur);
        }
    }

    private Bitmap decodeImage(String base64){
        base64 = base64.replace("data:image/png;base64,", "");
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    private String calculateAge(String born) {
        Date birthDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            birthDate = sdf.parse(born);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int years = 0, months = 0, days = 0;
        Calendar birthday = Calendar.getInstance();
        birthday.setTimeInMillis(birthDate.getTime());

        long currTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currTime);

        years = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthday.get(Calendar.MONTH) + 1;

        months = currMonth - birthMonth;
        if (months < 0) {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthday.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthday.get(Calendar.DATE)) {
            years--;
            months = 11;
        }

        if (now.get(Calendar.DATE) > birthday.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthday.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthday.get(Calendar.DATE)) {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH) + today;
        }
        else {
            days = 0;
            if (months == 12) {
                years++;
                months = 0;
            }
        }

        return years+" tahun";
    }
}
