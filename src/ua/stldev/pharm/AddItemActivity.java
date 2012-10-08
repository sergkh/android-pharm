package ua.stldev.pharm;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class AddItemActivity extends Activity {

	protected DatabaseWrapper db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new DatabaseWrapper(this);

		setContentView(R.layout.activity_add_item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_view_drugs, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.back_menu_item:
			startActivity(new Intent(this, ItemsSearchActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void add(View view) {

		EditText titleEdit = (EditText) findViewById(R.id.titleEdit);
		String title = titleEdit.getText().toString();

		EditText groupsEdit = (EditText) findViewById(R.id.groupEdit);
		String groups = groupsEdit.getText().toString();

		EditText shelfEdit = (EditText) findViewById(R.id.shelfEdit);
		String shelf = shelfEdit.getText().toString();

		db.addItem(title, groups, shelf);
		
		titleEdit.setText("");
		groupsEdit.setText("");
		shelfEdit.setText("");
		titleEdit.requestFocus();
	}

	@Override
	protected void onPause() {
		db.close();
		super.onPause();
	}
}
