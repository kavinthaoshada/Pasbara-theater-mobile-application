package lk.calm.pasbaratheater01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent;
import com.stripe.android.view.CardInputWidget;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CheckoutActivity extends AppCompatActivity {

    public static final String TAG = CheckoutActivity.class.getName();
    private static final String BACKEND_URL = "http://10.0.2.2:4242/";
    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51LauixIjAOH1QSloFfHFOFUN8xQ3Bc1XuB3ouIeZVK0dwXAnJZ3qpsnI7dDvsKufxmdhS0H7Y4SEM4sfpMASzh8x009HAJpvuC")
        );
        startCheckout();
    }
    private void startCheckout() {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
//        String json = "{"
//                +"\"currency\":\"usd\","
//                +"\"items\":["
//                +"{\"id\":\"photo_subscription\"}"
//                +"]"
//                +"}";

        double amount = 123.0;
        Map<String, Object> payMap = new HashMap<>();
        Map<String, Object> itemMap = new HashMap<>();
        List<Map<String, Object>> itemList = new ArrayList<>();
        payMap.put("currency","usd");
        itemMap.put("id", "photo_subscription");
        itemMap.put("amount", amount);
        itemList.add(itemMap);
        payMap.put("items",itemList);
        String json = new Gson().toJson(payMap);

        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL+"create-payment-intent")
                .post(body)
                .build();

        httpClient.newCall(request)
                .enqueue(new PayCallBack(this));

        Button payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener((View view)->{
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if(params != null){
                Log.i(TAG, "s key : "+paymentIntentClientSecret);
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(this, confirmParams);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private static final class PayCallBack implements Callback{
        @NonNull private final WeakReference<CheckoutActivity> activityRef;
        PayCallBack(@NonNull CheckoutActivity activity){
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final CheckoutActivity activity = activityRef.get();
            if(activity==null){
                return;
            }
            activity.runOnUiThread(()->
                            Log.i(TAG, "Err : "+e.toString())
//                    Toast.makeText(
//                            activity, "Error : "+e.toString(), Toast.LENGTH_LONG
//                    ).show()
            );
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            final CheckoutActivity activity = activityRef.get();
            if(activity == null){
                return;
            }
            if(!response.isSuccessful()){
                activity.runOnUiThread(()->
                        Log.i(TAG, "Err : "+response.toString())
//                        Toast.makeText(
//                                activity, "Error : "+response.toString(), Toast.LENGTH_LONG
//                        ).show()
                );
            }else {
                activity.onPaymentSuccess(response);
            }
        }
    }
    private void onPaymentSuccess(@NonNull final Response response) throws IOException{
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String,String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("clientSecret");
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult>{
        @NonNull private final WeakReference<CheckoutActivity> activityRef;
        PaymentResultCallback(@NonNull CheckoutActivity activity){
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(PaymentIntentResult result) {
            final CheckoutActivity activity = activityRef.get();
            if(activity == null){
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if(status == PaymentIntent.Status.Succeeded){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                activity.displayAlert(
                        "Payment Completed",
                        gson.toJson(paymentIntent)
                );
            }else if(status == PaymentIntent.Status.RequiresPaymentMethod){
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CheckoutActivity activity = activityRef.get();
            if(activity == null){
                return;
            }
            activity.displayAlert("Error", e.toString());
        }
    }
    private void displayAlert(@NonNull String title,
                              @Nullable String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("ok", null);
        builder.create().show();
    }
}