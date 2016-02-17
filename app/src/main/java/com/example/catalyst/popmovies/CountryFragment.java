package com.example.catalyst.popmovies;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.catalyst.popmovies.data.DBHelper;
import com.example.catalyst.popmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dsloane on 2/16/2016.
 */
public class CountryFragment extends DialogFragment {

    //private ArrayList<String> countries = new ArrayList<String>();

    private final String LOG_TAG = CountryFragment.class.getSimpleName();
    public CountryFragment() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //ListView countryList = new ListView(getActivity());
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, getResources().getStringArray(R.array.countries));

        //countryList.setAdapter(adapter);
        final View countryView = getActivity().getLayoutInflater().inflate(R.layout.fragment_country, null);
        RadioGroup countries = (RadioGroup)countryView.findViewById(R.id.countrylist);

        countries.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ArrayList<String> certifications = new ArrayList<String>();
                RadioGroup.LayoutParams rprms;
                RadioGroup certs = (RadioGroup)countryView.findViewById(R.id.rating_certifications);
                certs.removeAllViews();
                switch (checkedId) {
                    case(R.id.country_usa):
                        System.out.println("clicked usa");
                        for (int i = 0; i < getResources().getStringArray(R.array.usa_certs).length; i++) {
                            certifications.add(getResources().getStringArray(R.array.usa_certs)[i]);
                            System.out.println(certifications.get(i));
                        }
                        //certs.check(R.id.us_rating_nc17);
                        break;
                    case(R.id.country_uk):
                        for (int i = 0; i < getResources().getStringArray(R.array.uk_certs).length; i++) {
                            certifications.add(getResources().getStringArray(R.array.uk_certs)[i]);
                            System.out.println(certifications.get(i));
                        }
                        //certs.check(R.id.uk_rating_18);
                        break;
                    case(R.id.country_australia):
                        for (int i = 0; i < getResources().getStringArray(R.array.aus_certs).length; i++) {
                            certifications.add(getResources().getStringArray(R.array.aus_certs)[i]);
                            System.out.println(certifications.get(i));
                        }
                        break;
                    case(R.id.country_canada):
                        for (int i = 0; i < getResources().getStringArray(R.array.canada_certs).length; i++) {
                            certifications.add(getResources().getStringArray(R.array.canada_certs)[i]);
                            System.out.println(certifications.get(i));
                        }
                        break;
                    case(R.id.country_france):
                        for (int i = 0; i < getResources().getStringArray(R.array.fr_certs).length; i++) {
                            certifications.add(getResources().getStringArray(R.array.fr_certs)[i]);
                            System.out.println(certifications.get(i));
                        }
                        break;
                    case(R.id.country_germany):
                        for (int i = 0; i < getResources().getStringArray(R.array.germany_certs).length; i++) {
                            certifications.add(getResources().getStringArray(R.array.germany_certs)[i]);
                            System.out.println(certifications.get(i));
                        }
                        break;
                    case(R.id.country_india):
                        for (int i = 0; i < getResources().getStringArray(R.array.india_certs).length; i++) {
                            certifications.add(getResources().getStringArray(R.array.india_certs)[i]);
                            System.out.println(certifications.get(i));
                        }
                        break;
                    case(R.id.country_new_zealand):
                        for (int i = 0; i < getResources().getStringArray(R.array.nz_certs).length; i++) {
                            certifications.add(getResources().getStringArray(R.array.nz_certs)[i]);
                            System.out.println(certifications.get(i));
                        }
                        break;
                    default:
                        System.out.println("no match found");

                }
                for (int i = 0; i < certifications.size(); i++) {
                    RadioButton radioButton = new RadioButton(getContext());
                    radioButton.setText(certifications.get(i));
                    rprms = new RadioGroup.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
                    certs.addView(radioButton, rprms);
                }

            }
        });

        //countries.setAdapter(adapter);

        builder.setView(countryView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RadioGroup country = (RadioGroup)countryView.findViewById(R.id.countrylist);
                        String countryCode;
                        String rating;
                        int checkedId = country.getCheckedRadioButtonId();
                        View radioButton = country.findViewById(checkedId);
                        int idx = country.indexOfChild(radioButton);
                        RadioButton r = (RadioButton) country.getChildAt(idx);
                        String selectedCountry =  (String) r.getText();

                        RadioGroup cert = (RadioGroup)countryView.findViewById(R.id.rating_certifications);
                        int checkedCertId = cert.getCheckedRadioButtonId();
                        View certButton = cert.findViewById(checkedCertId);
                        int certIdx = cert.indexOfChild(certButton);
                        RadioButton cb = (RadioButton) cert.getChildAt(certIdx);
                        String selectedRating = (String) cb.getText();

                        switch (selectedCountry) {
                            case ("USA"):
                                countryCode = "US";
                                break;
                            case ("UK"):
                                countryCode = "GB";
                                break;
                            case ("Germany"):
                                countryCode = "DE";
                                break;
                            case ("France"):
                                countryCode = "FR";
                                break;
                            case ("India"):
                                countryCode = "IN";
                                break;
                            case ("Canada"):
                                countryCode = "CA";
                                break;
                            case ("Australia"):
                                countryCode = "AU";
                                break;
                            case ("New Zealand"):
                                countryCode = "NZ";
                                break;
                            default:
                                countryCode = "US";
                                break;

                        }

                        rating = selectedRating;

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                        editor.putString(getString(R.string.pref_search_key), (String) null);
                        editor.putString(getString(R.string.country_filter), countryCode);
                        editor.putString(getString(R.string.certification_filter), rating);
                        editor.apply();

                        getActivity().setIntent(null);
                        startActivity(new Intent(getActivity(), MainActivity.class));

                        //getMoviesByCountry(countryCode, rating);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Never mind");
                    }
                });

        return builder.create();
    }

    public static CountryFragment newInstance() {
        CountryFragment fragment = new CountryFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
