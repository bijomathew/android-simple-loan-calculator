package ee.smkv.calc.loan;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Andrei Samkov
 */
public class StoreManager {
  SharedPreferences settings;
  static final String TAG = "StoreManager";

  public StoreManager(SharedPreferences settings) {
    this.settings = settings;
  }

  public void loadTextViews(TextView... views) {
    for (TextView view : views) {
      loadTextView(view);
    }
  }

  private void loadTextView(TextView view) {
    try {
      view.setText("" + settings.getString("" + view.getId(), ""));
    }
    catch (Exception e) {
      Log.e(TAG, e.toString());
    }
  }

  public void loadSpinners(Spinner... spinners) {
    for (Spinner spinner : spinners) {
      loadSpinner(spinner);
    }
  }

  private void loadSpinner(Spinner spinner) {
    try {
      spinner.setSelection(settings.getInt("" + spinner.getId(), 0));
    }
    catch (Exception e) {
      Log.e(TAG, e.toString());
    }
  }

  public void storeTextViews(TextView... views) {
    SharedPreferences.Editor editor = settings.edit();
    for (TextView view : views) {
      editor.putString("" + view.getId(), view.getText().toString());
    }
    editor.commit();
  }

  public void storeSpinners(Spinner... spinners) {
    SharedPreferences.Editor editor = settings.edit();
    for (Spinner spinner : spinners) {
      editor.putInt("" + spinner.getId(),
                    spinner.getSelectedItemPosition());
    }
    editor.commit();
  }
}
