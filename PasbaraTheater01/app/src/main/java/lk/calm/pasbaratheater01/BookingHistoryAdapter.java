package lk.calm.pasbaratheater01;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lk.calm.pasbaratheater01.model.BookHistory;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingHistoryViewHolder> {
    public static final String TAG = BookingHistoryActivity.class.getName();
    private List<BookHistory> bookHistories;
    private BookingHistoryListener bookingHistoryListener;

    public BookingHistoryAdapter(List<BookHistory> bookHistories, BookingHistoryListener bookingHistoryListener) {
        this.bookHistories = bookHistories;
        this.bookingHistoryListener = bookingHistoryListener;
        Log.i(TAG, "in adapter constructor :"+bookingHistoryListener);
        Log.i(TAG, "in adapter constructor :"+bookHistories.size());
    }

    @NonNull
    @Override
    public BookingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "in adapter onCreateViewHolder booking");
        return new BookingHistoryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.booking_history_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BookingHistoryViewHolder holder, int position) {
        Log.i(TAG, "in onBindViewHolder");
        holder.bindBookingHistory(bookHistories.get(position));

        BookHistory bookHistory = bookHistories.get(position);
    }

    @Override
    public int getItemCount() {
        return bookHistories.size();
    }

    public List<BookHistory> getSelectedBookHistories(){
        List<BookHistory> selectedBookHistories = new ArrayList<>();
        for(BookHistory bookHistory : bookHistories){
            if(bookHistory.isSelected){
                selectedBookHistories.add(bookHistory);
            }
        }
        return selectedBookHistories;
    }

    class BookingHistoryViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layoutBookingHistory;
        ConstraintLayout backgroundConstraintLayout;
        TextView textTitle, textTimeStamp, textDetail;

        BookingHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutBookingHistory = itemView.findViewById(R.id.layoutBookingHistory);
            backgroundConstraintLayout = itemView.findViewById(R.id.backgroundConstraintLayout);
            textTitle = itemView.findViewById(R.id.textTitle);
            textTimeStamp = itemView.findViewById(R.id.textTimeStamp);
            textDetail = itemView.findViewById(R.id.textDetail);
        }

        void bindBookingHistory(final BookHistory bookHistory){

            Log.i(TAG, "in adapter bookingHistory");

            textTitle.setText(bookHistory.title);
            textTimeStamp.setText(bookHistory.timeStamp);
            textDetail.setText(bookHistory.seat+" seats | total cost: "+bookHistory.total_cost+".00 LKR");
            if(bookHistory.isSelected){
//                viewBackground.setBackgroundResource(R.drawable.tv_show_selected_backgound);
//                imageSelected.setVisibility(View.VISIBLE);
            }else{
//                viewBackground.setBackgroundResource(R.drawable.tv_show_background);
//                imageSelected.setVisibility(View.GONE);
            }
            layoutBookingHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bookHistory.isSelected){
                        bookHistory.isSelected = false;
                        if(getSelectedBookHistories().size()==0){
                            bookingHistoryListener.onBookingHistoryAction(false, getSelectedBookHistories().size());
                        }
                    }else{
                        bookHistory.isSelected = true;
                        bookingHistoryListener.onBookingHistoryAction(true,getSelectedBookHistories().size());
                    }
                }
            });
        }
    }

}
