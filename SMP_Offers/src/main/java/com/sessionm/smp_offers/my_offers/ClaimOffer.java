package com.sessionm.smp_offers.my_offers;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sessionm.api.SessionMError;
import com.sessionm.api.offers.OffersListener;
import com.sessionm.api.offers.OffersManager;
import com.sessionm.api.offers.data.results.claim.UserOfferClaimedResult;
import com.sessionm.api.offers.data.results.purchase.OfferPurchaseResult;
import com.sessionm.api.offers.data.results.store.OffersStoreResult;
import com.sessionm.api.offers.data.results.user.UserOfferItem;
import com.sessionm.api.offers.data.results.user.UserOffersResult;
import com.sessionm.smp_offers.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ClaimOffer {
    private final Activity _activity;
    private UserOfferItem _offer;
    private OffersManager _offerManager = OffersManager.getInstance();

    public ClaimOffer(Activity activity) {
        _activity = activity;
    }

    private Gson _gson = new Gson();

    public void redeem(UserOfferItem offer) {
        _offer = offer;
        _offerManager.setListener(claimListener);
        _offerManager.claimUserOffer(offer.getID());
    }

    private OffersListener claimListener = new OffersListener() {
        public AlertDialog _dialog;
        public Date _start;
        public Timer _timer;
        public TextView _countDown;

        @Override public void onOfferPurchased(OfferPurchaseResult purchase) {}
        @Override public void onUserOffersFetched(UserOffersResult userOffers) {}
        @Override public void onOffersStoreFetched(OffersStoreResult offersStore) {}

        @Override
        public void onUserOfferClaimed(UserOfferClaimedResult claimedResult) {

            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
            builder.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("TAG", "button dismiss");
                }
            });

            View dialogLayout = _activity.getLayoutInflater().inflate(R.layout.claim_offer, null);

            Picasso.with(_activity).load(Uri.parse(claimedResult.getClaimedOffer().getCodeImageURI())).into((ImageView) dialogLayout.findViewById(R.id.barcode_image));
            Picasso.with(_activity).load(Uri.parse(_offer.getMedia().get(0).getURI())).into((ImageView) dialogLayout.findViewById(R.id.picture));

            ((TextView) dialogLayout.findViewById(R.id.barcode_text)).setText(claimedResult.getClaimedOffer().getCode());
            ((TextView) dialogLayout.findViewById(R.id.title)).setText(claimedResult.getClaimedOffer().getName());
            ((TextView) dialogLayout.findViewById(R.id.description)).setText(claimedResult.getClaimedOffer().getDescription());
            ((TextView) dialogLayout.findViewById(R.id.expiration_date)).setText("Expires: " + dateTimeFormat.format(claimedResult.getClaimedOffer().getCodeExpirationDateTime()));

            _dialog = builder.create();
            _dialog.setView(dialogLayout);
            _dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            _dialog.show();

            countDown(dialogLayout);
        }

        private void countDown(View dialogLayout) {
            _countDown = (TextView) dialogLayout.findViewById(R.id.countdown);
            _start = new Date();
            _timer = new Timer();
            _timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Date now = new Date();
                    final long secs = (now.getTime() - _start.getTime()) / 1000;
                    if ((60 - secs) > 0) {
                        _activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                _countDown.setText(String.format("Automatic dismiss in %02d seconds", 60 - secs));
                            }
                        });
                    } else {
                        _timer.cancel();
                        _dialog.dismiss();
                        _timer = null;
                    }
                }
            }, 0, 1 * 1000);
        }

        @Override
        public void onFailure(SessionMError error) {
            Toast.makeText(_activity, "Failure: '" + error.getCode() + "' " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

}