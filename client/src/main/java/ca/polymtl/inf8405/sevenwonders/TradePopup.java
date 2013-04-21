package ca.polymtl.inf8405.sevenwonders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.polymtl.inf8405.sevenwonders.api.Card;
import ca.polymtl.inf8405.sevenwonders.api.NeighborReference;
import ca.polymtl.inf8405.sevenwonders.api.Resource;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class TradePopup extends DialogFragment{
	private List<Map<Resource,List<NeighborReference>>> trades_;
	private Card card_;
	private boolean wonder_;

	public static TradePopup newInstance(Card card, Set<Map<Resource,List<NeighborReference>>> trades, boolean wonder){
		TradePopup popup = new TradePopup();
		// Convert a Set trades to List
		List<Map<Resource,List<NeighborReference>>> tradeList = new ArrayList<Map<Resource,List<NeighborReference>>>(trades.size());
		for ( Map<Resource,List<NeighborReference>> trade: trades){
			tradeList.add(trade);
		}
		popup.trades_ = tradeList;
		popup.card_ = card;
		popup.wonder_ = wonder;
		return popup;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// List of all choices for trace - Received from server
		String[] traceChoices = new String[trades_.size()];

		/// WTF ??? Any idea to refactor theses lines??? n^3....
		for (int i = 0 ; i < trades_.size(); i++){
			traceChoices[i] = "";
			Map<Resource,List<NeighborReference>> trade = trades_.get(i);
			for( Map.Entry<Resource,List<NeighborReference>> entry : trade.entrySet() ) {
				for ( NeighborReference neighbor: entry.getValue()){
					traceChoices[i] += "1 " + entry.getKey() + " from " + neighbor + "; ";
				}
			}
		}

		builder.setTitle("Trade choices")
		.setItems(traceChoices, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int selectedItemId) {
				GameScreenActivity.play(card_, trades_.get(selectedItemId), wonder_);
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
