package ua.stldev.pharm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ItemsSearchActivity extends Activity implements
		OnItemLongClickListener {

	private static final int DIALOG_VIEW_ITEM = 0;

	protected EditText searchText;
	protected DatabaseWrapper db;
	protected Cursor cursor;
	protected SimpleCursorAdapter adapter;
	protected ListView itemsList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_items);

		db = new DatabaseWrapper(this);

		searchText = (EditText) findViewById(R.id.searchField);
		itemsList = (ListView) findViewById(R.id.foundItemsList);
		itemsList.setOnItemLongClickListener(this);
		
		searchText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				search(searchText);
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_search_items, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_menu_item:
			startActivity(new Intent(this, AddItemActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void search(View view) {
		String search = searchText.getText().toString().trim().toLowerCase();

		cursor = db.getReadableDatabase()
			.rawQuery("SELECT _id, title, groups, shelf FROM items WHERE lower(title) || ' ' || lower(groups) LIKE ?",
					new String[] { "%" + search + "%" });

		adapter = new SimpleCursorAdapter(this, R.layout.drug_list_item,
				cursor, new String[] { "title", "groups", "shelf" }, new int[] {
						R.id.titleTw, R.id.groupTw, R.id.shelfTw });

		itemsList.setAdapter(adapter);
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Cursor c = (Cursor) adapter.getItem(position);

		Bundle b = new Bundle();
		b.putLong(DatabaseWrapper.COL_ID, 
				c.getLong(c.getColumnIndex(DatabaseWrapper.COL_ID)));
		
		b.putString(DatabaseWrapper.COL_TITLE,
				c.getString(c.getColumnIndex(DatabaseWrapper.COL_TITLE)));
		b.putString(DatabaseWrapper.COL_GROUPS,
				c.getString(c.getColumnIndex(DatabaseWrapper.COL_GROUPS)));
		b.putString(DatabaseWrapper.COL_SHELF,
				c.getString(c.getColumnIndex(DatabaseWrapper.COL_SHELF)));

		return showDialog(DIALOG_VIEW_ITEM, b);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DIALOG_VIEW_ITEM:
			Log.i("Search", "Creating info dialog");

			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.item_details_dailog);
			
			Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
			
			cancelBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.cancel();
				}
			});
			
			return dialog;

		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	protected void onPrepareDialog(int id, final Dialog dialog, final Bundle args) {
		super.onPrepareDialog(id, dialog, args);

		switch (id) {
		case DIALOG_VIEW_ITEM:
			Log.i("Search", "Updating info dialog with data");

			dialog.setTitle(args.getString(DatabaseWrapper.COL_TITLE));
			
			TextView groupsTv = (TextView) dialog
					.findViewById(R.id.dlgGroupsTv);
			groupsTv.setText(args.getString(DatabaseWrapper.COL_GROUPS));

			TextView shelfTv = (TextView) dialog.findViewById(R.id.dlgShelfTv);
			shelfTv.setText(args.getString(DatabaseWrapper.COL_SHELF));

			Button detailsBtn = (Button) dialog.findViewById(R.id.removeItemBtn);
			detailsBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					long id = args.getLong(DatabaseWrapper.COL_ID);
					Log.i("Search", "Deleting item: " + id);
					db.removeItem(id);
					dialog.hide();
					search(null);
				}
			});
		
		default:
			super.onPrepareDialog(id, dialog, args);
		}
	}

	@Override
	protected void onPause() {
		db.close();
		super.onPause();
	}

}
