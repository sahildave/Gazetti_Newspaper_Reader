package com.example.try_masterdetail.homescreen.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.try_masterdetail.R;

public class AddCellDialogFragment extends DialogFragment implements OnItemSelectedListener {

	private Spinner spinner_newspaper;
	private Spinner spinner_category;

	public interface AddCellDialogListener {
		void onFinishAddingListener(String np, String cat);
	}

	public AddCellDialogFragment() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.add_new_cell_dialog, null);

		spinner_newspaper = (Spinner) v.findViewById(R.id.add_cell_newpaper_spinner);
		spinner_category = (Spinner) v.findViewById(R.id.add_cell_category_spinner);

		spinner_newspaper.setOnItemSelectedListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Add New Shortcut");
		builder.setView(v);
		builder.setPositiveButton(R.string.add_cell_dialog_add, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				String np = spinner_newspaper.getSelectedItem().toString();
				String cat = spinner_category.getSelectedItem().toString();

				AddCellDialogListener activity = (AddCellDialogListener) getActivity();
				activity.onFinishAddingListener(np, cat);
			}
		});
		builder.setNegativeButton(R.string.add_cell_dialog_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast.makeText(getActivity(), "User clicked Cancel", Toast.LENGTH_SHORT).show();
			}
		});

		AlertDialog dialog = builder.create();
		return dialog;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		if (position == 0) {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.hindu_cat,
					android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_category.setAdapter(adapter);
		} else if (position == 1) {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.toi_cat,
					android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_category.setAdapter(adapter);
		} else if (position == 2) {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.fp_cat,
					android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_category.setAdapter(adapter);
		} else if (position == 3) {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.ht_cat,
					android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_category.setAdapter(adapter);
		} else if (position == 4) {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.tie_cat,
					android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_category.setAdapter(adapter);
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

}
