package ca.polymtl.inf8405.sevenwonders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class TradePopup extends DialogFragment{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// List of all choices for trace - Received from server
		String[] traceChoices = new String[20];

		for (int i = 0 ; i < 20; i++){
			traceChoices[i] = "JR: 1 pierre,1 tapis,1 pierre,1 ore,1 pierre - 9 golds\n" +
					"Duc: 1 papier,1 pierre,1 ore,1 pierre,1 ore - 10 golds";
		}

		builder.setTitle("Trade choices")
		.setItems(traceChoices, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int selectedItemId) {

			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
			}
		});

		// Create the AlertDialog object and return it
		AlertDialog dialog = builder.create();
		dialog.getListView().setFastScrollEnabled(true);
		return dialog;
	}

}
