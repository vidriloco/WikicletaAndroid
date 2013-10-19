package org.wikicleta.routing;

import java.util.HashMap;
import org.interfaces.RemoteFetchingDutyListener;
import org.wikicleta.R;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.RankedComment;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others.Cruds;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;

public class RankedComments {

	protected String postPath="/api/ranked_comments";

	public class Post extends AsyncTask<RankedComment, Void, Boolean> {
		private RankedComment comment;
		public Cruds mode = Cruds.CREATE;
		AlertDialog dialog;
		RemoteFetchingDutyListener listener;
		
	    public Post(RemoteFetchingDutyListener listener) {
	    	this.listener = listener;
	    }
		
		@Override
		protected Boolean doInBackground(RankedComment... args) {
			comment = args[0];
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			return (NetworkOperations.postJSONTo(postPath, comment.toJSON(auth)) == 200);
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = DialogBuilder.buildLoadingDialogWithMessage((Activity) listener, R.string.uploading).create();
			dialog.show();
		}
		
	    protected void onPostExecute(Boolean success) {
	    	dialog.dismiss();
	    	if(success) {
				dialog.dismiss();
				listener.onFinished(comment);
	    	} else {
	    		listener.onFailed();
	    	}
	    }

	     
	 }

}
