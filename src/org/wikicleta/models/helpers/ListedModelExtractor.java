package org.wikicleta.models.helpers;

import org.interfaces.ListedModelInterface;
import android.content.Context;

public class ListedModelExtractor {

	public static Context context;
	
	public static String extractModelTitle(ListedModelInterface model) {
		return context.getResources().getString(model.getTitle());
	}
	
	public static String extractModelSubtitle(ListedModelInterface model) {
		if(model.getName() == null)
			return context.getResources().getString(
					context.getResources().getIdentifier(model.getSubtitle(), "string", context.getPackageName()));
		else
			return model.getName();
		
	}

	public static void setContext(Context context2) {
		context = context2;
	}
	
	
	
}
