package in.sahildave.gazetti.homescreen.adapter;

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
import in.sahildave.gazetti.R;

public class EditCellDialogFragment extends DialogFragment implements OnItemSelectedListener {

    private Spinner spinner_newspaper;
    private Spinner spinner_category;
    private String[] newspaperArray;

    public interface EditCellDialogListener {
        void onFinishEditingListener(int editPosition, String np, String cat, boolean edited);
    }

    public static EditCellDialogFragment newInstance(int position, int newspaperId, String category) {
        EditCellDialogFragment f = new EditCellDialogFragment();

        // Supply num input as an argument.
        Bundle editThisCellBundle = new Bundle();
        editThisCellBundle.putInt("EDITCELL", position);
        editThisCellBundle.putInt("NEWSPAPERID", newspaperId);
        editThisCellBundle.putString("CATEGORY", category);

        f.setArguments(editThisCellBundle);

        return f;
    }

    public EditCellDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v = inflater.inflate(R.layout.edit_cell_dialog, null);

        int newspaperID = getArguments().getInt("NEWSPAPERID");
        String cat = getArguments().getString("CATEGORY");
        final int editPosition = getArguments().getInt("EDITCELL");

        newspaperArray = getResources().getStringArray(R.array.newspaper_array);
        //Log.d("Fullscreen", newspaperID + ", " + cat);

        spinner_newspaper = (Spinner) v.findViewById(R.id.add_cell_newpaper_spinner);
        spinner_category = (Spinner) v.findViewById(R.id.add_cell_category_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                newspaperArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_newspaper.setAdapter(adapter);

        spinner_newspaper.setSelection(newspaperID);
        spinner_newspaper.setOnItemSelectedListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Shortcut");
        builder.setView(v);
        builder.setPositiveButton(R.string.edit_cell_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String np = (String) spinner_newspaper.getSelectedItem();
                String cat = (String) spinner_category.getSelectedItem();
                EditCellDialogListener activity = (EditCellDialogListener) getActivity();
                activity.onFinishEditingListener(editPosition, np, cat, true);
            }
        });
        builder.setNegativeButton(R.string.add_cell_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditCellDialogListener activity = (EditCellDialogListener) getActivity();
                activity.onFinishEditingListener(editPosition, "-1", "-1", false);
            }
        });

        return builder.create();
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
    public void onNothingSelected(AdapterView<?> parent) {}

}
