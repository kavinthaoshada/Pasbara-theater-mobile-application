package lk.calm.pasbaratheater01;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class QRDialogActivity extends Dialog {

    public static final String TAG = BookingHistoryActivity.class.getName();
    Bitmap qrCodeBitmap;
    private ImageView qrCodeImageView;

    public QRDialogActivity(@NonNull Context context, Bitmap qrCodeBitmap) {
        super(context);
        this.qrCodeBitmap = qrCodeBitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdialog);

        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        Log.i(TAG, "img ref in dialog : "+qrCodeImageView);
        qrCodeImageView.setImageBitmap(qrCodeBitmap);

        Button closeButton = findViewById(R.id.btnClose);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}