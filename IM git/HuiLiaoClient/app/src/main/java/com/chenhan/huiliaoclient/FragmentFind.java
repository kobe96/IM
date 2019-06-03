package com.chenhan.huiliaoclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentFind extends Fragment {

    private Bundle bundle = new Bundle();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_find, null);
        bundle = this.getArguments();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);
        getActivity().setTitle("发现");
        getActivity().findViewById(R.id.Find_The_Truth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTheTruth fragmentTheTruth = new FragmentTheTruth();
                fragmentTheTruth.setArguments(bundle);
                getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frameLayout,fragmentTheTruth)
                        .commit();
            }
        });

        getActivity().findViewById(R.id.Find_MySettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMySettings fragmentMySettings = new FragmentMySettings();
                fragmentMySettings.setArguments(bundle);
                getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frameLayout,fragmentMySettings)
                        .commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);
    }
}