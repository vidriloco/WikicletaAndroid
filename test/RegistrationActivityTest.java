import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.wikicleta.R;
import org.wikicleta.activities.access.RegistrationActivity;

import android.widget.ImageView;

@RunWith(RobolectricTestRunner.class)
public class RegistrationActivityTest {
	
	private int aceptButtonId;
	private RegistrationActivity activity;

	@Test
	public void RegistrationTest() throws Exception{
		aceptButtonId = R.id.save_profile;
		ImageView ivRegister = (ImageView)activity.findViewById(aceptButtonId);
		assertNotNull("Button not allowed to be null", ivRegister);
		ivRegister.performClick();
	}
	
	@Before
	  public void setup()  {
	    activity = Robolectric.buildActivity(RegistrationActivity.class)
	        .create().get();
	  }
	
}
