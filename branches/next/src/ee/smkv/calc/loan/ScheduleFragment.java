package ee.smkv.calc.loan;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import ee.smkv.calc.loan.model.Loan;
import ee.smkv.calc.loan.model.Payment;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends SherlockFragment {

    private CreateScheduleTableTask scheduleTableTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedule, container, false);

        Loan loan = StartActivity.loan;

        scheduleTableTask = new CreateScheduleTableTask(view.getContext());

        List<Payment> payments = loan.getPayments();
        Payment[] array = new Payment[payments.size()];
        scheduleTableTask.execute(payments.toArray(array));

        return view;
    }

    @Override
    public void onDetach() {
        if (scheduleTableTask != null) {
            scheduleTableTask.cancel(true);
        }
        super.onDetach();
    }

    public class CreateScheduleTableTask extends AsyncTask<Payment, Integer, List<View>> {

        private final Context context;
        private final ProgressDialog dialog;

        public CreateScheduleTableTask(Context context) {
            this.context = context;
            dialog = new ProgressDialog(this.context);
        }


        @Override
        protected void onPreExecute() {
            this.dialog.setIndeterminate(false);
            this.dialog.setMessage("Creating payments table...");
            this.dialog.show();
        }

        @Override
        protected List<View> doInBackground(Payment... payments) {
            this.dialog.setMax(payments.length);
            List<View> list = new ArrayList<View>(payments.length + 1);

            Loan loan = StartActivity.loan;
            boolean hasAnyCommission = loan.hasAnyCommission();


            if (loan.hasDownPayment() || loan.hasDisposableCommission()) {
                View tableRow = LayoutInflater.from(context).inflate(R.layout.schedule_row, null);
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentNr), 0);
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentBalance), loan.getAmount());
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentPrincipal), loan.getDownPaymentPayment());
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentInterest), 0);
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentCommission), loan.getDisposableCommissionPayment());
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentAmount), loan.getDownPaymentPayment());
                if (!hasAnyCommission) {
                    tableRow.findViewById(R.id.schedulePaymentCommission).setVisibility(View.GONE);
                }
                list.add(tableRow);
                this.publishProgress(0);
            }

            for (Payment payment : payments) {
                View tableRow = LayoutInflater.from(context).inflate(R.layout.schedule_row, null);
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentNr), payment.getNr());
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentBalance), payment.getBalance());
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentPrincipal), payment.getPrincipal());
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentInterest), payment.getInterest());
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentCommission), payment.getCommission());
                Utils.setNumber((TextView) tableRow.findViewById(R.id.schedulePaymentAmount), payment.getAmount());
                if (!hasAnyCommission) {
                    tableRow.findViewById(R.id.schedulePaymentCommission).setVisibility(View.GONE);
                }
                list.add(tableRow);
                this.publishProgress(payment.getNr());
            }

            return list;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            this.dialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(List<View> views) {
            TableLayout table = (TableLayout) getView().findViewById(R.id.scheduleTable);
            if (!StartActivity.loan.hasAnyCommission()) {
                getView().findViewById(R.id.paymentCommissionHeader).setVisibility(View.GONE);
            }
            for (View view : views) {
                table.addView(view);
            }
            closeDialog();
            scheduleTableTask = null;
        }

        private void closeDialog() {
            try {
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
            } catch (Exception ignore) {

            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            closeDialog();
        }
    }

}
