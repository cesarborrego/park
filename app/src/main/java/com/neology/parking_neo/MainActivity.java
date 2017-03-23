package com.neology.parking_neo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.neology.parking_neo.adapters.ViewPagerAdapter;
import com.neology.parking_neo.fragments.MapFragment;
import com.neology.parking_neo.fragments.PagoFragment;
import com.neology.parking_neo.util_vending.IabHelper;
import com.neology.parking_neo.util_vending.IabResult;
import com.neology.parking_neo.util_vending.Inventory;
import com.neology.parking_neo.util_vending.Purchase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SKU_10 = "parki_1";
    private static final String SKU_20 = "parki_20";
    private static final String SKU_30 = "parki_30";
    private static final String SKU_40 = "parki_40";
    private static final String SKU_50 = "parki_50";
    private static final String SKU_1_00 = "parki_1_00";

    private static String SKU = "android.test.purchased";


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.drawable.icon_park,
            R.drawable.icon_pago
    };
    private IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        */

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcon();

        connectGooglePlay();
    }

    private void setupTabIcon() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MapFragment(), "ONE");
        adapter.addFragment(new PagoFragment(), "TWO");
        viewPager.setAdapter(adapter);
    }

    private void connectGooglePlay() {
        mHelper = new IabHelper(this, getResources().getString(R.string.api_key_billing));
        mHelper.enableDebugLogging(true, TAG);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    View.OnClickListener comprarListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            consultaItemnsDisponibles();
            //openBilling();
        }
    };

    public void openBilling() {
        try {
            Random random = new Random();
            int requestCode = random.nextInt(65535);
            if(requestCode<0) {
                requestCode = requestCode*-1;
            }
            mHelper.launchPurchaseFlow(this, SKU, requestCode,
                    mPurchaseFinishedListener, "mypurchasetoken");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                return;
            } else if (purchase.getSku().equals(SKU)) {
                consumeItem();
            }

        }
    };

    private void consultaItemnsDisponibles() {
        ArrayList<String> additionalSkuList = new ArrayList<String>();
        additionalSkuList.add(SKU_10);
        additionalSkuList.add(SKU_20);
        additionalSkuList.add(SKU_30);
        additionalSkuList.add(SKU_40);
        additionalSkuList.add(SKU_50);
        additionalSkuList.add(SKU_1_00);
        try {
            mHelper.queryInventoryAsync  (true, null,  additionalSkuList, mReceivedInventoryListenerDisponibles);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListenerDisponibles
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {


            if (result.isFailure()) {
                // Handle failure
            } else {
                String applePrice =
                        inventory.getSkuDetails(SKU_1_00).getPrice();
                String bananaPrice =
                        inventory.getSkuDetails(SKU_10).getPrice();
                Log.d(TAG, "item disponible " + applePrice+bananaPrice);
            }
        }
    };

    public void consumeItem() {
        try {
            mHelper.queryInventoryAsync(mReceivedInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {


            if (result.isFailure()) {
                // Handle failure
            } else {
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU),
                            mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        //LANZAR EL SERVICIO PARA ACTUALIZAR EL SALDO Y ACTUALIZAR LA UI CON EL SALDO
                    } else {
                        // handle error
                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }
}
