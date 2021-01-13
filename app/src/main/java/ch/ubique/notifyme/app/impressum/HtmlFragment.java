/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.app.impressum;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ch.ubique.notifyme.app.R;

public class HtmlFragment extends Fragment {

	private static final String ARG_BASE_URL = "ARG_BASE_URL";
	private static final String ARG_DATA = "ARG_DATA";

	private String baseUrl;
	private String data;

	public static HtmlFragment newInstance(String baseUrl, @Nullable String data) {
		Bundle args = new Bundle();
		args.putString(ARG_BASE_URL, baseUrl);
		args.putString(ARG_DATA, data);
		HtmlFragment fragment = new HtmlFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public HtmlFragment() {
		super(R.layout.fragment_html);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		baseUrl = getArguments().getString(ARG_BASE_URL);
		data = getArguments().getString(ARG_DATA);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		WebView web = view.findViewById(R.id.html_fragment_webview);
		View loadingSpinner = view.findViewById(R.id.html_fragment_loading_spinner);
		View closeButton = view.findViewById(R.id.html_fragment_close_button);
		closeButton.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

		web.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				loadingSpinner.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (baseUrl.equals(url)) return true;
				openUrl(url);
				return true;
			}
		});

		WebSettings webSettings = web.getSettings();
		webSettings.setJavaScriptEnabled(true);
		if (data != null) {
			web.loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", null);
		} else {
			web.loadUrl(baseUrl);
		}
	}

	private void openUrl(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		try {
			requireActivity().startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(requireContext(), "No browser installed", Toast.LENGTH_LONG).show();
		}
	}

}
