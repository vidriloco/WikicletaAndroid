package org.wikicleta.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.interfaces.RemoteFetchingDutyListener;
import org.interfaces.RemoteModelInterface;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.RankedComment;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others.Cruds;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;

public class RankedComments {

	protected String postPath="/api/ranked_comments";
	protected String getPath="/api/ranked_comments/list/:object_id/:object_type";
	
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
	
	public class Get extends AsyncTask<RemoteModelInterface, Void, Boolean> {
    	
		RemoteFetchingDutyListener listener;

	    JSONArray objectList;
	    HashMap<String, String> viewport;
	   
	    public Get(RemoteFetchingDutyListener listener) {
	    	this.listener = listener;
	    }
	    
		@Override
		protected Boolean doInBackground(RemoteModelInterface... args) {
			RemoteModelInterface model = args[0];
			
			String item = getPath.replaceFirst(":object_id", String.valueOf(model.getRemoteId())).replaceFirst(":object_type", model.getKind());
			Log.e("WIKICLETA", item);
			String fetchedString = NetworkOperations.getJSONExpectingString(item, false);
			if(fetchedString == null)
				return false;
			
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				objectList = (JSONArray) object.get("ranked_comments");
				return true;
			} else {
				return false;
			}
		}	
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if(success) {
				ArrayList<RankedComment> commentList = new ArrayList<RankedComment>();

				@SuppressWarnings("unchecked")
				Iterator<JSONObject> iterator = (Iterator<JSONObject>) objectList.iterator();
				while(iterator.hasNext()) {
					JSONObject json = iterator.next();
					commentList.add(RankedComment.buildFrom(json));
				}
				listener.onFinished(commentList);
			}
		}
		
		@Override
		protected void onCancelled() {
			listener.onFailed();
		}

	}

}
